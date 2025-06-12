type Review = {
  rating: number
  comment: string
  createdAt: string
  userId: number
}

type Props = {
  reviews: Review[]
}

export const ReviewsTable = ({ reviews }: Props) => {
  return (
    <table className="min-w-full text-white">
      <thead>
        <tr>
          <th className="px-4 py-2 text-left">User</th>
          <th className="px-4 py-2 text-left">Rating</th>
          <th className="px-4 py-2 text-left">Comment</th>
          <th className="px-4 py-2 text-left">Date</th>
        </tr>
      </thead>
      <tbody>
        {reviews.map((review, idx) => (
          <tr key={idx} className="border-t border-green-800">
            <td className="px-4 py-2">#{review.userId}</td>
            <td className="px-4 py-2">{"⭐".repeat(review.rating)}</td>
            <td className="px-4 py-2">{review.comment}</td>
            <td className="px-4 py-2">{new Date(review.createdAt).toLocaleDateString()}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}