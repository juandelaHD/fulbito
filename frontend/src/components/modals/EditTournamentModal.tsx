import Modal from "react-modal"
import { useState } from "react"
import { OwnedTournament } from "@/models/GetOwnedTournaments"
import { useUpdateTournament } from "@/services/TournamentServices"
import styles from "@/components/modals/AddTournamentModal.module.css"

type Props = {
  tournament: OwnedTournament
  onClose: () => void
  onSaved: () => void
}

export function EditTournamentModal({ tournament, onClose, onSaved }: Props) {
  const { mutateAsync: updateTournament } = useUpdateTournament(tournament.id)

  const [form, setForm] = useState({
    name: tournament.name,
    format: tournament.format,
    maxTeams: tournament.maxTeams,
    startDate: tournament.startDate,
    endDate: tournament.endDate,
    rules: tournament.rules,
    prizes: tournament.prizes ?? "",
    registrationFee: tournament.registrationFee ?? 0,
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
    setForm((prev) => ({
      ...prev,
      [name]: name === "maxTeams" || name === "registrationFee" ? Number(value) : value,
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    await updateTournament(form)
    onSaved()
  }

  return (
    <Modal
      isOpen
      onRequestClose={onClose}
      shouldCloseOnOverlayClick
      contentLabel="Edit Tournament"
      style={{
        overlay: {
          backgroundColor: "rgba(0,0,0,0.4)",
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
      <form onSubmit={handleSubmit} className={styles.modalContainer}>
        <button type="button" className={styles.closeButton} onClick={onClose}>
          ✖
        </button>
        <h2 className={styles.modalTitle}>Edit Tournament</h2>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>Name</label>
          <input
            name="name"
            value={form.name}
            onChange={handleChange}
            className={styles.input}
          />
        </div>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>Start Date</label>
          <input
            name="startDate"
            type="date"
            value={form.startDate}
            onChange={handleChange}
            className={styles.input}
          />
        </div>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>End Date</label>
          <input
            name="endDate"
            type="date"
            value={form.endDate}
            onChange={handleChange}
            className={styles.input}
          />
        </div>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>Format</label>
          <select
            name="format"
            value={form.format}
            onChange={handleChange}
            className={styles.select}
          >
            <option value="SINGLE_ELIMINATION">Direct Elimination</option>
            <option value="GROUP_STAGE_WITH_ELIMINATION">Group + Elimination</option>
            <option value="ROUND_ROBIN">League</option>
          </select>
        </div>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>Max Teams</label>
          <input
            name="maxTeams"
            type="number"
            value={form.maxTeams}
            onChange={handleChange}
            className={styles.input}
          />
        </div>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>Rules</label>
          <textarea
            name="rules"
            value={form.rules}
            onChange={handleChange}
            className={styles.textarea}
            rows={3}
          />
        </div>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>Prizes</label>
          <input
            name="prizes"
            value={form.prizes}
            onChange={handleChange}
            className={styles.input}
          />
        </div>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>Registration Fee</label>
          <input
            name="registrationFee"
            type="number"
            value={form.registrationFee}
            onChange={handleChange}
            className={styles.input}
          />
        </div>

        <button type="submit" className={styles.submitButton}>
          Save Changes
        </button>
      </form>
    </Modal>
  )
}
