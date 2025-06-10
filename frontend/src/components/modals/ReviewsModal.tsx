import Modal from "react-modal"
import { ReviewsTable } from "@/components/tables/ReviewsTable"

type Props = {
  isOpen: boolean
  onClose: () => void
  fieldName?: string
}

export const ReviewsModal = ({ isOpen, onClose, fieldName }: Props) => {
  return (
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

      <ReviewsTable />

      <div className="mt-4 flex justify-end">
        <button
          onClick={onClose}
          className="px-4 py-2 bg-green-700 text-white rounded hover:bg-green-800"
        >
          Cerrar
        </button>
      </div>
    </Modal>
  )
}
