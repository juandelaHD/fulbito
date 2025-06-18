import Modal from "react-modal";
import { useState } from "react";
import { Field } from "@/components/tables/ManageFieldsTable";
import { useUpdateField } from "@/services/FieldServices";

type Props = {
    field: Field;
    onClose: () => void;
    onSaved: () => void;
};

export function EditTournamentModal({ field, onClose, onSaved }: Props) {
    const { mutateAsync: updateField } = useUpdateField();

    const [form, setForm] = useState({
        name: field.name,
        grassType: field.grassType,
        zone: field.zone,
        address: field.address,
        illuminated: field.lighting === "Yes",
        enabled: field.enabled,
    });

    const handleSubmit = async () => {
        const payload = new FormData();
        payload.append("field", JSON.stringify(form));
        await updateField({ id: field.id, data: payload });
        onSaved();
    };

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
                <h2 className="text-2xl font-bold">Edit Field</h2>
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
                    onChange={(e) => setForm({...form, name: e.target.value})}
                    placeholder="Name"
                />

                <label className="block">
                    <span className="text-sm font-medium">Grass Type</span>
                    <select
                        className="w-full border border-gray-500 bg-white text-black p-2 rounded mt-1"
                        value={form.grassType}
                        onChange={(e) => setForm({...form, grassType: e.target.value as typeof form.grassType})}
                    >
                        <option value="NATURAL_GRASS">Natural</option>
                        <option value="SYNTHETIC_TURF">Synthetic</option>
                        <option value="HYBRID_TURF">Hybrid</option>
                    </select>
                </label>

                <input
                    type="text"
                    className="w-full border border-gray-500 bg-white text-black p-2 rounded"
                    value={form.zone}
                    onChange={(e) => setForm({...form, zone: e.target.value})}
                    placeholder="Zone"
                />

                <input
                    type="text"
                    className="w-full border border-gray-500 bg-white text-black p-2 rounded"
                    value={form.address}
                    onChange={(e) => setForm({...form, address: e.target.value})}
                    placeholder="Address"
                />

                <label className="flex items-center space-x-2">
                    <input
                        type="checkbox"
                        checked={form.illuminated}
                        onChange={(e) => setForm({...form, illuminated: e.target.checked})}
                    />
                    <span>Illuminated</span>
                </label>

                <label className="flex items-center space-x-2">
                    <input
                        type="checkbox"
                        checked={form.enabled}
                        onChange={(e) => setForm({...form, enabled: e.target.checked})}
                    />
                    <span>Enabled</span>
                </label>
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
    );
}
