import { ColumnDef } from "@tanstack/react-table"
import { Table } from "@/components/tables/Table"
import { useImageById } from "@/services/ImageServices"

export type Field = {
  id: number
  name: string
  grassType: string
  lighting: string
  zone: string
  address: string
  imageUrl?: string
  allImagesUrls?: string[]
  matchesWithMissingPlayers?: Record<string, number> | null
}

type FieldsTableProps = {
  data: Field[]
  onReserve: (field: Field) => void
  onViewReviews: (field: Field) => void
  onViewMatchNeeds: (field: Field) => void
}

export function FieldsTable({ data, onReserve , onViewReviews, onViewMatchNeeds }: FieldsTableProps) {
  const columns: ColumnDef<Field>[] = [
    { accessorKey: "name", header: "Name" },
    { accessorKey: "grassType", header: "Grass Type" },
    { accessorKey: "lighting", header: "Lighting" },
    { accessorKey: "zone", header: "Zone" },
    { accessorKey: "address", header: "Address" },
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
      id: "reviews",
      header: "Reviews",
      cell: ({ row }) => (
        <button
        onClick={(e) => {
          e.stopPropagation();
          onViewReviews(row.original)
        }}
          className="text-sm text-white bg-blue-600 px-2 py-1 rounded hover:bg-blue-700"
        >
          Reviews
        </button>
      ),
    },
    {
      id: "image",
      header: "Image",
      cell: ({ row }) => {
        const imageEndpoint = row.original.imageUrl
        const imageUrl = useImageById(imageEndpoint)

        return (
          <div className="w-[120px] h-[100px] overflow-hidden rounded bg-black/10 flex items-center justify-center">
            {imageUrl ? (
              <img
                src={imageUrl}
                alt={row.original.name}
                className="w-full h-full object-cover block"
              />
            ) : (
              <span className="text-xs">🖼️</span>
            )}
          </div>
        )
      },
    },
    {
      id: "matchNeeds",
      header: "Open Matches",
      cell: ({row}) => {
        const matchMap = row.original.matchesWithMissingPlayers;
        return matchMap && Object.keys(matchMap).length > 0 ? (
            <button
                onClick={(e) => {
                  e.stopPropagation();
                  onViewMatchNeeds(row.original);
                }}
                className="text-sm text-white bg-yellow-600 px-2 py-1 rounded hover:bg-yellow-700"
            >
              View Matches
            </button>
        ) : "[Use 'Has Open Match' Filter]";
      }
    }
  ]

  return (
    <>
      <Table columns={columns} data={data} />
    </>
  )
}
