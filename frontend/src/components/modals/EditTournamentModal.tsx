import Modal from "react-modal"
import { useState } from "react"
import { OwnedTournament } from "@/models/GetOwnedTournaments"
import { useUpdateTournament } from "@/services/TournamentServices"

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

  const handleSubmit = async () => {
    await updateTournament(form)
    onSaved()
  }

  return (
    <Modal
      isOpen={true}
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
        <h2 className="text-2xl font-bold">Edit Tournament</h2>
        <button
          onClick={onClose}
          className="text-white hover:text-green-400 transition"
          aria-label="Close modal"
        >
          <span className="text-2xl leading-none font-bold">✖</span>
        </button>
      </div>

      <form className="space-y-3">
        <input
          type="text"
          className="w-full border border-gray-500 bg-white text-black p-2 rounded"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
          placeholder="Tournament Name"
        />

        <label className="block">
          <span className="text-sm font-medium">Format</span>
          <select
            className="w-full border border-gray-500 bg-white text-black p-2 rounded mt-1"
            value={form.format}
            onChange={(e) =>
              setForm({ ...form, format: e.target.value as typeof form.format })
            }
          >
            <option value="SINGLE_ELIMINATION">Single Elimination</option>
            <option value="GROUP_STAGE_WITH_ELIMINATION">Group + Elim.</option>
            <option value="ROUND_ROBIN">Round Robin</option>
          </select>
        </label>

        <input
          type="number"
          className="w-full border border-gray-500 bg-white text-black p-2 rounded"
          value={form.maxTeams}
          onChange={(e) => setForm({ ...form, maxTeams: parseInt(e.target.value, 10) })}
          placeholder="Max Teams"
        />

        <input
          type="date"
          className="w-full border border-gray-500 bg-white text-black p-2 rounded"
          value={form.startDate}
          onChange={(e) => setForm({ ...form, startDate: e.target.value })}
        />

        <input
          type="date"
          className="w-full border border-gray-500 bg-white text-black p-2 rounded"
          value={form.endDate}
          onChange={(e) => setForm({ ...form, endDate: e.target.value })}
        />

        <textarea
          className="w-full border border-gray-500 bg-white text-black p-2 rounded"
          rows={3}
          value={form.rules}
          onChange={(e) => setForm({ ...form, rules: e.target.value })}
          placeholder="Rules"
        />

        <input
          type="text"
          className="w-full border border-gray-500 bg-white text-black p-2 rounded"
          value={form.prizes}
          onChange={(e) => setForm({ ...form, prizes: e.target.value })}
          placeholder="Prizes"
        />

        <input
          type="number"
          className="w-full border border-gray-500 bg-white text-black p-2 rounded"
          value={form.registrationFee}
          onChange={(e) => setForm({ ...form, registrationFee: parseFloat(e.target.value) })}
          placeholder="Registration Fee"
        />
      </form>

      <div className="mt-4 flex justify-end space-x-2">
        <button
          onClick={onClose}
          className="px-4 py-2 text-sm rounded bg-gray-100 text-gray-800 hover:bg-gray-200 transition"
        >
          Cancel
        </button>
        <button
          onClick={handleSubmit}
          className="px-4 py-2 text-sm rounded bg-blue-600 text-white hover:bg-blue-700 transition"
        >
          Save
        </button>
      </div>
    </Modal>
  )
}
