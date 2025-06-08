import { useAppForm } from "@/config/use-app-form";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { CreateMatchSchema } from "@/models/CreateMatch";
import { useCreateMatch } from "@/services/MatchesServices";
import { useAvailableFields } from "@/services/FieldServices";
import { getFieldSchedulesService, ScheduleSlot } from "@/services/FieldServices";
import "react-datepicker/dist/react-datepicker.css";
import { toast } from "react-hot-toast";
import DatePicker from "react-datepicker";
import { format } from "date-fns";
import { useState } from "react";
import { useToken } from "@/services/TokenContext.tsx";

const matchLabels: Record<string, string> = {
  matchType: "Match Type",
  fieldId: "Field",
  minPlayers: "Min Players",
  maxPlayers: "Max Players",
  date: "Date",
  startTime: "Start Time",
  endTime: "End Time",
  scheduleId: "Schedule",
};

export const MatchScreen = () => {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";
  const { mutateAsync } = useCreateMatch();
  const { fields, loadingFields } = useAvailableFields(token);
  const [schedules, setSchedules] = useState<ScheduleSlot[]>([]);
  const [loadingSchedules, setLoadingSchedules] = useState(false);
  const [schedulesFetched, setSchedulesFetched] = useState(false);
  console.log("fields:", fields);

  const formData = useAppForm({
    defaultValues: {
      matchType: "OPEN",
      fieldId: "",
      minPlayers: 1,
      maxPlayers: 10,
      date: new Date(),
      startTime: new Date(),
      endTime: new Date(),
      scheduleId: "", // Nuevo campo para el horario seleccionado
    },
    validators: {
      onSubmit: () => {
        const values = formData.store.state.values;
        const result = CreateMatchSchema.safeParse(values);
        if (!result.success) {
          const errors = result.error.flatten().fieldErrors as Record<string, string[]>;
          const firstErrorKey = Object.keys(errors)[0];
          const message = errors[firstErrorKey]?.[0];
          if (message) {
            const label = matchLabels[firstErrorKey] ?? firstErrorKey;
            toast.error(`${label}: ${message}`, { duration: 5000 });
          }
          return { isValid: false };
        }
      },
    },
    onSubmit: async ({ value }) => {
      const result = CreateMatchSchema.safeParse(value);
      if (!result.success) return;

      const fieldId = parseInt(result.data.fieldId, 10);

      if (isNaN(fieldId)) {
        toast.error("Please select a field.", { duration: 5000 });
        return;
      }
      // Si se seleccionó un horario, usarlo para startTime y endTime
      let startTime = value.startTime;
      let endTime = value.endTime;
      if (value.scheduleId && schedules.length > 0) {
        const selected = schedules.find(s => s.id.toString() === value.scheduleId);
        if (selected) {
          const dateStr = format(value.date, "yyyy-MM-dd");
          startTime = new Date(`${dateStr}T${selected.start}:00`);
          endTime = new Date(`${dateStr}T${selected.end}:00`);
        }
      }

      const payload = {
        ...result.data,
        fieldId,
        date: format(result.data.date, "yyyy-MM-dd"),
        startTime: format(startTime, "yyyy-MM-dd'T'HH:mm:ss"),
        endTime: format(endTime, "yyyy-MM-dd'T'HH:mm:ss"),
      };

      await mutateAsync(payload);
    },
  });

  const handleFetchSchedules = async () => {
    const fieldId = formData.store.state.values.fieldId;
    const date = formData.store.state.values.date;

    console.log("Fetching schedules for fieldId:", fieldId, "on date:", format(date, "yyyy-MM-dd"));
    if (!fieldId || isNaN(Number(fieldId)) || Number(fieldId) <= 0) {
      toast.error("Please select a valid field.", { duration: 5000 });
      return;
    }
    setLoadingSchedules(true);
    setSchedulesFetched(false);
    try {
      console.log("1")
      console.log("The fieldId is:", fieldId);
      const result = await getFieldSchedulesService(
        Number(fieldId),
        format(date, "yyyy-MM-dd"),
        token
      );
      console.log(result);
      console.log("2")
      setSchedules(result.filter((s: ScheduleSlot) => s.available));
      setSchedulesFetched(true);
      if (result.filter((s: ScheduleSlot) => s.available).length === 0) {
        toast("No hours available for this field on the selected date.", { icon: "ℹ️", duration: 4000 });
      }
    } catch (e) {
      console.log("Error fetching schedules:", e);
      toast.error("Error fetching schedules. Please try again later.", { duration: 5000 });
    } finally {
      setLoadingSchedules(false);
    }
  };

  return (
    <CommonLayout>
      <section>
        <h1 className="text-center text-2xl font-semibold mb-4">Create a New Match</h1>
        <formData.AppForm>
          <formData.FormContainer extraError={null} className="space-y-4" submitLabel="Create Match">
            {/* Match Type */}
            <formData.AppField name="matchType">
              {(field) => (
                <field.SelectField
                  label="Match Type"
                  options={[
                    { label: "Open", value: "OPEN" },
                    { label: "Close", value: "CLOSE" },
                  ]}
                />
              )}
            </formData.AppField>

            {/* Field */}
            <formData.AppField name="fieldId">
              {(field) => (
                <field.SelectField
                  label="Field"
                  options={
                    loadingFields
                      ? [{ label: "No available fields", value: "" }]
                      : [
                        { label: "Select a field", value: "" },
                        ...fields ? Object.entries(fields).map(([id, name]) => ({
                          label: name,
                          value: id.toString(),
                        })) : []
                      ]
                  }
                />
              )}
            </formData.AppField>

            {/* Date */}
            <formData.AppField name="date">
              {(field) => (
                <div>
                  <label className="block mb-1 font-medium">Date</label>
                  <DatePicker
                    selected={field.state.value}
                    onChange={(val) => field.setValue(val as Date)}
                    dateFormat="yyyy-MM-dd"
                    className="border rounded p-2 w-full"
                  />
                </div>
              )}
            </formData.AppField>

            <button
              type="button"
              onClick={handleFetchSchedules}
              disabled={loadingSchedules}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded transition"
            >
              {loadingSchedules ? "Searching..." : "Search Available Schedules"}
            </button>

            {schedulesFetched && (
              <formData.AppField name="scheduleId">
                {(field) => (
                  <field.SelectField
                    label="Horario disponible"
                    options={
                      schedules.length === 0
                        ? [{ label: "No available schedules", value: "" }]
                        : schedules.map(s => ({
                          label: `${s.start} - ${s.end}`,
                          value: s.id.toString(),
                        }))
                    }
                  />
                )}
              </formData.AppField>
            )}

            {/* Min / Max Players */}
            <formData.AppField name="minPlayers">
              {(field) => (
                <field.TextField
                  label="Min Players"
                  type="number"
                  value={field.state.value}
                  onChange={(e) => field.setValue(Number(e.target.value))}
                />
              )}
            </formData.AppField>
            <formData.AppField name="maxPlayers">
              {(field) => (
                <field.TextField
                  label="Max Players"
                  type="number"
                  value={field.state.value}
                  onChange={(e) => field.setValue(Number(e.target.value))}
                />
              )}
            </formData.AppField>

          </formData.FormContainer>
        </formData.AppForm>
      </section>
    </CommonLayout>
  );
};