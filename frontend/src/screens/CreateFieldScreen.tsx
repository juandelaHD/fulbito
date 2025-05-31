import { useAppForm } from "@/config/use-app-form";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { CreateFieldSchema } from "@/models/CreateField";
import { toast } from "react-hot-toast";
import {useCreateField} from "@/services/FieldServices.ts";

const fieldLabels: Record<string, string> = {
  name: "Name",
  grassType: "Grass Type",
  illuminated: "Lighting",
  zone: "Zone",
  address: "Address",
};

export const CreateFieldScreen = () => {
  const { mutate } = useCreateField();

  const formData = useAppForm({
    defaultValues: {
      name: "",
      grassType: "",
      illuminated: "",
      zone: "",
      address: "",
      photos: null as FileList | null,
    },
    validators: {
      onSubmit: () => {
        const values = formData.store.state.values;
        const result = CreateFieldSchema.safeParse(values);
        if (!result.success) {
          const errors = result.error.flatten().fieldErrors as Record<string, string[]>;
          const firstErrorKey = Object.keys(errors)[0];
          const message = errors[firstErrorKey]?.[0];
          if (message) {
            const label = fieldLabels[firstErrorKey] ?? firstErrorKey;
            toast.error(`${label}: ${message}`, { duration: 5000 });
          }
          return { isValid: false };
        }
      },
    },
    onSubmit: async ({ value }) => {
      const result = CreateFieldSchema.safeParse(value);
      if (!result.success) {
        return;
      }

      const payload = {
        name: result.data.name,
        grassType: result.data.grassType,
        illuminated: result.data.illuminated,
        zone: result.data.zone,
        address: result.data.address,
        photos: value.photos ? Array.from(value.photos) : [],
      };

      mutate(payload);
    },
  });

  return (
    <CommonLayout>
      <section>
        <h1 className="text-center text-2xl font-semibold mb-4">Create a New Field</h1>
        <formData.AppForm>
          <formData.FormContainer extraError={null} className="space-y-4" submitLabel="Create Field">
            <formData.AppField name="name">
              {(field) => <field.TextField label="Name" />}
            </formData.AppField>
            <formData.AppField name="grassType">
              {(field) => (
                <field.SelectField
                  label="Grass Type"
                  options={[
                    { label: "Select...", value: "" },
                    { label: "Synthetic", value: "SYNTHETIC_TURF" },
                    { label: "Natural", value: "NATURAL_GRASS" },
                    { label: "Hybrid", value: "HYBRID_TURF" },
                  ]}
                />
              )}
            </formData.AppField>
            <formData.AppField name="illuminated">
              {(field) => (
                <field.SelectField
                  label="Lighting"
                  options={[
                    { label: "Select...", value: "" },
                    { label: "Yes", value: "Yes" },
                    { label: "No", value: "No" },
                  ]}
                />
              )}
            </formData.AppField>
            <formData.AppField name="zone">
              {(field) => <field.TextField label="Zone" />}
            </formData.AppField>
            <formData.AppField name="address">
              {(field) => <field.TextField label="Address" />}
            </formData.AppField>
            <formData.AppField name="photos">
              {(field) => (
                <div className="flex flex-col gap-1 mt-4 mb-4">
                  <label className="text-sm font-medium text-green-900">Field Picture (optional)</label>
                  <input
                    type="file"
                    accept="image/*"
                    multiple
                    onChange={(e) => field.handleChange(e.target.files)}
                    className="w-full px-4 py-[0.6rem] rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-green-500 text-black bg-white"
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
