import { useEffect, useState } from "react"
import Modal from "react-modal"
import styles from "./FieldDetailsModal.module.css"
import { Field } from "@/components/tables/FieldsTable"
import { getFieldSchedulesService, ScheduleSlot } from "@/services/FieldServices"
import { FieldDetailsSchedulesTable } from "@/components/tables/FieldDetailsSchedulesTable"
import { useToken } from "@/services/TokenContext";

type Props = {
  isOpen: boolean
  onClose: () => void
  field: Field
}

export const FieldDetailsModal = ({ isOpen, onClose, field }: Props) => {
  const [tokenState] = useToken()
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : ""
  const [schedules, setSchedules] = useState<ScheduleSlot[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!isOpen) return

    const fetchSchedules = async () => {
      try {
        setLoading(true)
        const today = new Date().toISOString().split("T")[0]
        const result = await getFieldSchedulesService(field.id, today, token)
        setSchedules(result)
      } catch (err) {
        console.error("Error fetching schedules:", err)
      } finally {
        setLoading(false)
      }
    }

    fetchSchedules()
  }, [isOpen, field.id, token])

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
          width: "min(90vw, 1000px)",
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

        <h2 className={styles.modalTitle}>🏟️ {field.name}</h2>

        <div className={styles.detailsGrid}>
          <div><strong>Address:</strong> {field.address}</div>
          <div><strong>Zone:</strong> {field.zone}</div>
          <div><strong>Grass Type:</strong> {field.grassType}</div>
          <div><strong>Lighting:</strong> {field.lighting}</div>
          {field.matchesWithMissingPlayers && Object.keys(field.matchesWithMissingPlayers).length > 0 && (
            <div>
              <strong>Open Matches:</strong>{" "}
              {Object.entries(field.matchesWithMissingPlayers)
                .map(([k, v]) => `${k}: ${v}`)
                .join(", ")}
            </div>
          )}
        </div>

        <h3 className={styles.sectionTitle}>📅 Schedules for Today</h3>

        {loading ? (
          <p className={styles.loadingText}>Loading schedules...</p>
        ) : (
          <FieldDetailsSchedulesTable schedules={schedules} />
        )}
      </div>
    </Modal>
  )
}
