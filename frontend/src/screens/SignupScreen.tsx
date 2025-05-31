import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { useAppForm } from "@/config/use-app-form";
import { useSignup } from "@/services/UserServices";
import { SignupRequestSchema } from "@/models/Signup";
import { toast } from "react-hot-toast";
import { FileInput } from "@/components/form-components/FileInput/FileInput";

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

export const SignupScreen = () => {
  const { mutate, error } = useSignup();

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
        
        console.log("Valores actuales:", values);

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
      // Por qué me obliga a hacer esta transformacion manual?
      const ageNum = Number(value.age);
      if (isNaN(ageNum)) {
        toast.error("Age: Debe ser un número válido", {duration: 5000});
        console.log("Error: Age debe ser un número válido");
        return;
      }

      let role: "USER" | "ADMIN";
      if (value.role === "USER") {
        role = "USER";
      } else if (value.role === "ADMIN") {
        role = "ADMIN";
      } else {
        toast.error("Role: Selecciona un rol válido", { duration: 5000 });
        console.log("Error: Role debe ser 'Player' o 'Field Admin'");
        return;
      }

      let gender: "Male" | "Female" | "Other";
      if (["Male", "Female", "Other"].includes(value.gender)) {
        gender = value.gender as any;
      } else {
        toast.error("Gender: Selecciona un género válido", { duration: 5000 });
        console.log("Error: Gender debe ser 'Male', 'Female' o 'Other'");
        return;
      }

      const payload = {
        ...value,
        age: ageNum,
        role,
        gender
      };

      console.log("Signup data:", payload);
      mutate(payload);
    },
  });

  return (
      <CommonLayout>
        <section>
          <div>
            <div>
              <h1 className="text-center text-2xl font-semibold mb-2">
                Create an account
              </h1>
              <h2 className="text-center text-sm font-light text-green-800 mb-4">
                Join the community and simplify your matchday!
              </h2>
              <formData.AppForm>
                <formData.FormContainer extraError={error} className="space-y-4 md:space-y-6">
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
                        <field.PasswordField
                            label="Password"
                        />
                    )}
                  </formData.AppField>
                  <formData.AppField name="age">
                    {(field) => (
                        <field.TextField
                            label="Age"
                        />
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
                        <field.TextField
                            label="Location"
                        />
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
          <div className="text-center text-sm font-light text-green-800 mt-4">
            Already have an account?{" "}
            <a href="/login" className="font-medium text-green-700 hover:underline">
              Log in
            </a>
          </div>
        </section>
      </CommonLayout>
  );
}

