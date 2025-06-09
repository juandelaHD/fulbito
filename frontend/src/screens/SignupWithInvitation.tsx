import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { useAppForm } from "@/config/use-app-form";
import { useSignup } from "@/services/UserServices";
import { SignupRequestSchema } from "@/models/Signup";
import { toast } from "react-hot-toast";
import { FileInput } from "@/components/form-components/FileInput/FileInput";
import { useLocation, useRoute } from "wouter";

const fieldLabels: Record<string, string> = {
  firstName: "First Name",
  lastName: "Last Name",
  username: "Email",
  password: "Password",
  age: "Age",
  gender: "Gender",
  zone: "Location",
  role: "Role",
};

export const SignupInvitationScreen = () => {
  useLocation();
  // Extrae el token de la URL: /invite/:token
  const [, params] = useRoute("/invite/:token");
  const token = params?.token;
  const { mutateAsync } = useSignup();

  const formData = useAppForm({
    defaultValues: {
      firstName: "",
      lastName: "",
      username: "",
      password: "",
      age: "",
      gender: "",
      zone: "",
      role: "",
      avatar: null as File | null
    },
    validators: {
      onSubmit: () => {
        const values = formData.store.state.values;
        const result = SignupRequestSchema.safeParse(values);
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
      const result = SignupRequestSchema.safeParse(value);
      if (!result.success) {
        return;
      }
      const payload = {
        firstName: result.data.firstName,
        lastName: result.data.lastName,
        username: result.data.username,
        password: result.data.password,
        age: result.data.age,
        gender: result.data.gender,
        zone: result.data.zone,
        role: result.data.role,
        avatar: result.data.avatar instanceof File ? result.data.avatar : null,
        invitationToken: token,
      };

      await mutateAsync(payload);
    },
  });

  return (
    <CommonLayout>
      <section>
        <div>
          <div>
            <h1 className="text-center text-2xl font-semibold mb-2">
              Invitation to Join
            </h1>
            <h2 className="text-center text-sm font-light text-green-800 mb-4">
              You have been invited to create an account to join a match!
            </h2>
            <formData.AppForm>
              <formData.FormContainer extraError={null} className="space-y-4 md:space-y-6">
                <formData.AppField name="firstName">
                  {(field) => (
                    <field.TextField label="First Name"/>
                  )}
                </formData.AppField>
                <formData.AppField name="lastName">
                  {(field) => (
                    <field.TextField label="Last Name"/>
                  )}
                </formData.AppField>
                <formData.AppField name="username">
                  {(field) => (
                    <field.TextField label="Email"/>
                  )}
                </formData.AppField>
                <formData.AppField name="password">
                  {(field) => (
                    <field.PasswordField label="Password"/>
                  )}
                </formData.AppField>
                <formData.AppField name="age">
                  {(field) => (
                    <field.TextField label="Age"/>
                  )}
                </formData.AppField>
                <formData.AppField name="gender">
                  {(field) => (
                    <field.SelectField
                      label="Gender"
                      options={[
                        {label: "Select...", value: ""},
                        {label: "Male", value: "Male"},
                        {label: "Female", value: "Female"},
                        {label: "Other", value: "Other"},
                      ]}
                    />
                  )}
                </formData.AppField>
                <formData.AppField name="zone">
                  {(field) => (
                    <field.TextField label="Location"/>
                  )}
                </formData.AppField>
                <formData.AppField name="role">
                  {(field) => (
                    <field.SelectField
                      label="Role"
                      options={[
                        {label: "Select Role...", value: ""},
                        {label: "Player", value: "USER"},
                        {label: "Field Admin", value: "ADMIN"},
                      ]}
                    />
                  )}
                </formData.AppField>
                <formData.AppField name="avatar">
                  {(field) => (
                    <FileInput
                      label="Avatar"
                      accept="image/*"
                      onChange={(file) => field.handleChange(() => file)}
                    />
                  )}
                </formData.AppField>
              </formData.FormContainer>
            </formData.AppForm>
          </div>
        </div>
      </section>
    </CommonLayout>
  );
};