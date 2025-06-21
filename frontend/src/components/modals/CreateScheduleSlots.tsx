import Modal from "react-modal";
import { useState } from "react";
import { CreateFieldScheduleRequest, CreateFieldScheduleRequestSchema } from "@/models/CreateFieldScheduleRequest";
import { createFieldScheduleService } from "@/services/FieldServices";
import { useToken } from "@/services/TokenContext";
import { toast } from "react-hot-toast";

type Props = {
  isOpen: boolean;
  onClose: () => void;
  fieldId: number;
};

const daysOfWeekList = [
  "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
];

export function CreateScheduleSlotsModal({ isOpen, onClose, fieldId }: Props) {
  const [form, setForm] = useState<CreateFieldScheduleRequest>({
    startDate: "",
    endDate: "",
    openingTime: "",
    closingTime: "",
    slotDurationMinutes: 60,
    breakDurationMinutes: 0,
    daysOfWeek: [],
  });
  const [loading, setLoading] = useState(false);
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    console.log("Handling change for:", name, "Value:", value, "Type:", type);
    setForm(prev => ({
      ...prev,
      [name]: type === "number" ? Number(value) : value,
    }));
  };

  const handleDaysChange = (day: string) => {
    console.log("Handling change for:", day);
    setForm(prev => ({
      ...prev,
      daysOfWeek: prev.daysOfWeek.includes(day)
          ? prev.daysOfWeek.filter(d => d !== day)
          : [...prev.daysOfWeek, day],
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    // Justo antes de enviar:
    const cleanedForm = {
      ...form,
      slotDurationMinutes: Number(form.slotDurationMinutes) || 0,
      breakDurationMinutes: Number(form.breakDurationMinutes) || 0,
      daysOfWeek: form.daysOfWeek.length > 0 ? form.daysOfWeek : [],
    };

    console.log("Datos enviados:", cleanedForm);
    const parsed = CreateFieldScheduleRequestSchema.safeParse(form);
    if (!parsed.success) {
      toast.error("Invalid form data. Check your inputs.");
      return;
    }
    setLoading(true);
    try {
      console.log("AAAAAAA")
      console.log(parsed.data)
      await createFieldScheduleService(fieldId, parsed.data, token);
      toast.success("Schedule created successfully!");
      onClose();
    } catch {
      toast.error("Error creating schedule. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
      <Modal
          isOpen={isOpen}
          onRequestClose={onClose}
          shouldCloseOnOverlayClick={true}
          ariaHideApp={false}
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
              borderRadius: "12px",
              padding: "24px",
              border: "1px solid #3a4d39",
              position: "fixed",
              transform: "translate(-50%, -50%)",
              top: "50%",
              left: "50%",
              margin: 0,
            },
          }}
      >
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-bold">Crear a new schedule!</h2>
          <button
              onClick={onClose}
              className="text-black hover:text-red-500 transition"
              aria-label="Cerrar modal"
          >
            <span className="text-2xl leading-none font-bold">✖</span>
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="block font-medium">Start date</label>
            <input
                type="date"
                name="startDate"
                value={form.startDate}
                onChange={handleChange}
                className="border rounded p-2 w-full"
                required
            />
          </div>
          <div>
            <label className="block font-medium">End date</label>
            <input
                type="date"
                name="endDate"
                value={form.endDate}
                onChange={handleChange}
                className="border rounded p-2 w-full"
                required
            />
          </div>
          <div>
            <label className="block font-medium">Opening time</label>
            <input
                type="time"
                name="openingTime"
                value={form.openingTime}
                onChange={handleChange}
                className="border rounded p-2 w-full"
                required
            />
          </div>
          <div>
            <label className="block font-medium">Closing time</label>
            <input
                type="time"
                name="closingTime"
                value={form.closingTime}
                onChange={handleChange}
                className="border rounded p-2 w-full"
                required
            />
          </div>
          <div>
            <label className="block font-medium">Slot duration (minutes)</label>
            <input
                type="number"
                name="slotDurationMinutes"
                value={form.slotDurationMinutes}
                onChange={handleChange}
                className="border rounded p-2 w-full"
                min={1}
                required
            />
          </div>
          <div>
            <label className="block font-medium">Break duration (minutes)</label>
            <input
                type="number"
                name="breakDurationMinutes"
                value={form.breakDurationMinutes}
                onChange={handleChange}
                className="border rounded p-2 w-full"
                min={0}
                required
            />
          </div>
          <div>
            <label className="block font-medium">Days of the week</label>
            <div className="flex flex-wrap gap-2">
              {daysOfWeekList.map(day => (
                  <label key={day} className="flex items-center gap-1">
                    <input
                        type="checkbox"
                        checked={form.daysOfWeek.includes(day)}
                        onChange={() => handleDaysChange(day)}
                    />
                    {day}
                  </label>
              ))}
            </div>
          </div>
          <div className="flex justify-center gap-4 mt-4">
            <button
                type="button"
                onClick={onClose}
                className="px-4 py-2 rounded bg-gray-300 hover:bg-gray-400"
                disabled={loading}
            >
              Cancel
            </button>
            <button
                type="submit"
                className="px-4 py-2 rounded bg-blue-600 text-white hover:bg-blue-700"
                disabled={loading}
            >
              {loading ? "Creating..." : "Create"}
            </button>
          </div>
        </form>
      </Modal>
  );
}