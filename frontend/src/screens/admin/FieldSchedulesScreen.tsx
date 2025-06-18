import { toast } from "react-hot-toast";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import "react-datepicker/dist/react-datepicker.css";
import DatePicker from "react-datepicker";
import { ScheduleSlot, getFieldSchedulesService, updateScheduleSlotStatusService, deleteFieldScheduleService } from "@/services/FieldServices.ts";
import { useState } from "react";
import { useToken } from "@/services/TokenContext.tsx";
import { CreateScheduleSlotsModal } from "@/components/modals/CreateScheduleSlots.tsx";
import { SchedulesSlotsTable } from "@/components/tables/SchedulesSlotsTable"

export const FieldSchedulesScreen = () => {
  // Extrae /fields/:id/schedules/:name de la URL
  const pathMatch = window.location.pathname.match(/^\/fields\/(\d+)\/schedules\/(.+)$/);
  const fieldId = pathMatch ? parseInt(pathMatch[1], 10) : null;
  const fieldName = pathMatch ? decodeURIComponent(pathMatch[2]) : "";
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [schedules, setSchedules] = useState<ScheduleSlot[]>([]);
  const [loadingSchedules, setLoadingSchedules] = useState(false);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  const handleDateChange = async (date: Date | null) => {
    setSelectedDate(date);
    setSchedules([]);
    if (date && fieldId && token) {
      setLoadingSchedules(true);
      try {
        const formattedDate = date.toISOString().split("T")[0];
        const slots = await getFieldSchedulesService(fieldId, formattedDate, token);
        setSchedules(slots);
      } catch (e) {
        setSchedules([]);
      } finally {
        setLoadingSchedules(false);
      }
    }
  };

  const handleSlotAction = async (slot: ScheduleSlot, newStatus: "AVAILABLE" | "BLOCK") => {
    if (!fieldId || !token) return;
    try {
      await updateScheduleSlotStatusService(fieldId, slot.id, newStatus, token);
      toast.success("Status updated successfully");
      // Recarga los schedules para reflejar el cambio
      if (selectedDate) {
        await handleDateChange(selectedDate);
      }
    } catch {
      toast.error("Error while updating status");
    }
  };

  const handleDeleteSlot = async (slot: ScheduleSlot) => {
    if (!fieldId || !token) return;
    try {
      await deleteFieldScheduleService(fieldId, slot.id, token);
      toast.success("Schedule deleted successfully");
      if (selectedDate) {
        await handleDateChange(selectedDate); // Refresca la tabla
      }
    } catch {
      toast.error("Error while deleting schedule");
    }
  };

  return (
    <CommonLayout>
      <div className="p-6">
        <div className="flex flex-col items-center gap-4 mb-6">
          <h1 className="text-2xl font-bold">
            Schedules Slots for {fieldName}
          </h1>
          <button
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
            onClick={() => setShowCreateModal(true)}
          >
            Create schedules
          </button>
          <div className="w-full max-w-xs">
            <label className="block mb-1 font-medium text-center">Date</label>
            <DatePicker
              selected={selectedDate}
              onChange={handleDateChange}
              dateFormat="yyyy-MM-dd"
              className="border rounded p-2 w-full"
            />
          </div>
        </div>
        <div className="mt-6">
          {loadingSchedules && <div>Loading schedules...</div>}
          {!loadingSchedules && schedules.length > 0 && (
            <SchedulesSlotsTable schedules={schedules} onAction={handleSlotAction} onDelete={handleDeleteSlot}/>
          )}
          {!loadingSchedules && selectedDate && schedules.length === 0 && (
            <div>No schedules for this day</div>
          )}
        </div>
        <CreateScheduleSlotsModal
          isOpen={showCreateModal}
          onClose={() => setShowCreateModal(false)}
          fieldId={fieldId ?? 0}
        />
      </div>
    </CommonLayout>
  );
};