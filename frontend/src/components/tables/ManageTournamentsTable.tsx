import { ColumnDef } from "@tanstack/react-table";
import { Table } from "@/components/tables/Table";
import { useGetOwnedTournaments, useDeleteTournament } from "@/services/TournamentServices";
import { useState, useMemo } from "react";
import { DeleteTournamentConfirmationModal } from "@/components/modals/DeleteTournamentConfirmationModal";
import { EditTournamentModal } from "@/components/modals/EditTournamentModal";

type Tournament = {
  id: number;
  name: string;
  format: string;
  status: string;
  startDate: string;
  endDate: string;
  maxTeams: number;
  registrationFee?: number;
};

export function ManageTournamentsTable() {
  const { data, isLoading, isError, refetch } = useGetOwnedTournaments(); // este endpoint deberías implementarlo
  const { mutateAsync: deleteTournament } = useDeleteTournament();
  const [selectedToDelete, setSelectedToDelete] = useState<Tournament | null>(null);
  const [editingTournament, setEditingTournament] = useState<Tournament | null>(null);

  const columns: ColumnDef<Tournament>[] = useMemo(() => [
    { accessorKey: "name", header: "Name" },
    { accessorKey: "format", header: "Format" },
    { accessorKey: "status", header: "Status" },
    { accessorKey: "startDate", header: "Start Date" },
    { accessorKey: "endDate", header: "End Date" },
    { accessorKey: "maxTeams", header: "Teams" },
    { accessorKey: "registrationFee", header: "Fee" },
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
            onClick={() => setSelectedToDelete(row.original)}
            className="text-sm bg-red-600 text-white px-2 py-1 rounded hover:bg-red-700"
          >
            Delete
          </button>
        </div>
      ),
    },
  ], []);

  return (
    <>
      {isLoading && <div>Loading tournaments...</div>}
      {isError && <div className="text-red-500">Error loading tournaments</div>}
      {!isLoading && data && (
        <Table columns={columns} data={data.content} />
      )}
      {selectedToDelete && (
        <DeleteTournamentConfirmationModal
          isOpen
          tournamentName={selectedToDelete.name}
          onCancel={() => setSelectedToDelete(null)}
          onConfirm={async () => {
            await deleteTournament({ tournamentId: selectedToDelete.id, confirm: true });
            setSelectedToDelete(null);
            refetch();
          }}
        />
      )}
      {editingTournament && (
        <EditTournamentModal
          tournament={editingTournament}
          onClose={() => setEditingTournament(null)}
          onSaved={() => {
            setEditingTournament(null);
            refetch();
          }}
        />
      )}
    </>
  );
}
