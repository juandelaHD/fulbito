import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { useGetMyTeams } from "@/services/TeamServices";
import { TeamsTable } from "@/components/tables/TeamsTable";
import { useLocation } from "wouter";


// TODO: LA TABLA DE EQUIPOS DEBE ESTAR EN LA PANTALLA DEL PERFIL DE USUARIO, NO AQUÍ
export const TeamsScreen = () => {
  const { data, isLoading, error } = useGetMyTeams();
  const [, navigate] = useLocation();

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
        <h1 className="text-2xl font-semibold mb-4">Teams</h1>
        {isLoading && <p>Loading teams...</p>}
        {error && <p className="text-red-500">Error while loading teams</p>}
        {Array.isArray(data) && <TeamsTable data={data} />}
      </section>
    </CommonLayout>
  );
};