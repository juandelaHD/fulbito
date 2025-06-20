import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { useAppForm } from "@/config/use-app-form.ts";
import { toast } from "react-hot-toast";
import { FileInput } from "@/components/form-components/FileInput/FileInput.tsx";
import { useEditTeam, useUpdateTeam } from "@/services/TeamServices.ts"; // Hook to create team
import { useLocation,useRoute } from "wouter";
import { TeamEditSchema } from "@/models/EditTeam";
import { TeamMembersTable } from "@/components/tables/MembersTable";
import { RawUserDTO } from "@/services/UserServices.ts";

const fieldLabels: Record<string, string> = {
  id: "Team Id",
  name: "Team name",
  mainColor: "Main color",
  secondaryColor: "Secondary color",
  ranking: "Level/Ranking",
  logo: "Logo",
};


export const TeamEditScreen  = () => {
    const [,navigate] = useLocation();
    const [,params] = useRoute("/teams/edit/:id");
    const { mutateAsync } = useUpdateTeam();
    console.log('Team Edit Screen');
    const { id } = params as { id: string };
    //const { data,isLoading, error, refetch } = useEditTeam(id);
    const { data } = useEditTeam(id);
    let count: number | undefined = data?.members.length;
    let miembros: RawUserDTO[] = [];
    let condicion :boolean = (count !== undefined && count > 0);
    if ( data?.members ){
      miembros = data.members;
    }
    //
    const formData = useAppForm({
      defaultValues: {
        id: data?.id,
        name: data?.name,
        mainColor: data?.mainColor,
        secondaryColor: data?.secondaryColor,
        ranking: data?.ranking,
        logo: null as File | null,
      },
      validators: {
        onSubmit: () => {
          const values = formData.store.state.values;
          const result = TeamEditSchema.safeParse(values);
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
        const result = TeamEditSchema.safeParse(value);
        if (!result.success) return;
        const payload = {
          id: result.data.id,
          name: result.data.name,
          mainColor: result.data.mainColor,
          secondaryColor: result.data.secondaryColor,
          ranking: result.data.ranking ? Number(result.data.ranking) : undefined,
          logo: result.data.logo instanceof File ? result.data.logo : null,
        };
        await mutateAsync(payload, {
          onSuccess: () => {
            toast.success("Team updated successfully");
            navigate("/teams"); // Navigate to the user profile to see the new team
          },
          onError: (error) => {
            toast.error("Error updating team");
            console.error(error);
          }
        });
      }
    });

    return (
        <CommonLayout>
          <section>
            <div>
              <h1 className="text-center text-2xl font-semibold mb-2">
                Edit team
              </h1>
              <formData.AppForm>
                <formData.FormContainer extraError={null} className="space-y-4 md:space-y-6">
                  <formData.AppField name="id">
                    {(field) => (
                      <field.HiddenField />
                    )}
                  </formData.AppField>
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
                  { condicion && 
                    <section>
                      <div>
                        <h1 className="text-center text-2xl font-semibold my-2">
                          Team Members
                        </h1>
                      </div>
                      <div>
                        <TeamMembersTable data={miembros} />
                      </div>
                    </section>
                  }
                </formData.FormContainer>
              </formData.AppForm>
            </div>
          </section>
        </CommonLayout>
      );
}
