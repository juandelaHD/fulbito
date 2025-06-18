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
    setForm(prev => ({
      ...prev,
      [name]: type === "number" ? Number(value) : value,
    }));
  };

  const handleDaysChange = (day: string) => {
    setForm(prev => ({
      ...prev,
      daysOfWeek: prev.daysOfWeek.includes(day)
        ? prev.daysOfWeek.filter(d => d !== day)
        : [...prev.daysOfWeek, day],
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const parsed = CreateFieldScheduleRequestSchema.safeParse(form);
    if (!parsed.success) {
      toast.error("Datos inválidos. Revisa el formulario.");
      return;
    }
    setLoading(true);
    try {
      await createFieldScheduleService(fieldId, parsed.data, token);
      toast.success("Schedule creado correctamente");
      onClose();
    } catch {
      toast.error("Error al crear el schedule");
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
          zIndex: 1000,
        },
        content: {
          background: "#fff",
          color: "#222",
          maxWidth: "400px",
          margin: "auto",
          borderRadius: "12px",
          padding: "24px",
          border: "1px solid #ccc",
        },
      }}
    >
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-bold">Crear Schedule</h2>
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
          <label className="block font-medium">Fecha inicio</label>
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
          <label className="block font-medium">Fecha fin</label>
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
          <label className="block font-medium">Hora apertura</label>
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
          <label className="block font-medium">Hora cierre</label>
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
          <label className="block font-medium">Duración slot (minutos)</label>
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
          <label className="block font-medium">Duración break (minutos)</label>
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
          <label className="block font-medium">Días de la semana</label>
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
        <div className="flex justify-end gap-2 mt-4">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 rounded bg-gray-300 hover:bg-gray-400"
            disabled={loading}
          >
            Cancelar
          </button>
          <button
            type="submit"
            className="px-4 py-2 rounded bg-blue-600 text-white hover:bg-blue-700"
            disabled={loading}
          >
            {loading ? "Creando..." : "Crear"}
          </button>
        </div>
      </form>
    </Modal>
  );
}