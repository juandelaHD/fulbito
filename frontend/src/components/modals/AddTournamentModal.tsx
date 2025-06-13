import { useState } from "react"
import Modal from "react-modal"
import { z } from "zod"
import { TournamentFormatEnum } from "@/models/GetTournaments"

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
          backgroundColor: "rgba(0,0,0,0.5)",
          backdropFilter: "blur(5px)",
          zIndex: 1000,
        },
        content: {
          backgroundColor: "#111",
          color: "#f0f0f0",
          width: "90%",
          maxWidth: "500px",
          margin: "auto",
          padding: "24px",
          borderRadius: "12px",
          border: "1px solid #333",
        },
      }}
    >
      <h2 className="text-2xl font-semibold mb-4">Hungry for glory? Create a New Tournament! 🏆</h2>

      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <input
          name="name"
          placeholder="Tournament Name *"
          value={formState.name || ""}
          onChange={handleChange}
          className="bg-white text-black px-3 py-2 rounded shadow-sm"
        />

        <input
          name="startDate"
          type="date"
          value={formState.startDate || ""}
          onChange={handleChange}
          className="bg-white text-black px-3 py-2 rounded shadow-sm"
        />

        <select
          name="format"
          value={formState.format || ""}
          onChange={handleChange}
          className="bg-white text-black px-3 py-2 rounded shadow-sm"
        >
          <option value="">Select Format *</option>
          <option value="SINGLE_ELIMINATION">Eliminación directa</option>
          <option value="GROUPS_AND_ELIMINATION">Grupos + Eliminación</option>
          <option value="ROUND_ROBIN">Todos contra todos</option>
        </select>

        <input
          name="maxTeams"
          type="number"
          min={2}
          placeholder="Max Teams *"
          value={formState.maxTeams || ""}
          onChange={handleChange}
          className="bg-white text-black px-3 py-2 rounded shadow-sm"
        />

        <input
          name="endDate"
          type="date"
          value={formState.endDate || ""}
          onChange={handleChange}
          className="bg-white text-black px-3 py-2 rounded shadow-sm"
        />

        <textarea
          name="description"
          placeholder="Rules / Description"
          value={formState.description || ""}
          onChange={handleChange}
          rows={3}
          className="bg-white text-black px-3 py-2 rounded shadow-sm"
        />

        <input
          name="prizes"
          placeholder="Prizes"
          value={formState.prizes || ""}
          onChange={handleChange}
          className="bg-white text-black px-3 py-2 rounded shadow-sm"
        />

        <input
          name="entryFee"
          type="number"
          min={0}
          placeholder="Entry Fee"
          value={formState.entryFee ?? ""}
          onChange={handleChange}
          className="bg-white text-black px-3 py-2 rounded shadow-sm"
        />

        {error && <p className="text-red-400 text-sm">{error}</p>}

        <button
          type="submit"
          disabled={submitting}
          className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 disabled:opacity-50"
        >
          {submitting ? "Submitting..." : "Create Tournament"}
        </button>
      </form>
    </Modal>
  )
}
