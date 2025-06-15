import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { useAppForm } from "@/config/use-app-form.ts";
import { toast } from "react-hot-toast";
import { FileInput } from "@/components/form-components/FileInput/FileInput.tsx";
import { TeamCreateSchema } from "@/models/CreateTeam.ts"; // You must define this schema with zod or similar
import { useCreateTeam } from "@/services/TeamServices.ts"; // Hook to create team
import { useLocation } from "wouter";

const fieldLabels: Record<string, string> = {
  name: "Team name",
  mainColor: "Main color",
  secondaryColor: "Secondary color",
  ranking: "Level/Ranking",
  logo: "Logo",
};

export const TeamCreateScreen = () => {
  const [, navigate] = useLocation();
  const { mutateAsync } = useCreateTeam();

  const formData = useAppForm({
    defaultValues: {
      name: "",
      mainColor: "",
      secondaryColor: "",
      ranking: "",
      logo: null as File | null,
    },
    validators: {
      onSubmit: () => {
        const values = formData.store.state.values;
        const result = TeamCreateSchema.safeParse(values);
        if (!result.success) {
          const errors = result.error.flatten().fieldErrors as Record<string, string[]>;
          const firstErrorKey = Object.keys(errors)[0];
          const message = errors[firstErrorKey]?.[0];
          if (message) {
            const label = fieldLabels[firstErrorKey] ?? firstErrorKey;
            toast.error(`${label}: ${message}`, { duration: 5000 });
          }
          return { isValid: false, error: "Validation failed" };
        }
        return undefined;
      },
    },
    onSubmit: async ({ value }) => {
      const result = TeamCreateSchema.safeParse(value);
      if (!result.success) return;
      const payload = {
        name: result.data.name,
        mainColor: result.data.mainColor,
        secondaryColor: result.data.secondaryColor,
        ranking: result.data.ranking ? Number(result.data.ranking) : undefined,
        logo: result.data.logo instanceof File ? result.data.logo : null,
      };
      await mutateAsync(payload, {
        onSuccess: () => {
          toast.success("Team created successfully");
          navigate("/profile"); // Navigate to the user profile to see the new team
        },
        onError: (error) => {
          toast.error("Error creating team");
          console.error(error);
        }
      });
    },
  });

  return (
    <CommonLayout>
      <section>
        <div>
          <h1 className="text-center text-2xl font-semibold mb-2">
            Create team
          </h1>
          <formData.AppForm>
            <formData.FormContainer extraError={null} className="space-y-4 md:space-y-6">
              <formData.AppField name="name">
                {(field) => (
                  <field.TextField label="Team name" required />
                )}
              </formData.AppField>
              <formData.AppField name="mainColor">
                {(field) => (
                  <div>
                    <label className="block mb-1">Main color</label>
                    <input
                      type="color"
                      value={field.state.value}
                      onChange={e => field.handleChange(() => e.target.value)}
                      className="w-10 h-10 p-0 border-0 bg-transparent"
                    />
                  </div>
                )}
              </formData.AppField>
              <formData.AppField name="secondaryColor">
                {(field) => (
                  <div>
                    <label className="block mb-1">Secondary color</label>
                    <input
                      type="color"
                      value={field.state.value}
                      onChange={e => field.handleChange(() => e.target.value)}
                      className="w-10 h-10 p-0 border-0 bg-transparent"
                    />
                  </div>
                )}
              </formData.AppField>
              <formData.AppField name="ranking">
                {(field) => (
                  <field.TextField label="Level/Ranking" type="number" />
                )}
              </formData.AppField>
              <formData.AppField name="logo">
                {(field) => (
                  <FileInput
                    label="Logo"
                    accept="image/*"
                    onChange={(file) => field.handleChange(() => file)}
                  />
                )}
              </formData.AppField>
            </formData.FormContainer>
          </formData.AppForm>
        </div>
      </section>
    </CommonLayout>
  );
};