import { ColumnDef } from "@tanstack/react-table";
import { Table } from "@/components/tables/Table";
import { RawMatchDTO } from "@/services/UserServices.ts";
import { useStartMatch, useFinishMatch, useCancelMatch, useConfirmMatch } from "@/services/MatchesServices";
import { useState } from "react";
import { OrganizerProfileModal } from "@/components/modals/ProfileModal.tsx";

type Props = {
  matches: RawMatchDTO[];
  columns: ColumnDef<RawMatchDTO, any>[];
  onSetResult?: (match: RawMatchDTO) => void;
  refetch?: () => void;
};

export function AdminDashboardTable({ matches, columns, onSetResult, refetch }: Props) {
  const startMatch = useStartMatch();
  const finishMatch = useFinishMatch();
  const confirmMatch = useConfirmMatch();
  const cancelMatch = useCancelMatch();
  const [loadingId, setLoadingId] = useState<number | null>(null);
  const [selectedOrganizer, setSelectedOrganizer] = useState<RawMatchDTO["organizer"] | null>(null);
  // Modifica la columna de organizer para que sea clickeable
  const columnsWithOrganizerModal: ColumnDef<RawMatchDTO, any>[] = columns.map(col =>
    "accessorKey" in col && col.accessorKey === "organizer"
      ? {
        ...col,
        cell: ({ row }) => (
          <span
            style={{ color: "#00ff84", cursor: "pointer", textDecoration: "underline" }}
            onClick={() => row.original.organizer && setSelectedOrganizer(row.original.organizer)}
          >
          {row.original.organizer?.firstName ?? "N/A"} {row.original.organizer?.lastName ?? ""}
        </span>
        ),
      }
      : col
  );

  // Columna de acciones
  const actionColumn: ColumnDef<RawMatchDTO, any> = {
    id: "actions",
    header: "Acciones",
    cell: ({ row }) => {
      const match = row.original;
      if (match.status === "PENDING") {
        return (
          <div className="flex gap-2 justify-center">
            <button
              className="px-2 py-1 bg-blue-600 text-white rounded"
              disabled={loadingId === match.id}
              onClick={async () => {
                setLoadingId(match.id);
                await confirmMatch.mutateAsync(match.id);
                setLoadingId(null);
                refetch?.();
              }}
            >
              CONFIRM
            </button>
            <button
              className="px-2 py-1 bg-red-600 text-white rounded"
              disabled={loadingId === match.id}
              onClick={async () => {
                setLoadingId(match.id);
                await cancelMatch.mutateAsync(match.id);
                setLoadingId(null);
                refetch?.();
              }}
            >
              CANCEL
            </button>
          </div>
        );
      }
      if (match.status === "SCHEDULED") {
        return (
          <div className="flex justify-center">
            <button
              className="px-2 py-1 bg-blue-600 text-white rounded"
              disabled={loadingId === match.id}
              onClick={async () => {
                setLoadingId(match.id);
                await startMatch.mutateAsync(match.id);
                setLoadingId(null);
                refetch?.();
              }}
            >
              START
            </button>
          </div>
        );
      }
      if (match.status === "IN_PROGRESS") {
        return (
          <div className="flex justify-center">
            <button
              className="px-2 py-1 bg-green-600 text-white rounded"
              disabled={loadingId === match.id}
              onClick={async () => {
                setLoadingId(match.id);
                await finishMatch.mutateAsync(match.id);
                setLoadingId(null);
                refetch?.();
              }}
            >
              FINISH
            </button>
          </div>
        );
      }
      if (match.status === "FINISHED") {
        return (
          <div className="flex justify-center">
            <button
              className="px-2 py-1 bg-yellow-600 text-white rounded"
              onClick={() => onSetResult && onSetResult(match)}
            >
              Set Result
            </button>
          </div>
        );
      }
      return null;
    },
  };

  if (!matches || matches.length === 0) return <div>No matches found</div>;

  return (
    <>
      <Table columns={[...columnsWithOrganizerModal, actionColumn]} data={matches} />
      {selectedOrganizer && (
        <OrganizerProfileModal
          isOpen={!!selectedOrganizer}
          onClose={() => setSelectedOrganizer(null)}
          user={selectedOrganizer}
        />
      )}
    </>
  );
}