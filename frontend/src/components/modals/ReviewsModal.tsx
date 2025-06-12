import { useState } from "react"
import Modal from "react-modal"
import { useQueryClient } from "@tanstack/react-query"
import { ReviewsTable } from "@/components/tables/ReviewsTable"
import { AddReviewModal } from "@/components/modals/AddReviewModal"
import { useCreateReview, useGetReviews } from "@/services/ReviewServices"

type Props = {
  isOpen: boolean
  onClose: () => void
  fieldName?: string
  fieldId: number
}

export const ReviewsModal = ({ isOpen, onClose, fieldName, fieldId }: Props) => {
  const [showAdd, setShowAdd] = useState(false)
  const queryClient = useQueryClient()
  const { mutate } = useCreateReview(fieldId)
  const { data: reviews, isLoading, isError } = useGetReviews(fieldId)

  return (
    <>
      <Modal
        isOpen={isOpen}
        onRequestClose={onClose}
        shouldCloseOnOverlayClick={true}
        style={{
          overlay: {
            backgroundColor: "rgba(0,0,0,0.4)",
            backdropFilter: "blur(6px)",
            zIndex: 1000,
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
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-2xl font-bold">
            {fieldName ? `Reviews for ${fieldName}` : "Reviews"}
          </h2>
          <button
            onClick={onClose}
            className="text-white hover:text-green-400 transition"
            aria-label="Close modal"
          >
            <span className="text-2xl leading-none font-bold">✖</span>
          </button>
        </div>

        {isLoading && <p>Loading...</p>}
        {isError && <p>Error loading reviews.</p>}
        {!isLoading && !isError && reviews && (
          <ReviewsTable reviews={reviews.content} />
        )}

        <div className="mt-4 flex justify-end gap-2">
          <button
            onClick={() => setShowAdd(true)}
            className="bg-blue-600 text-white px-4 py-2 rounded"
          >
            Add Review
          </button>
          <button
            onClick={onClose}
            className="bg-green-700 text-white px-4 py-2 rounded"
          >
            Close
          </button>
        </div>
      </Modal>

      <AddReviewModal
        isOpen={showAdd}
        onClose={() => setShowAdd(false)}
        onSubmit={(data) => {
          mutate(
            {
              rating: Number(data.rating), 
              comment: data.comment,
            },
            {
              onSuccess: () => {
                setShowAdd(false) 
                queryClient.invalidateQueries({ queryKey: ["reviews", fieldId] })
              },
            }
          )
        }}
      />
    </>
  )
}
