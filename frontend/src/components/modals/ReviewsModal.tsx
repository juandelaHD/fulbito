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
          backgroundColor: "#0f1e11", // mismo verde oscuro que tu app
          color: "#f0f0f0",
          maxWidth: "900px",
          width: "fit-content",
          maxHeight: "80vh",
          overflowY: "auto",
          margin: "auto",
          borderRadius: "12px",
          padding: "24px",
          border: "1px solid #3a4d39",
        }
      }}
    >
      <h2 className="text-2xl font-bold mb-4">{fieldName ? `Reviews for ${fieldName}` : "Reviews"}</h2>
      <ReviewsTable />
      <div className="mt-6 flex justify-end">
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