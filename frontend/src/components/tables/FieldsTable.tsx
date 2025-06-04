import { ColumnDef } from "@tanstack/react-table"
import { Table } from "@/components/tables/Table"

export type Field = {
  id: number
  name: string
  grassType: string
  lighting: string
  zone: string
  address: string
  photos: string
}

type FieldsTableProps = {
  data: Field[]
  onReserve: (field: Field) => void
}

export function FieldsTable({ data, onReserve }: FieldsTableProps) {
  const columns: ColumnDef<Field>[] = [
    {
      accessorKey: "name",
      header: "Name",
    },
    {
      accessorKey: "grassType",
      header: "Grass Type",
    },
    {
      accessorKey: "lighting",
      header: "Lighting",
    },
    {
      accessorKey: "zone",
      header: "Zone",
    },
    {
      accessorKey: "address",
      header: "Address",
    },
    {
      id: "actions",
      header: "Actions",
      cell: ({ row }) => (
        <button
          onClick={(e) => {
            e.stopPropagation()
            onReserve(row.original)
          }}
          className="text-sm text-white bg-green-600 px-2 py-1 rounded hover:bg-green-700"
        >
          Make a Reservation
        </button>
      ),
    },
    {
      id: "photo",
      header: "View Photos",
      cell: ({ row }) => (
        <div className="flex justify-center">
          <a
            href={row.original.photos}
            target="_blank"
            rel="noopener noreferrer"
            className="text-xl hover:scale-110 transition-transform"
          >
            🖼️
          </a>
        </div>
      ),
    },
  ]

  return <Table columns={columns} data={data} />
}