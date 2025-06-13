import { ColumnDef } from "@tanstack/react-table"
import { Table } from "@/components/tables/Table"

/*
export interface RawBasicMatchDTO {
  matchId: number;
  matchType: string;
  matchStatus: string;
  date: string;
  startTime: string;
  endTime: string;
  fieldName: string;
  result: string;
  players: RawPlayerDTO[];
}
*/
export type MyMatch = {
  matchId: number
  matchType: string
  matchStatus: string
  fieldName: string
  date: string
  startTime: string
  endTime: string
  result: string
}

type MyMatchesTableProps = {
  data: MyMatch[]
}

export function MyMatchesHistoryTable({ data  }: MyMatchesTableProps) {
  const columns: ColumnDef<MyMatch>[] = [
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
      id: "matchType",
      header: "Match Type",
      cell: ({ row }) => row.original.matchType,
    },
    {
      id: "result",
      header: "Result",
      cell: ({ row }) => row.original.result || "Pending",
    },
  ]

  return <Table columns={columns} data={data} />
}