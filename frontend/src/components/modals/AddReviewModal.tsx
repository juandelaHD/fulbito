import { useState } from "react"
import Modal from "react-modal"
import { z } from "zod"

const schema = z.object({
  rating: z.string().min(1, "Rating is required"),
  comment: z.string().min(1, "Comment is required").max(100),
})

type Props = {
  isOpen: boolean
  onClose: () => void
  onSubmit: (data: { rating: string; comment: string }) => void
}

export const AddReviewModal = ({ isOpen, onClose, onSubmit }: Props) => {
  const [rating, setRating] = useState("")
  const [comment, setComment] = useState("")
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)

    const result = schema.safeParse({ rating, comment })
    if (!result.success) {
      setError(result.error.issues[0].message)
      return
    }

    try {
      setSubmitting(true)
      await onSubmit({ rating, comment })
      setRating("")
      setComment("")
      onClose()
    } catch (err) {
      console.error(err)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onClose}
      contentLabel="Add Review"
      shouldCloseOnOverlayClick
      style={{
        overlay: {
          backgroundColor: "rgba(0,0,0,0.4)",
          backdropFilter: "blur(6px)",
          zIndex: 1100,
        },
        content: {
          backgroundColor: "#0f1e11",
          color: "#f0f0f0",
          maxWidth: "fit-content",
          width: "fit-content",
          maxHeight: "fit-content",
          margin: "auto",
          borderRadius: "12px",
          padding: "24px",
          border: "1px solid #3a4d39",
          position: "relative",
        },
      }}
    >
      <h2 className="text-2xl font-bold mb-4">Add Review</h2>

      <form onSubmit={handleSubmit} className="flex flex-col gap-4 w-80">
        <label className="flex flex-col">
          <span className="mb-1">Rating</span>
          <select
            value={rating}
            onChange={(e) => setRating(e.target.value)}
            className="px-2 py-1 rounded bg-white text-black"
          >
            <option value="">Select rating</option>
            {[1, 2, 3, 4, 5].map((r) => (
              <option key={r} value={r}>
                {"⭐".repeat(r)}
              </option>
            ))}
          </select>
        </label>

        <label className="flex flex-col mt-2">
          <span className="mb-1">Comment</span>
          <textarea
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            className="px-3 py-2 rounded bg-white text-black font-sans text-sm leading-snug shadow-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            rows={4}
            maxLength={100}
            placeholder="Write your comment..."
          />
        </label>

        {error && <p className="text-red-400 text-sm">{error}</p>}

        <button
          type="submit"
          disabled={submitting}
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
        >
          {submitting ? "Submitting..." : "Submit Review"}
        </button>
      </form>
    </Modal>
  )
}
