import { useAppForm } from "@/config/use-app-form";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { toast } from "react-hot-toast";
import { CreateMatchSchema } from "@/models/CreateMatch";
import { useCreateMatch, useAvailableFields } from "@/services/MatchServices";
import "react-datepicker/dist/react-datepicker.css";
import DatePicker from "react-datepicker";
import { format } from "date-fns"; // Agregá esto arriba si no lo tenés

// TODO: Reemplazá esto por una forma real de obtener el usuario autenticado
const organizerId = 1;

const matchLabels: Record<string, string> = {
  matchType: "Match Type",
  fieldId: "Field",
  minPlayers: "Min Players",
  maxPlayers: "Max Players",
  date: "Date",
  startTime: "Start Time",
  endTime: "End Time",
};

export const MatchScreen = () => {
  const { mutate } = useCreateMatch();
  const { fields, loadingFields } = useAvailableFields();

  const formData = useAppForm({
    defaultValues: {
      matchType: "OPEN",
      fieldId: "",
      minPlayers: 1,
      maxPlayers: 10,
      date: new Date(),
      startTime: new Date(),
      endTime: new Date(),
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

        const payload = {
          ...result.data,
          organizerId,
          fieldId: parseInt(result.data.fieldId, 10),
          date: format(result.data.date, "yyyy-MM-dd"),
          startTime: format(result.data.startTime, "yyyy-MM-dd'T'HH:mm:ss"),
          endTime: format(result.data.endTime, "yyyy-MM-dd'T'HH:mm:ss"),
        };

        mutate(payload);
      },
  });

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
                      ? [{ label: "Loading fields...", value: "" }]
                      : fields.map((f) => ({
                        label: f.name,
                        value: f.id.toString(),
                      }))
                  }
                />
              )}
            </formData.AppField>

            {/* Min / Max Players */}
            <formData.AppField name="minPlayers">
              {(field) => <field.TextField label="Min Players" type="number" />}
            </formData.AppField>
            <formData.AppField name="maxPlayers">
              {(field) => <field.TextField label="Max Players" type="number" />}
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

            {/* Start Time */}
            <formData.AppField name="startTime">
              {(field) => (
                <div>
                  <label className="block mb-1 font-medium">Start Time</label>
                  <DatePicker
                    selected={field.state.value}
                    onChange={(val) => field.setValue(val as Date)}
                    showTimeSelect
                    showTimeSelectOnly
                    timeIntervals={15}
                    timeCaption="Start"
                    dateFormat="HH:mm"
                    className="border rounded p-2 w-full"
                  />
                </div>
              )}
            </formData.AppField>

            {/* End Time */}
            <formData.AppField name="endTime">
              {(field) => (
                <div>
                  <label className="block mb-1 font-medium">End Time</label>
                  <DatePicker
                    selected={field.state.value}
                    onChange={(val) => field.setValue(val as Date)}
                    showTimeSelect
                    showTimeSelectOnly
                    timeIntervals={15}
                    timeCaption="End"
                    dateFormat="HH:mm"
                    className="border rounded p-2 w-full"
                  />
                </div>
              )}
            </formData.AppField>

          </formData.FormContainer>
        </formData.AppForm>
      </section>
    </CommonLayout>
  );
};
