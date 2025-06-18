import { useEffect, useState } from "react"
import Modal from "react-modal"
import { useGetMyTeams } from "@/services/TeamServices"
import { useRegisterTeamToTournament } from "@/services/TournamentServices"
import styles from "./AddTournamentModal.module.css"
import toast from "react-hot-toast"

interface Props {
  isOpen: boolean
  onClose: () => void
  tournamentId: number
  tournamentName: string
}

export const TournamentTeamRegistrationModal = ({ isOpen, onClose, tournamentId, tournamentName }: Props) => {
  const { data: myTeams} = useGetMyTeams({ enabled: isOpen })
  const [selectedTeamId, setSelectedTeamId] = useState<number | undefined>()
  const { mutate, isPending } = useRegisterTeamToTournament()

  useEffect(() => {
    if (isOpen && myTeams && myTeams.length === 0) {
      toast.success
      ("You have no Teams on your behalf. Create one from the Teams menu and become a legendary Team Captain!", { icon: "⚽" })
      onClose()
    }
  }, [isOpen, myTeams, onClose])

  const handleRegister = () => {
    if (!selectedTeamId) {
      toast.error("Please select a team first")
      return
    }
    mutate({ tournamentId, teamId: selectedTeamId }, {
      onSuccess: () => onClose(),
    })
  }

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onClose}
      shouldCloseOnOverlayClick
      contentLabel="Register Team"
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

        <h2 className={styles.modalTitle}>Register My Team for Tournament:</h2>
        <h2 className={styles.modalTitle}>{tournamentName} 🏆</h2>

        <div className={styles.fieldGroup}>
          <label className={styles.label}>My Teams</label>
          <select
            value={selectedTeamId ?? ""}
            onChange={(e) => setSelectedTeamId(Number(e.target.value))}
            className={styles.select}
          >
            <option value="">Select the team you want to Register</option>
            {myTeams?.map((team) => (
              <option key={team.id} value={team.id}>{team.name}</option>
            ))}
          </select>
        </div>

        <div className="w-full flex justify-end mt-2">
          <button
            type="button"
            className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 transition"
            onClick={handleRegister}
            disabled={isPending}
          >
            {isPending ? "Adding..." : "Add Team"}
          </button>
        </div>
      </div>
    </Modal>
  )
}
