import { useAppForm } from "@/config/use-app-form";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { CreateFieldSchema } from "@/models/CreateField";
import { toast } from "react-hot-toast";

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
      const formPayload = new FormData();

      const fieldJson = {
        name: value.name,
        grassType: value.grassType === "Synthetic" ? "SYNTHETIC" : "NATURAL_GRASS",
        illuminated: value.lighting === "Yes",
        location: {
          zone: value.zone,
          address: value.address,
        },
      };

      formPayload.append("field", JSON.stringify(fieldJson));

      if (value.photos && value.photos.length > 0) {
        Array.from(value.photos).forEach((file: File) => {
          formPayload.append("images", file);
        });
      }

      try {
        const response = await fetch("/fields", {
          method: "POST",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token") ?? ""}`,
          },
          body: formPayload,
        });

        if (!response.ok) {
          const errText = await response.text();
          throw new Error(errText || "Unknown error");
        }

        toast.success("Field created successfully!");
      } catch (error: any) {
        toast.error("Error creating field: " + error.message);
      }
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
