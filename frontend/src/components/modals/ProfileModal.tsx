import Modal from "react-modal";
import { useImageById } from "@/services/ImageServices.ts";

interface Organizer {
  firstName: string;
  lastName: string;
  username: string;
  avatarUrl?: string;
  age: number;
  gender: string;
  zone: string;
}

interface Props {
  isOpen: boolean;
  onClose: () => void;
  user: Organizer;
}

export function OrganizerProfileModal({ isOpen, onClose, user }: Props) {
  const userAvatarURL= useImageById(user.avatarUrl);

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onClose}
      shouldCloseOnOverlayClick
      contentLabel="Organizer Profile"
      style={{
        overlay: {
          backgroundColor: "rgba(0,0,0,0.6)",
          zIndex: 1000,
        },
        content: {
          top: "10vh",
          left: "50%",
          right: "auto",
          bottom: "auto",
          transform: "translateX(-50%)",
          background: "#181818",
          border: "none",
          borderRadius: "16px",
          padding: 0,
          minWidth: "350px",
          maxWidth: "90vw",
        },
      }}
    >
      <div style={{ padding: "2rem", color: "white" }}>
        <button
          onClick={onClose}
          style={{
            position: "absolute",
            top: 16,
            right: 16,
            background: "transparent",
            border: "none",
            color: "#fff",
            fontSize: 24,
            cursor: "pointer",
          }}
        >
          ✖
        </button>
        <div style={{ display: "flex", gap: "2rem", alignItems: "center" }}>
          {!userAvatarURL ? (
            <div
              style={{
                width: "150px",
                height: "150px",
                borderRadius: "9999px",
                background: "#222",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                border: "3px solid #0f0"
              }}
            >
              <span className="dot-typing">
                <span></span>
                <span></span>
                <span></span>
              </span>
            </div>
          ) : (
            <img
              src={userAvatarURL}
              alt="Avatar"
              style={{
                width: "150px",
                height: "150px",
                borderRadius: "9999px",
                objectFit: "cover",
                border: "3px solid #0f0"
              }}
            />
          )}
          <div>
            <h1 style={{ fontSize: "2.5rem", fontWeight: "bold", color: "#00ff84", marginBottom: "0.5rem" }}>
              {user.firstName} {user.lastName}
            </h1>
            <p style={{ marginBottom: "0.2rem" }}>@{user.username}</p>
            <p style={{ marginBottom: "0.2rem" }}>Age: {user.age} | Gender: {user.gender}</p>
            <p style={{ marginBottom: "0.2rem" }}>Zone: {user.zone}</p>
          </div>
        </div>
      </div>
    </Modal>
  );
}