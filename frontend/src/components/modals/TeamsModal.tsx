import { RawTeamDTO } from "@/services/UserServices.ts";
import Modal from "react-modal";

type TeamsModalProps = {
  team: RawTeamDTO;
  onClose: () => void;
  isOpen: boolean;
};

export function TeamsModal({ team, onClose, isOpen }: TeamsModalProps) {

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onClose}
      contentLabel="Team Details"
      style={{
        overlay: {
          backgroundColor: "rgba(0,0,0,0.4)",
          zIndex: 1100,
        },
        content: {
          backgroundColor: "#fff",
          color: "#222",
          maxWidth: "400px",
          margin: "auto",
          borderRadius: "12px",
          padding: "24px",
          border: "1px solid #ccc",
          position: "relative",
        },
      }}
    >
      <button
        onClick={onClose}
        style={{
          position: "absolute",
          top: 12,
          right: 12,
          fontSize: 24,
          color: "#6b7280",
          background: "none",
          border: "none",
          cursor: "pointer",
          zIndex: 10,
        }}
        aria-label="Close modal"
      >
        ×
      </button>
      <div className="flex items-center gap-4 mb-4">
        <div>
          <h2 className="text-xl font-bold">{team.name}</h2>
            <div className="flex items-center gap-2 mt-1">
              <span
                className="color-circle"
                style={{ "--circle-color": team.mainColor || "#cccccc" } as React.CSSProperties}
                title={team.mainColor || "Sin color"}
              />
              <span
                className="color-circle"
                style={{ "--circle-color": team.secondaryColor || "#cccccc" } as React.CSSProperties}
                title={team.secondaryColor || "Sin color"}
              />
            </div>
          </div>
      </div>
      <p className="mb-2">
        <span className="font-semibold">Ranking:</span> {team.ranking}
      </p>
      <p className="mb-2">
        <span className="font-semibold">Captain:</span>{" "}
        {`${team.captain.firstName} ${team.captain.lastName}`.toUpperCase()}
      </p>
      <div>
        <span className="font-semibold">Members:</span>
        <ul className="mt-1">
          {team.members.map((member, idx) => (
            <li key={member.id}>
              <div className="flex items-center gap-2 py-1">
                <span>{member.firstName} {member.lastName}</span>
                <span className="text-xs text-gray-500">- ({member.username})</span>
              </div>
              {idx < team.members.length - 1 && (
                <hr className="border-t border-gray-200" />
              )}
            </li>
          ))}
        </ul>
      </div>
    </Modal>
  );
}