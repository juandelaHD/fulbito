import { useAppForm } from "@/config/use-app-form";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { CreateFieldSchema } from "@/models/CreateField";
import { toast } from "react-hot-toast";
import { CreateButton } from "@/components/form-components/CreateButton/CreateButton.tsx";

const fieldLabels: Record<string, string> = {
  name: "Name",
  grassType: "Grass Type",
  lighting: "Lighting",
  zone: "Zone",
  address: "Address",
};

export const CreateFieldScreen = () => {
  const formData = useAppForm({
    defaultValues: {
      name: "",
      grassType: "",
      lighting: "",
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
        return { isValid: true };
      },
    },
    onSubmit: async ({ value }) => {
      console.log("Create field data:", value);
      toast.success("Field created!");
      // Llamar a tu servicio para guardar la cancha
    },
  });

  return (
    <CommonLayout>
      <section>
        <h1 className="text-center text-2xl font-semibold mb-4">Create a New Field</h1>
        <formData.AppForm>
          <formData.FormContainer extraError={null} className="space-y-4">
            <formData.AppField name="name">
              {(field) => <field.TextField label="Name" />}
            </formData.AppField>
            <formData.AppField name="grassType">
              {(field) => (
                <field.SelectField
                  label="Grass Type"
                  options={[
                    { label: "Select...", value: "" },
                    { label: "Synthetic", value: "Synthetic" },
                    { label: "Natural", value: "Natural" },
                  ]}
                />
              )}
            </formData.AppField>
            <formData.AppField name="lighting">
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
                <input
                  type="file"
                  accept="image/*"
                  multiple
                  onChange={(e) => field.handleChange(e.target.files)}
                  className="..." // <- acá falta el estilo
                />
              )}
            </formData.AppField>
            <CreateButton>Create Field</CreateButton>
          </formData.FormContainer>
        </formData.AppForm>
      </section>
    </CommonLayout>
  );
};
