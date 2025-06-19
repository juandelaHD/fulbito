import { useState } from "react"
import { CommonLayout } from "@/components/CommonLayout/CommonLayout"
import { AddTournamentModal } from "@/components/modals/AddTournamentModal"
import { useGetAvailableTournaments } from "@/services/TournamentServices"
import { useCreateTournament } from "@/services/TournamentServices"
import { TournamentFiltersContainer } from "@/components/filters/TournamentFilters"
import {
  TournamentTable,
  TournamentForTable,
} from "@/components/tables/TournamentsTable"
import { TournamentStatus } from "@/models/GetAvailableTournaments"

export const TournamentsScreen = () => {
  const [filters, setFilters] = useState({
    organizerUsername: "",
    openForRegistration: true,
  })

  const [showAddModal, setShowAddModal] = useState(false)
  const { mutateAsync: createTournament } = useCreateTournament()

  const {
    data: fetchedTournaments,
    refetch,
    isFetching,
    isError,
  } = useGetAvailableTournaments({
    organizerUsername: filters.organizerUsername || undefined,
    openForRegistration: filters.openForRegistration || undefined,
  })

  const handleSearch = async () => {
    await refetch()
  }

  const rowsForTable: TournamentForTable[] =
    fetchedTournaments?.map((t) => ({
      id: t.id,
      name: t.name,
      startDate: t.startDate,
      format: t.format,
      status: t.status as TournamentStatus,
      onRegister: t.status === "OPEN_FOR_REGISTRATION" ? () => {} : undefined,
    })) || []

  return (
    <CommonLayout>
      <div className="w-[1040px] mx-auto px-4">
        <div className="flex justify-between items-center mb-4">
          <h1 className="text-2xl font-bold">Search Available Tournaments</h1>
          <button
            type="button"
            onClick={() => setShowAddModal(true)}
            className="bg-green-600 text-white px-4 py-2 rounded-xl"
          >
            Create Tournament 🏆
          </button>
        </div>

        <TournamentFiltersContainer
          filters={filters}
          setFilters={setFilters}
          onSearch={handleSearch}
        />

        {isError && (
          <p className="text-sm text-red-500 mt-4">
            ❌ Error loading tournaments. Please try again.
          </p>
        )}

        {isFetching && (
          <p className="text-sm text-gray-500 mt-4">Loading tournaments...</p>
        )}

        {!isFetching && !isError && rowsForTable.length === 0 && (
          <p className="text-sm text-gray-500 mt-4">
            No tournaments found matching your criteria.
          </p>
        )}

        {!isFetching && !isError && rowsForTable.length > 0 && (
          <TournamentTable data={rowsForTable} />
        )}
      </div>

      <AddTournamentModal
        isOpen={showAddModal}
        onClose={() => setShowAddModal(false)}
        onSubmit={async (data) => {
          await createTournament(data)
          setShowAddModal(false)
          await refetch()
        }}
      />
    </CommonLayout>
  )
}
