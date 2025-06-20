import { useState } from "react";
import { useLocation } from "wouter";
import { useGetTeams } from "@/services/TeamServices.ts";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";

type Team = { id: string; name: string };

export const CreateClosedMatchScreen = () => {
  const { data: teams } = useGetTeams();
  const [homeTeam, setHomeTeam] = useState<string>("");
  const [awayTeam, setAwayTeam] = useState<string>("");
  const [, navigate] = useLocation();

  const filteredAwayTeams = teams?.filter((t: Team) => t.id !== homeTeam);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!homeTeam || !awayTeam || homeTeam === awayTeam) return;
    navigate(`/match/create?homeTeamId=${homeTeam}&awayTeamId=${awayTeam}&defaultMatchType=CLOSED`);
  };

  return (
    <CommonLayout>
      <section>
        <h1 className="text-center text-2xl font-semibold mb-4">Selecciona los equipos</h1>
        <form onSubmit={handleSubmit} className="flex gap-4 mb-6 justify-center">
          <div>
            <label className="block mb-1 font-medium">Home Team</label>
            <select
              className="border rounded p-2 w-48"
              value={homeTeam}
              onChange={(e) => setHomeTeam(e.target.value)}
              required
            >
              <option value="">Select a team</option>
              {teams?.map((team: Team) => (
                <option key={team.id} value={team.id}>
                  {team.name}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block mb-1 font-medium">Away Team</label>
            <select
              className="border rounded p-2 w-48"
              value={awayTeam}
              onChange={(e) => setAwayTeam(e.target.value)}
              required
              disabled={!homeTeam}
            >
              <option value="">Select a team</option>
              {filteredAwayTeams?.map((team: Team) => (
                <option key={team.id} value={team.id}>
                  {team.name}
                </option>
              ))}
            </select>
          </div>
          <button
            type="submit"
            className="h-12 px-6 bg-blue-600 text-white rounded-lg font-bold disabled:bg-gray-400"
            disabled={!homeTeam || !awayTeam || homeTeam === awayTeam}
          >
            Siguiente
          </button>
        </form>
      </section>
    </CommonLayout>
  );
};