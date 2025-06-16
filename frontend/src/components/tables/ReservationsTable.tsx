import { ColumnDef } from "@tanstack/react-table"
import { Table } from "@/components/tables/Table"
import { RawMatchDTO } from "@/services/UserServices.ts";

type MyReservationTableProps = {
  data: RawMatchDTO[]
  onGetInviteLink: (id: number) => void
  onCancel: (matchId: number) => void
  cancelId: number | null
  onFormTeams: (matchId: number) => void
}

export function MyReservationsTable({ data, onGetInviteLink, onCancel, cancelId, onFormTeams }: MyReservationTableProps) {
  const columns: ColumnDef<RawMatchDTO>[] = [
    {
      id: "fieldName",
      header: "Field",
      cell: ({ row }) => row.original.field.name,
    },
    {
      id: "fieldLocation",
      header: "Field Location",
      cell: ({ row }) => {
        const location = row.original.field.location
        return location ? `${location.zone} - ${location.address}` : "N/A"
      },
    },
    {
      id: "date",
      header: "Date",
      cell: ({ row }) => row.original.date,
    },
    {
      id: "hour",
      header: "Hour",
      cell: ({ row }) => (
        <>{row.original.startTime} – {row.original.endTime}</>
      ),
    },
    {
      id: "matchType",
      header: "Match Type",
      cell: ({ row }) => row.original.matchType,
    },
    {
      id: "status",
      header: "Status",
      cell: ({ row }) => {
        const status = row.original.status;
        return (
          <span
            className="text-sm"
            style={{
              color:
                status === "ACCEPTED"
                  ? "#16a34a" // verde-600
                  : status === "PENDING"
                    ? "#ca8a04" // amarillo-600
                    : "#dc2626", // rojo-600
            }}
          >
        {status}
        </span>
        );
      },
    },
    {
      id: "Max/Min Players",
      header: "Min/Max Players",
      cell: ({ row }) => {
        const match = row.original;
        return (
          <span className="text-sm">
            {match.minPlayers} - {match.maxPlayers}
          </span>
        );
      },
    },
    {
      id: "inviteLink",
      header: "Invite Link",
      cell: ({ row }) => {
        const match = row.original;
        const canShowButton =
          match.matchType === "OPEN" &&
          match.status === "ACCEPTED" &&
          Array.isArray(match.players) &&
          match.players.length <= match.maxPlayers;

        return canShowButton ? (
          <button
            onClick={(e) => {
              e.stopPropagation();
              onGetInviteLink(match.id);
            }}
            className="text-sm text-white px-2 py-1 rounded bg-blue-600 hover:bg-blue-700 transition-all"
          >
            Get Link
          </button>
        ) : (
          <span className="text-red-400 text-sm">Not Allowed</span>
        );
      },
    },
    {
      id: "formTeams",
      header: "Form Teams",
      cell: ({ row }) => {
        const match = row.original;
        const canFormTeams =
          match.matchType === "OPEN" &&
          Array.isArray(match.players) &&
          match.players.length >= match.minPlayers &&
          match.status === "ACCEPTED";

        if (!canFormTeams) {
          return <span className="text-red-400 text-sm">Not Allowed</span>;
        }

        return (
          <button
            onClick={(e) => {
              e.stopPropagation();
              onFormTeams(match.id);
            }}
            className="text-sm text-white px-2 py-1 rounded bg-purple-600 hover:bg-purple-700 transition-all"
          >
            Form Teams
          </button>
        );
      },
    },
    {
      id: "Cancel",
      header: "Cancel",
      cell: ({ row }) => {
        const match = row.original;
        const canShowButton = match.status === "ACCEPTED" || match.status === "PENDING" || match.status === "SCHEDULED";
        if (!canShowButton) {
          return <span className="text-red-400 text-sm">Not Allowed</span>;
        }

        return (
          <button
            onClick={(e) => {
              e.stopPropagation();
              onCancel(match.id);
            }}
            disabled={cancelId === match.id}
            className={`text-sm text-white px-2 py-1 rounded transition-all
          bg-red-600 hover:bg-red-700
          ${cancelId === match.id ? "opacity-60 cursor-wait" : ""}`
            }
          >
            {cancelId === match.id ? "Cancelling…" : "Cancel"}
          </button>
        );
      },
    },
  ]

  return <Table columns={columns} data={data} />
}