import { Field } from "@/components/tables/FieldsTable"
import Modal from "react-modal"
import styles from "./AddTournamentModal.module.css"

type Props = {
  isOpen: boolean
  onClose: () => void
  field: Field | null
}

export const FieldDetailsModal = ({ isOpen, onClose, field }: Props) => {
  if (!field) return null;

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onClose}
      shouldCloseOnOverlayClick
      contentLabel="Field Details"
      style={{
        overlay: {
          backgroundColor: "rgba(0, 0, 0, 0.5)",
          backdropFilter: "blur(6px)",
          zIndex: 1000,
        },
        content: {
          top: "5vh",
          bottom: "auto",
          left: "50%",
          right: "auto",
          transform: "translateX(-50%)",
          padding: 0,
          border: "none",
          background: "none",
          overflow: "visible",
          maxHeight: "90vh",
        },
      }}
    >
      <div className={styles.modalContainer}>
        <button
          type="button"
          className={styles.closeButton}
          onClick={onClose}
          aria-label="Close"
        >
          ✖
        </button>

        <h2 className={styles.modalTitle}>
          Field Details
        </h2>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>Name</label>
          <div className={styles.input}>{field.name}</div>
        </div>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>Zone</label>
          <div className={styles.input}>{field.zone}</div>
        </div>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>Address</label>
          <div className={styles.input}>{field.address}</div>
        </div>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>Grass Type</label>
          <div className={styles.input}>{field.grassType}</div>
        </div>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>Illuminated</label>
          <div className={styles.input}>{field.lighting === "true" ? "Yes" : "No"}</div>
        </div>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>Photos</label>
          <div className="flex flex-wrap gap-2">
            {(field.allImagesUrls ?? []).map((url, idx) => (
              <img
                key={idx}
                src={url}
                alt={`field-img-${idx}`}
                className="w-[120px] h-[90px] object-cover rounded border border-white/20"
              />
            ))}
          </div>
        </div>
      </div>
    </Modal>
  )
}
