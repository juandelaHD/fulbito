import { ColumnDef } from "@tanstack/react-table"
import { Table } from "@/components/tables/Table"

export type MyJoinedMatch = {
  matchId: number
  matchType: string
  status: string
  fieldName: string
  fieldLocation?: {
    zone: string
    address: string
  }
  date: string
  startTime: string
  endTime: string
  result: string
}


type MyMatchesTableProps = {
  data: MyJoinedMatch[]
  onGetInviteLink: (id: number) => void
  onLeave: (matchId: number) => void
  leaveId: number | null
}

export function MyJoinedMatchesTable({ data, onGetInviteLink, onLeave, leaveId }: MyMatchesTableProps) {
  const columns: ColumnDef<MyJoinedMatch>[] = [
    {
      accessorKey: "fieldName",
      header: "Field",
    },
    {
      id: "fieldLocation",
      header: "Field Location",
      cell: ({ row }) => {
        const location = row.original.fieldLocation
        return location ? `${location.zone} - ${location.address}` : "N/A"
      },
    },
    {
      accessorKey: "date",
      header: "Date",
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
      id: "inviteLink",
      header: "Invite Link",
      cell: ({ row }) => {
        const match = row.original;
        return match.matchType === "OPEN" ? (
          <button
            onClick={(e) => {
              e.stopPropagation();
              onGetInviteLink(match.matchId);
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
      id: "Leave",
      header: "Leave Match",
      cell: ({ row }) => {
        const match = row.original;
        const canShowButton = match.status === "ACCEPTED";
        if (!canShowButton) {
          return <span className="text-red-400 text-sm">Not Allowed</span>;
        }

        return (
          <button
            onClick={(e) => {
              e.stopPropagation();
              onLeave(match.matchId);
            }}
            disabled={leaveId === match.matchId}
            className={`text-sm text-white px-2 py-1 rounded transition-all
          bg-green-600 hover:bg-green-700
          ${leaveId === match.matchId ? "opacity-60 cursor-wait" : ""}`
            }
          >
            {leaveId === match.matchId ? "Leaving…" : "Leave"}
          </button>
        );
      },
    },
  ]

  return <Table columns={columns} data={data} />
}