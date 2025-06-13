import { ColumnDef } from "@tanstack/react-table"
import { Table } from "@/components/tables/Table"

export type Match = {
  id: number
  fieldName: string
  date: string
  startTime: string
  endTime: string
  registered: number
  minPlayers: number
  maxPlayers: number
}

type OpenMatchesTableProps = {
  data: Match[]
  onGetInviteLink: (id: number) => void
  onJoin: (matchId: number) => void
  joiningId: number | null
  onFormTeams: (matchId: number) => void
}

export function OpenMatchesTable({ data, onGetInviteLink, onJoin, joiningId, onFormTeams }: OpenMatchesTableProps) {
  const columns: ColumnDef<Match>[] = [
    {
      accessorKey: "fieldName",
      header: "Field",
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
      id: "registered",
      header: "Registered & Remaining",
      cell: ({ row }) => {
        const { registered, maxPlayers } = row.original
        const remaining = maxPlayers - registered
        return (
          <>{registered} / {maxPlayers} ({remaining > 0 ? `${remaining} remaining` : "Completed"})</>
        )
      },
    },
    {
      id: "signup",
      header: "Join Match",
      cell: ({ row }) => {
        const match = row.original
        const isFull = match.registered >= match.maxPlayers

        return (
          <button
            onClick={(e) => {
              e.stopPropagation()
              onJoin(match.id)
            }}
            disabled={isFull || joiningId === match.id}
            className={`text-sm text-white px-2 py-1 rounded transition-all
              ${isFull ? "bg-gray-400 cursor-not-allowed" : "bg-green-600 hover:bg-green-700"}
              ${joiningId === match.id ? "opacity-60 cursor-wait" : ""}`
            }
          >
            {joiningId === match.id
              ? "Signing up…"
              : isFull
                ? "Full"
                : "Join"
            }
          </button>
        )
      },
    },
    {
      id: "inviteLink",
      header: "Invite Link",
      cell: ({ row }) => {
        const match = row.original;
        return (
          <button
            onClick={(e) => {
              e.stopPropagation();
              onGetInviteLink(match.id);
            }}
            className="text-sm text-white px-2 py-1 rounded bg-blue-600 hover:bg-blue-700 transition-all"
          >
            Get Link
          </button>
        );
      },
    },
    {
      id: "formTeams",
      header: "Form Teams",
      cell: ({ row }) => {
        const match = row.original;
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
  ]

  return <Table columns={columns} data={data} />
}