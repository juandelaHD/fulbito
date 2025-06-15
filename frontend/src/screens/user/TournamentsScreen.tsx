import { useState } from "react";
import { toast } from "react-hot-toast";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { AddTournamentModal } from "@/components/modals/AddTournamentModal.tsx";
import { useGetTournaments } from "@/services/TournamentServices.ts";
import type { GetTournamentsRequest, Tournament } from "@/models/GetTournaments.ts";
import {
  TournamentFiltersContainer,
} from "@/components/filters/TournamentFilters.tsx";
import {
  TournamentTable,
  TournamentForTable,
} from "@/components/tables/TournamentsTable.tsx";

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

  const rowsForTable: TournamentForTable[] = tournaments.map((t) => ({
    id: t.id,
    name: t.name,
    startDate: t.startDate,
    format: t.format,
    status: t.status,
  }));

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
            Create Tournament 🏆
          </button>
        </div>

        <TournamentFiltersContainer
        filters={{
            name: filters.name ?? "",
            status: filters.status,
        }}
        setFilters={setFilters}
        onSearch={handleSearch}
        />

        {isFetching ? (
          <p className="text-sm text-gray-500">Loading tournaments...</p>
        ) : rowsForTable.length === 0 ? (
          <p className="text-sm text-gray-500">No tournaments found matching your filters.</p>
        ) : (
          <TournamentTable
            data={rowsForTable}
            onClickTournament={(t) =>
              toast(`You clicked on ${t.name}`)
            }
          />
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
