import { useLocation, useRoute } from "wouter";
import {
  useFormTeams,
  TeamFormationRequestDTO,
  useGetMatchById
} from "@/services/MatchesServices.ts";
import { toast } from "react-hot-toast";
import { useAppForm } from "@/config/use-app-form.ts";
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

  const formData = useAppForm({
    defaultValues: {
      strategy: "",
      teamA: "",
      teamB: "",
    },
    validators: {
      onSubmit: () => {
        const values = formData.store.state.values;
        const result = FormTeamsSchema.safeParse({
          strategy: values.strategy,
          teamAPlayerIds: values.teamA
            ? values.teamA.split(",").map((id: string) => Number(id.trim())).filter((id: number) => !isNaN(id))
            : undefined,
          teamBPlayerIds: values.teamB
            ? values.teamB.split(",").map((id: string) => Number(id.trim())).filter((id: number) => !isNaN(id))
            : undefined,
        });
        if (!result.success) {
          const errors = result.error.flatten().fieldErrors as Record<string, string[]>;
          const firstErrorKey = Object.keys(errors)[0];
          const message = errors[firstErrorKey]?.[0];
          if (message) {
            toast.error(message, { duration: 5000 });
          }
          return { isValid: false };
        }
      },
    },
    onSubmit: async ({ value }) => {
      const teamAIds = value.teamA
        ? value.teamA.split(",").map((id: string) => Number(id.trim())).filter((id: number) => !isNaN(id))
        : [];
      const teamBIds = value.teamB
        ? value.teamB.split(",").map((id: string) => Number(id.trim())).filter((id: number) => !isNaN(id))
        : [];

      const result = FormTeamsSchema.safeParse({
        strategy: value.strategy,
        teamAPlayerIds: teamAIds.length > 0 ? teamAIds : undefined,
        teamBPlayerIds: teamBIds.length > 0 ? teamBIds : undefined,
      });
      if (!result.success) return;

      if (!value.strategy) {
        toast.error("Please select a strategy for forming teams.", { duration: 3000 });
        return;
      }

      const payload: TeamFormationRequestDTO = {
        strategy: value.strategy,
        teamAPlayerIds: teamAIds.length > 0 ? teamAIds : undefined,
        teamBPlayerIds: teamBIds.length > 0 ? teamBIds : undefined,
      };
      await mutateAsync({ matchId: match?.id || 0, payload });
      navigate("/matches");
    },
  });

  return (
    <CommonLayout>
      <section>
      <h1 className="text-center text-2xl font-semibold mb-4">Form teams</h1>
      <p className="text-center text-gray-600 mb-6">
        Select a strategy and assign players to teams for the match!
      </p>
      <formData.AppForm>
        <formData.FormContainer extraError={null} className="space-y-4" submitLabel="Form Team" >
          <formData.AppField name="strategy">
            {(field) => (
              <field.SelectField
                label="Strategy"
                options={[{ label: "Select a strategy", value: "" }, ...STRATEGIES]}
              />
            )}
          </formData.AppField>
          <div className="space-y-4">
            <h2 className="text-lg font-semibold">Players not assigned</h2>
            <ul className="list-disc pl-6">
              {match?.players.map((player) => (
                <li key={player.id} className="text-gray-700">
                  {player.username} (ID: {player.id})
                </li>
              ))}
            </ul>
          </div>

          <formData.AppField name="teamA">
            {(field) => (
              <field.TextField
                label="Team A (IDs separated by commas)"
                value={field.state.value}
                onChange={e => field.setValue(e.target.value)}
              />
            )}
          </formData.AppField>
          <formData.AppField name="teamB">
            {(field) => (
              <field.TextField
                label="Team B (IDs separated by commas)"
                value={field.state.value}
                onChange={e => field.setValue(e.target.value)}
              />
            )}
          </formData.AppField>
        </formData.FormContainer>
      </formData.AppForm>
      </section>
    </CommonLayout>
  );
};