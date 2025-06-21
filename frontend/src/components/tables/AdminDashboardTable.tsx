import { ColumnDef } from "@tanstack/react-table";
import { Table } from "@/components/tables/Table";
import { RawMatchDTO } from "@/services/UserServices.ts";
import {
  useStartMatch,
  useFinishMatch,
  useCancelMatch,
  useConfirmMatch,
  useChangeMatchResult,
} from "@/services/MatchesServices";
import { useState } from "react";
import { OrganizerProfileModal } from "@/components/modals/ProfileModal.tsx";
import { toast } from "react-hot-toast";

type Props = {
  matches: RawMatchDTO[];
  columns: ColumnDef<RawMatchDTO, any>[];
  refetch?: () => void;
};

export function AdminDashboardTable({ matches, columns, refetch }: Props) {
  const startMatch = useStartMatch();
  const finishMatch = useFinishMatch();
  const confirmMatch = useConfirmMatch();
  const cancelMatch = useCancelMatch();
  const changeResult = useChangeMatchResult();
  const [loadingId, setLoadingId] = useState<number | null>(null);
  const [selectedOrganizer, setSelectedOrganizer] = useState<RawMatchDTO["organizer"] | null>(null);
  // Estado para los resultados de cada partido
  const [results, setResults] = useState<Record<number, { home: string; away: string }>>({});

  const handleResultChange = (matchId: number, type: "home" | "away", value: string) => {
    setResults(prev => ({
      ...prev,
      [matchId]: {
        ...prev[matchId],
        [type]: value.replace(/[^0-9]/g, ""), // Solo números
      },
    }));
  };

  const handleSubmitResult = async (match: RawMatchDTO) => {
    const home = results[match.id]?.home ?? "";
    const away = results[match.id]?.away ?? "";
    if (home === "" || away === "") {
      toast.error("Please, enter both home and away scores");
      return;
    }
    try {
      await changeResult.mutateAsync({
        matchId: match.id,
        result: `${home}-${away}`,
      });
      toast.success("Result updated successfully");
      refetch?.();
    } catch (e) {
      toast.error("Error updating result");
    }
  };

  const columnsWithOrganizerModal = columns.map(col =>
    "accessorKey" in col && col.accessorKey === "organizer"
      ? {
        ...col,
        cell: ({ row }: any) => (
          <span
            style={{ color: "#00ff84", cursor: "pointer", textDecoration: "underline" }}
            onClick={() => row.original.organizer && setSelectedOrganizer(row.original.organizer)}
          >
              {row.original.organizer?.firstName ?? "N/A"} {row.original.organizer?.lastName ?? ""}
            </span>
        ),
      } as ColumnDef<RawMatchDTO, any>
      : col
  );

  const actionColumn: ColumnDef<RawMatchDTO, any> = {
    id: "actions",
    header: "Actions",
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
        // Inicializa los valores si no existen
        if (!results[match.id] && match.result) {
          const [home, away] = match.result.split("-").map(x => x.trim());
          setResults(prev => ({
            ...prev,
            [match.id]: { home, away },
          }));
        }
        return (
          <div className="flex items-center gap-2">
            <input
              type="text"
              className="w-12 text-center border rounded p-1"
              placeholder="Home"
              value={results[match.id]?.home ?? ""}
              onChange={e => handleResultChange(match.id, "home", e.target.value)}
            />
            <span>-</span>
            <input
              type="text"
              className="w-12 text-center border rounded p-1"
              placeholder="Away"
              value={results[match.id]?.away ?? ""}
              onChange={e => handleResultChange(match.id, "away", e.target.value)}
            />
            <button
              className="px-2 py-1 bg-yellow-600 text-white rounded"
              onClick={() => handleSubmitResult(match)}
              disabled={loadingId === match.id}
            >
              Guardar
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