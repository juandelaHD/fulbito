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
  inscritos: number      // players.length
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
      header: "Cancha",
    },
    {
      accessorKey: "date",
      header: "Fecha",
    },
    {
      id: "hora",
      header: "Hora",
      // Como la hora no está en un solo campo, usamos cell() para concatenar
      cell: ({ row }) => (
        <>{row.original.startTime} – {row.original.endTime}</>
      ),
    },
    {
      id: "inscritos",
      header: "Inscritos / Faltantes",
      cell: ({ row }) => {
        const { inscritos, minPlayers } = row.original
        const faltantes = minPlayers - inscritos
        return (
          <>{inscritos} / {minPlayers} ({faltantes > 0 ? `${faltantes} faltan` : "Completado"})</>
        )
      },
    },
    {
      id: "actions",
      header: "Acción",
      cell: ({ row }) => {
        const m = row.original
        const yaLleno = m.inscritos >= m.maxPlayers

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
