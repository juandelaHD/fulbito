import { ColumnDef } from "@tanstack/react-table"
import { Table } from "@/components/tables/Table"
import { useDeleteTournament, useGetOwnedTournaments } from "@/services/TournamentServices"
import { DeleteTournamentConfirmationModal } from "@/components/modals/DeleteTournamentConfirmationModal"
import { EditTournamentModal } from "@/components/modals/EditTournamentModal"
import { useMemo, useState } from "react"
import { OwnedTournament } from "@/models/GetOwnedTournaments"


type Props = {
  tournament: OwnedTournament
  onClose: () => void
  onSaved: () => void
}

export function ManageTournamentsTable() {
  const { data, isLoading, isError, refetch } = useGetOwnedTournaments()
  const { mutateAsync: deleteTournament } = useDeleteTournament()
  const [tournamentToDelete, setTournamentToDelete] = useState<OwnedTournament | null>(null)
  const [editingTournament, setEditingTournament] = useState<OwnedTournament | null>(null)

  const columns: ColumnDef<OwnedTournament>[] = useMemo(() => [
    { accessorKey: "name", header: "Name" },
    { accessorKey: "format", header: "Format" },
    { accessorKey: "status", header: "Status" },
    { accessorKey: "startDate", header: "Start Date" },
    { accessorKey: "endDate", header: "End Date" },
    { accessorKey: "maxTeams", header: "Max Teams" },
    {
      id: "registered",
      header: "Registered",
      cell: ({ row }) => row.original.registeredTeams.length
    },
    {
      id: "actions",
      header: "Actions",
      cell: ({ row }) => (
        <div className="flex space-x-2">
          <button
            onClick={() => setEditingTournament(row.original)}
            className="text-sm bg-blue-600 text-white px-2 py-1 rounded hover:bg-blue-700"
          >
            Edit
          </button>
          <button
            onClick={() => setTournamentToDelete(row.original)}
            className="text-sm bg-red-600 text-white px-2 py-1 rounded hover:bg-red-700"
          >
            Delete
          </button>
        </div>
      ),
    },
  ], [])

  return (
    <>
      {isLoading && <div>Loading tournaments...</div>}
      {isError && <div className="text-red-500">Error loading tournaments</div>}
      {!isLoading && data && (
        <Table columns={columns} data={data} />
      )}

      {tournamentToDelete && (
        <DeleteTournamentConfirmationModal
          isOpen
          tournamentName={tournamentToDelete.name}
          onCancel={() => setTournamentToDelete(null)}
          onConfirm={async () => {
            await deleteTournament({ tournamentId: tournamentToDelete.id, confirm: true })
            setTournamentToDelete(null)
            refetch()
          }}
        />
      )}

      {editingTournament && (
        <EditTournamentModal
          tournament={editingTournament}
          onClose={() => setEditingTournament(null)}
          onSaved={() => {
            setEditingTournament(null)
            refetch()
          }}
        />
      )}
    </>
  )
}
