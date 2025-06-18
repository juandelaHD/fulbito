import Modal from "react-modal";

type Props = {
    isOpen: boolean;
    onConfirm: () => void;
    onCancel: () => void;
    tournamentName?: string;
};

export const DeleteTournamentConfirmationModal = ({ isOpen, onConfirm, onCancel, tournamentName }: Props) => {
    return (
        <Modal
            isOpen={isOpen}
            onRequestClose={onCancel}
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
                <h2 className="text-2xl font-bold">Are you sure?</h2>
                <button
                    onClick={onCancel}
                    className="text-white hover:text-green-400 transition"
                    aria-label="Close modal"
                >
                    <span className="text-2xl leading-none font-bold">✖</span>
                </button>
            </div>

            <p className="text-sm mb-4">
                {
                    tournamentName
                    ? `Are you sure you want to delete "${tournamentName}"?`
                    : "Are you sure you want to delete this tournament?" 
                }
            </p>

            <div className="flex justify-end space-x-3 pt-2">
                <button
                    onClick={onCancel}
                    className="px-4 py-2 text-sm rounded bg-gray-100 text-gray-800 hover:bg-gray-200 transition"
                >
                    Cancel
                </button>
                <button
                    onClick={onConfirm}
                    className="px-4 py-2 text-sm rounded bg-red-600 text-white hover:bg-red-700 transition"
                >
                    Delete
                </button>
            </div>
        </Modal>
    );
};
