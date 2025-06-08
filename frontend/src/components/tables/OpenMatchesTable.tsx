// src/components/tables/OpenMatchesTable.tsx
import { ColumnDef } from "@tanstack/react-table"
import { Table } from "@/components/tables/Table"

// 1) Definimos la interfaz Match tal como la usaremos en la tabla.
export type Match = {
  id: number
  fieldName: string      // mapearemos match.field.name
  date: string           // "YYYY-MM-DD"
  startTime: string      // "HH:mm"
  endTime: string        // "HH:mm"
  registered: number      // players.length
  minPlayers: number     // match.minPlayers
  maxPlayers: number     // match.maxPlayers
}

// 2) Props que recibe la tabla
type OpenMatchesTableProps = {
  data: Match[]
  onJoin: (matchId: number) => void
  joiningId: number | null
}

export function OpenMatchesTable({ data, onJoin, joiningId }: OpenMatchesTableProps) {
  // 3) Columnas para React Table
  const columns: ColumnDef<Match>[] = [
    {
      accessorKey: "fieldName",
      header: "FieldName",
    },
    {
      accessorKey: "date",
      header: "Date",
    },
    {
      id: "hour",
      header: "Hour",
      // Como la hora no está en un solo campo, usamos cell() para concatenar
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
      id: "actions",
      header: "Action",
      cell: ({ row }) => {
        const m = row.original
        const yaLleno = m.registered >= m.maxPlayers

        return (
          <button
            onClick={(e) => {
              e.stopPropagation()
              onJoin(m.id)
            }}
            disabled={yaLleno || joiningId === m.id}
            className={`text-sm text-white px-2 py-1 rounded transition-all
              ${yaLleno ? "bg-gray-400 cursor-not-allowed" : "bg-green-600 hover:bg-green-700"}
              ${joiningId === m.id ? "opacity-60 cursor-wait" : ""}`
            }
          >
            {joiningId === m.id
              ? "Inscribiendo…"
              : yaLleno
              ? "Lleno"
              : "Inscribirme"
            }
          </button>
        )
      },
    },
  ]

  return <Table columns={columns} data={data} />
}
