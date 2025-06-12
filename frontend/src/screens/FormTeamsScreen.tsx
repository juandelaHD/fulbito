import { useState } from "react";
import { useLocation, useRoute } from "wouter";
import {
  useFormTeams,
  TeamFormationRequestDTO,
  useGetMatchById
} from "@/services/MatchesServices";
import { toast } from "react-hot-toast";
import { useAppForm } from "@/config/use-app-form";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { FormTeamsSchema } from "@/models/FormTeamsSchema.ts";

const STRATEGIES = [
  { value: "MANUAL", label: "Manual" },
  { value: "RANDOM", label: "Random" },
  { value: "BY_AGE", label: "Por edad" },
  { value: "BY_EXPERIENCE", label: "Por experiencia" },
  { value: "BY_GENDER", label: "Por género" },
  { value: "BY_ZONE", label: "Por zona" },
];

export const FormTeamsScreen = () => {
  const [, navigate] = useLocation();
  const [, params] = useRoute("/matches/:id/teams");
  const matchId = params && typeof params.id === "string" ? Number(params.id) : undefined;
  const { data: match } = useGetMatchById(matchId);
  const { mutateAsync } = useFormTeams();

  const [teamA, setTeamA] = useState<number[]>([]);
  const [teamB, setTeamB] = useState<number[]>([]);

  const players = match?.players ?? [];
  const unassigned = players.filter((p) => !teamA.includes(p.id) && !teamB.includes(p.id));

  const formData = useAppForm({
    defaultValues: {
      strategy: "",
    },
    validators: {
      onSubmit: () => {
        const values = formData.store.state.values;
        // Construye el objeto para validar
        const toValidate = {
          ...values,
          teamAPlayerIds: teamA,
          teamBPlayerIds: teamB,
        };
        const result = FormTeamsSchema.safeParse(toValidate);
        if (!result.success) {
          const message = result.error.errors[0]?.message || "Error en el formulario";
          toast.error(message);
          return { isValid: false };
        }
        return { isValid: true };
      },
    },
    onSubmit: async ({ value }) => {
      let payload: TeamFormationRequestDTO;
      if (value.strategy === "MANUAL") {
        payload = {
          strategy: "MANUAL",
          teamAPlayerIds: teamA,
          teamBPlayerIds: teamB,
        };
      } else {
        payload = {
          strategy: value.strategy as TeamFormationRequestDTO["strategy"],
        };
      }
      await mutateAsync(
        { matchId: matchId!, payload }
      );
      navigate("/match");
    },
  });

  // Lógica para mover jugadores
  const moveToTeam = (playerId: number, team: "A" | "B") => {
    if (team === "A") {
      setTeamA([...teamA, playerId]);
      setTeamB(teamB.filter((id) => id !== playerId));
    } else {
      setTeamB([...teamB, playerId]);
      setTeamA(teamA.filter((id) => id !== playerId));
    }
  };
  const removeFromTeams = (playerId: number) => {
    setTeamA(teamA.filter((id) => id !== playerId));
    setTeamB(teamB.filter((id) => id !== playerId));
  };

  return (
    <CommonLayout>
      <section>
      <h1 className="text-center text-2xl font-semibold mb-4">Formar Equipos</h1>
      <p className="text-center text-gray-600 mb-6">
        Selecciona la estrategia para formar los equipos del partido: {match?.id || "Partido Desconocido"}.
      </p>
      <formData.AppForm>
        <formData.FormContainer extraError={null} className="space-y-4" submitLabel="Formar Equipos">
          <formData.AppField name="strategy">
            {(field) => (
              <field.SelectField
                label="Estrategia"
                options={[{ label: "Selecciona una estrategia", value: "" }, ...STRATEGIES]}
              />
            )}
          </formData.AppField>

          {formData.state.values.strategy === "MANUAL" && (
            <div className="flex flex-col gap-8 mb-4">
              {/* Jugadores no asignados arriba */}
              <div>
                <h2 className="font-semibold mb-2 text-center">Jugadores por asignar</h2>
                <ul className="flex flex-wrap justify-center gap-4">
                  {unassigned.map((p) => (
                    <li key={p.id} className="flex items-center gap-4 mb-1">
                      <span className="min-w-[100px]">{p.username}</span>
                      <div className="flex gap-2">
                        <button
                          type="button"
                          className="bg-green-500 text-white px-2 py-1 rounded"
                          onClick={() => moveToTeam(p.id, "A")}
                        >
                          + Team A
                        </button>
                        <button
                          type="button"
                          className="bg-blue-500 text-white px-2 py-1 rounded"
                          onClick={() => moveToTeam(p.id, "B")}
                        >
                          + Team B
                        </button>
                      </div>
                    </li>
                  ))}
                </ul>
              </div>
              {/* Equipos abajo, distribuidos a izquierda y derecha */}
              <div className="flex justify-between gap-8">
                {/* Team A */}
                <div className="flex-1">
                  <h2 className="font-semibold mb-2 text-center">Team A</h2>
                  <ul>
                    {teamA.map((id) => {
                      const player = players.find((p) => p.id === id);
                      return (
                        <li key={id} className="flex items-center gap-4 mb-1">
                          <span className="min-w-[100px]">{player?.username}</span>
                          <button
                            type="button"
                            className="bg-red-500 text-white px-2 py-1 rounded"
                            onClick={() => removeFromTeams(id)}
                          >
                            Quitar
                          </button>
                        </li>
                      );
                    })}
                  </ul>
                </div>
                {/* Team B */}
                <div className="flex-1">
                  <h2 className="font-semibold mb-2 text-center">Team B</h2>
                  <ul>
                    {teamB.map((id) => {
                      const player = players.find((p) => p.id === id);
                      return (
                        <li key={id} className="flex items-center gap-4 mb-1">
                          <span className="min-w-[100px]">{player?.username}</span>
                          <button
                            type="button"
                            className="bg-red-500 text-white px-2 py-1 rounded"
                            onClick={() => removeFromTeams(id)}
                          >
                            Quitar
                          </button>
                        </li>
                      );
                    })}
                  </ul>
                </div>
              </div>
            </div>
          )}
        </formData.FormContainer>
      </formData.AppForm>
      </section>
    </CommonLayout>
  );
};