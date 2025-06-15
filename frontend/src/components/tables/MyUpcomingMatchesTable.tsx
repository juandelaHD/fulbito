import { ColumnDef } from "@tanstack/react-table"
import { Table } from "@/components/tables/Table"

export type MyUpcomingMatch = {
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
  data: MyUpcomingMatch[]
}

export function MyUpcomingMatchesTable({ data }: MyMatchesTableProps) {
  const columns: ColumnDef<MyUpcomingMatch>[] = [
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
  ]
  return <Table columns={columns} data={data} />
}