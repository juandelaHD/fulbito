import { useRef, useState } from "react";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { useGetMyTeams } from "@/services/TeamServices";
import { TeamsTable } from "@/components/tables/TeamsTable";
import { useLocation } from "wouter";

export const TeamsScreen = () => {
  // Deshabilita el fetch automático
  const { data, isLoading, error, refetch, isFetching } = useGetMyTeams({ enabled: false });
  const [, navigate] = useLocation();
  const prevCount = useRef<number>(0);
  const [newTeams, setNewTeams] = useState<number>(0);
  console.log("TeamsScreen rendered");
  // Cargar equipos la primera vez manualmente
  const handleInitialLoad = async () => {
    const result = await refetch();
    if (result.data) {
      prevCount.current = result.data.length;
    }
  };

  useState(() => {
    handleInitialLoad();
  });

  // Al hacer refresh, compara la cantidad de equipos
  const handleRefresh = async () => {
    const result = await refetch();
    if (result.data) {
      const diff = result.data.length - prevCount.current;
      setNewTeams(diff > 0 ? diff : 0);
      prevCount.current = result.data.length;
    }
  };

  return (
    <CommonLayout>
      <section>
        <div className="mb-6 flex flex-col items-center">
          <span className="text-lg font-medium mb-2">Want to create a team?</span>
          <button
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
            onClick={() => navigate("/teams/create")}
          >
            Create team
          </button>
        </div>
        <div className="flex items-center gap-4 mb-4">
          <h1 className="text-2xl font-semibold">Teams</h1>
          <button
            className="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300"
            onClick={handleRefresh}
            disabled={isFetching}
          >
            Refresh
          </button>
          {newTeams > 0 && (
            <span className="text-green-600 font-medium">
              {newTeams} nuevo{newTeams > 1 ? "s" : ""} equipo{newTeams > 1 ? "s" : ""}!
            </span>
          )}
        </div>
        {isLoading && <p>Loading teams...</p>}
        {error && <p className="text-red-500">Error while loading teams</p>}
        {Array.isArray(data) && <TeamsTable data={data} />}
      </section>
    </CommonLayout>
  );
};