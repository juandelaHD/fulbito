import { ColumnDef } from "@tanstack/react-table"
import { Table } from "@/components/tables/Table"

type Review = {
  score: number
  comment: string
  user: string
  date: string
}

const mockReviews: Review[] = [
  { score: 5, comment: "Excelente cancha, súper cuidada", user: "maria_22", date: "2025-06-01" },
  { score: 4, comment: "Muy buena iluminación y césped", user: "juancho", date: "2025-05-27" },
  { score: 3, comment: "Todo bien, pero un poco caro", user: "nico_b", date: "2025-05-20" },
]

const columns: ColumnDef<Review>[] = [
  { accessorKey: "score", header: "Score" },
  { accessorKey: "comment", header: "Comment" },
  { accessorKey: "user", header: "User" },
  { accessorKey: "date", header: "Date" },
]

export const ReviewsTable = () => {
  return <Table columns={columns} data={mockReviews} />
}
