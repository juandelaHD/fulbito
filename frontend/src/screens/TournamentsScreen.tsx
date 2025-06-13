import { useState } from "react";
import { toast } from "react-hot-toast";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { AddTournamentModal } from "@/components/modals/AddTournamentModal";
import { useGetTournaments } from "@/services/TournamentServices";
import type { GetTournamentsRequest, Tournament } from "@/models/GetTournaments";

export const TournamentsScreen = () => {
  const [filters, setFilters] = useState<GetTournamentsRequest>({
    name: "",
    status: undefined,
  });
  const [showAddModal, setShowAddModal] = useState(false);

  const {
    data: fetchedTournaments,
    refetch,
    isFetching,
  } = useGetTournaments(filters);

  const handleSearch = async () => {
    await refetch();
  };

  const tournaments: Tournament[] = fetchedTournaments?.content || [];

  return (
    <CommonLayout>
      <div className="w-[1040px] mx-auto px-4">
        <div className="flex justify-between items-center mb-4">
          <h1 className="text-2xl font-bold">Search Tournaments</h1>
          <button
            type="button"
            onClick={() => setShowAddModal(true)}
            className="bg-green-600 text-white px-4 py-2 rounded-xl"
          >
            + Add Tournament 🏆
          </button>
        </div>

        {/* ACA VAN LOS FILTROS, como hiciste en FieldsFiltersContainer */}
        {/* De momento lo dejamos para después si querés separar en componente */}
        <div className="flex gap-4 mb-4">
          <input
            type="text"
            placeholder="Tournament name"
            className="px-3 py-2 rounded bg-white text-black border w-full"
            value={filters.name || ""}
            onChange={(e) => setFilters((prev) => ({ ...prev, name: e.target.value }))}
          />

          <select
            className="px-3 py-2 rounded bg-white text-black border"
            value={filters.status || ""}
            onChange={(e) =>
              setFilters((prev) => ({
                ...prev,
                status: e.target.value === "" ? undefined : (e.target.value as GetTournamentsRequest["status"]),
              }))
            }
          >
            <option value="">All</option>
            <option value="OPEN">Open</option>
            <option value="ONGOING">Ongoing</option>
            <option value="FINISHED">Finished</option>
          </select>

          <button
            onClick={handleSearch}
            className="bg-blue-600 text-white px-4 py-2 rounded-xl"
          >
            Search
          </button>
        </div>

        {isFetching ? (
          <p className="text-sm text-gray-500">Loading tournaments...</p>
        ) : tournaments.length === 0 ? (
          <p className="text-sm text-gray-500">No tournaments found matching your filters.</p>
        ) : (
          <div className="grid gap-4">
            {tournaments.map((tournament) => (
              <div key={tournament.id} className="border p-4 rounded-xl shadow">
                <h3 className="text-lg font-bold">{tournament.name}</h3>
                <p>📅 Start Date: {tournament.startDate}</p>
                <p>🏁 Format: {tournament.format}</p>
                <p>📌 Status: {tournament.status}</p>
              </div>
            ))}
          </div>
        )}
      </div>

      <AddTournamentModal
        isOpen={showAddModal}
        onClose={() => setShowAddModal(false)}
        onSubmit={async () => {
          setShowAddModal(false);
          toast.success("Tournament created!");
          await refetch();
        }}
      />
    </CommonLayout>
  );
};
