import Modal from "react-modal";
import type { Field } from "@/components/tables/FieldsTable";

type Props = {
    isOpen: boolean;
    onClose: () => void;
    field: Field | null;
};

export const ViewFieldMatchesModal = ({ isOpen, onClose, field }: Props) => {
    const formatLocalDateTime = (raw: string): string => {
        const [date, time] = raw.split("T");
        const shortTime = time.slice(0, 5);
        return `${date} [${shortTime}]`;
    };

    return (
        <Modal
            isOpen={isOpen}
            onRequestClose={onClose}
            style={{
                overlay: {
                    backgroundColor: "rgba(0,0,0,0.4)",
                    backdropFilter: "blur(6px)",
                    zIndex: 1000,
                },
                content: {
                    backgroundColor: "#0f1e11",
                    color: "#f0f0f0",
                    maxWidth: "400px",
                    margin: "auto",
                    borderRadius: "12px",
                    padding: "24px",
                    border: "1px solid #3a4d39",
                    position: "relative",
                },
            }}
        >
            <h2 className="text-xl font-semibold mb-4">
                Missing Players for {field?.name}
            </h2>

            {field?.matchesWithMissingPlayers &&
            Object.keys(field.matchesWithMissingPlayers).length > 0 ? (
                <ul className="space-y-2">
                    {Object.entries(field.matchesWithMissingPlayers)
                        .sort(([a], [b]) => new Date(a).getTime() - new Date(b).getTime())
                        .map(([datetime, missingCount]) => (
                            <li key={datetime} className="text-sm">
                                {formatLocalDateTime(datetime)} – Missing Players:{" "}
                                <span className="text-red-400 font-semibold">{missingCount}</span>
                            </li>
                        )
                    )}
                </ul>
            ) : (
                <p className="text-gray-400">No data available.</p>
            )}

            <div className="mt-6 flex justify-end">
                <button
                    onClick={onClose}
                    className="bg-green-700 text-white px-4 py-2 rounded"
                >
                    Close
                </button>
            </div>
        </Modal>
    );
};
