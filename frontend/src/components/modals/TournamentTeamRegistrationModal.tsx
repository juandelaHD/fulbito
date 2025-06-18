import { useState } from "react";
import Modal from "react-modal";
import styles from "./AddTournamentModal.module.css";
import { useGetMyTeams } from "@/services/TeamServices";
import { useRegisterTeamToTournament } from "@/services/TournamentServices";
import toast from "react-hot-toast";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  tournamentId: number;
  tournamentName: string;
}

export const TournamentTeamRegistrationModal = ({ isOpen, onClose, tournamentId, tournamentName }: Props) => {
  const { data: myTeams = [], isLoading } = useGetMyTeams();
  const [selectedTeamId, setSelectedTeamId] = useState<number | undefined>(undefined);
  const { mutateAsync: registerTeam, isPending: isSubmitting } = useRegisterTeamToTournament();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedTeamId) {
      toast.error("Please select a team to register.");
      return;
    }
    try {
      await registerTeam({ tournamentId, teamId: selectedTeamId });
      toast.success("Team registered successfully!");
      onClose();
    } catch (error) {
      console.error(error);
    }
  };

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
          top: "10vh",
          left: "50%",
          right: "auto",
          bottom: "auto",
          transform: "translateX(-50%)",
          padding: 0,
          border: "none",
          background: "none",
          maxHeight: "90vh",
        },
      }}
    >
      <form onSubmit={handleSubmit} className={styles.modalContainer}>
        <button
          type="button"
          className={styles.closeButton}
          onClick={onClose}
          aria-label="Close"
        >
          ✖
        </button>

        <h2 className={styles.modalTitle}>
          Register My Team for Tournament "{tournamentName}" 🏆
        </h2>

        <div className={styles.fieldGroup} style={{ flexDirection: "row", alignItems: "flex-end", gap: "10px" }}>
          <div style={{ flex: 1 }}>
            <label className={styles.label}>Select Team *</label>
            <select
              name="teamId"
              value={selectedTeamId ?? ""}
              onChange={(e) => setSelectedTeamId(Number(e.target.value))}
              className={styles.select}
              disabled={isLoading}
            >
              <option value="">Select your team</option>
              {myTeams.map((team) => (
                <option key={team.id} value={team.id}>
                  {team.name}
                </option>
              ))}
            </select>
          </div>

          <button
            type="submit"
            className={styles.submitButton}
            disabled={isSubmitting || isLoading || !myTeams.length}
            style={{ width: "fit-content", padding: "0.5rem 1rem" }}
          >
            {isSubmitting ? "Registering..." : "Add Team"}
          </button>
        </div>
      </form>
    </Modal>
  );
};
