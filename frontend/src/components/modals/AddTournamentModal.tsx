import { useState } from "react"
import Modal from "react-modal"
import { z } from "zod"
import { TournamentFormatEnum } from "@/models/GetTournaments"
import styles from "./AddTournamentModal.module.css"

const schema = z.object({
  name: z.string().min(1, "Tournament name is required"),
  startDate: z.string().min(1, "Start date is required"),
  format: TournamentFormatEnum,
  maxTeams: z.number().min(2, "Minimum 2 teams required"),
  endDate: z.string().optional(),
  description: z.string().optional(),
  prizes: z.string().optional(),
  entryFee: z.number().optional(),
})

type TournamentData = z.infer<typeof schema>

type Props = {
  isOpen: boolean
  onClose: () => void
  onSubmit: (data: TournamentData) => Promise<void>
}

export const AddTournamentModal = ({ isOpen, onClose, onSubmit }: Props) => {
  const [formState, setFormState] = useState<Partial<TournamentData>>({})
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
    setFormState((prev) => ({ ...prev, [name]: name === "maxTeams" || name === "entryFee" ? Number(value) : value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    const parsed = schema.safeParse(formState)
    if (!parsed.success) {
      setError(parsed.error.issues[0].message)
      return
    }

    try {
      setSubmitting(true)
      await onSubmit(parsed.data)
      setFormState({})
      onClose()
    } catch (err) {
      console.error(err)
      setError("An unexpected error occurred.")
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Modal
    isOpen={isOpen}
    onRequestClose={onClose}
    shouldCloseOnOverlayClick
    contentLabel="Add Tournament"
    style={{
        overlay: {
        backgroundColor: "rgba(0, 0, 0, 0.5)", // como otros modales
        backdropFilter: "blur(6px)",
        zIndex: 1000,
        },
        content: {
        top: "5vh",
        bottom: "auto",
        left: "50%",
        right: "auto",
        transform: "translateX(-50%)", // 👈 truco para centrar horizontal
        padding: 0,
        border: "none",
        background: "none",
        overflow: "visible",
        },
    }}
    >

        <form onSubmit={handleSubmit} className={styles.modalContainer}>
            <h2 className={styles.modalTitle}>
                Hungry for glory? Create a New Tournament! 🏆
            </h2>

            <div className={styles.fieldGroup}>
                <label className={styles.label}>Tournament Name *</label>
                <input
                name="name"
                value={formState.name || ""}
                onChange={handleChange}
                className={styles.input}
                />
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label}>Start Date *</label>
                <input
                name="startDate"
                type="date"
                value={formState.startDate || ""}
                onChange={handleChange}
                className={styles.input}
                />
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label}>Format *</label>
                <select
                name="format"
                value={formState.format || ""}
                onChange={handleChange}
                className={styles.select}
                >
                <option value="">Select Format</option>
                <option value="SINGLE_ELIMINATION">Direct Elimination</option>
                <option value="GROUPS_AND_ELIMINATION">Group Stage + Elimination</option>
                <option value="ROUND_ROBIN">League</option>
                </select>
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label}>Max Teams *</label>
                <input
                name="maxTeams"
                type="number"
                min={2}
                value={formState.maxTeams || ""}
                onChange={handleChange}
                className={styles.input}
                />
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label}>End Date</label>
                <input
                name="endDate"
                type="date"
                value={formState.endDate || ""}
                onChange={handleChange}
                className={styles.input}
                />
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label}>Rules / Description</label>
                <textarea
                name="description"
                value={formState.description || ""}
                onChange={handleChange}
                rows={3}
                className={styles.textarea}
                />
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label}>Prizes</label>
                <input
                name="prizes"
                value={formState.prizes || ""}
                onChange={handleChange}
                className={styles.input}
                />
            </div>

            <div className={styles.fieldGroup}>
                <label className={styles.label}>Entry Fee</label>
                <input
                name="entryFee"
                type="number"
                min={0}
                value={formState.entryFee ?? ""}
                onChange={handleChange}
                className={styles.input}
                />
            </div>

            {error && <p className="text-red-400 text-sm">{error}</p>}

            <button
                type="submit"
                disabled={submitting}
                className={styles.submitButton}
            >
                {submitting ? "Submitting..." : "Create Tournament"}
            </button>
        </form>
    </Modal>
  )
}
