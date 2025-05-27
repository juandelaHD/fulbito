import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { useAppForm } from "@/config/use-app-form";
import { useSignup } from "@/services/UserServices";
import { SignupRequestSchema } from "@/models/Signup";
import { toast } from "react-hot-toast";

const fieldLabels: Record<string, string> = {
  firstName: "First Name",
  lastName: "Last Name",
  email: "Email",
  age: "Age",
  gender: "Gender",
  location: "Location",
  username: "Username",
  password: "Password",
  userType: "Role",
};

export const SignupScreen = () => {
  const { mutate, error } = useSignup();

  const formData = useAppForm({
    defaultValues: {
      firstName: "",
      lastName: "",
      email: "",
      age: "",
      gender: "",
      location: "",
      username: "",
      password: "",
      userType: "",
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
        return { isValid: true };
      },
    },
    onSubmit: async ({ value }) => {
      console.log("Signup data:", value);
      mutate(value);
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
                        <field.TextField label="Last Name" />
                    )}
                  </formData.AppField>
                  <formData.AppField name="email">
                    {(field) => (
                        <field.TextField label="Email" />
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
                    {(field: any) => (
                        <div>
                          <label className="block text-sm font-medium text-green-900">Gender</label>
                          <select
                              className="w-full px-4 py-2 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-green-500 text-black bg-white"
                              {...field}
                          >
                            <option value="">Select...</option>
                            <option value="Male">Masculine</option>
                            <option value="Female">Feminine</option>
                            <option value="Other">Other</option>
                          </select>
                        </div>
                    )}
                  </formData.AppField>
                  <formData.AppField name="location">
                    {(field) => (
                        <field.TextField
                            label="Location"
                        />
                    )}
                  </formData.AppField>
                  <formData.AppField name="username">
                    {(field) => (
                        <field.TextField
                            label="Username"
                        />
                    )}
                  </formData.AppField>
                  <formData.AppField name="password">
                    {(field) => (
                        <field.PasswordField
                            label="Password"
                        />
                    )}
                  </formData.AppField>
                  <formData.AppField name="userType">
                    {(field: any) => (
                      <div>
                        <label className="block text-sm font-medium text-green-900">Role</label>
                        <select
                          className="w-full px-4 py-2 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-green-500 text-black bg-white"
                          {...field}
                        >
                          <option value="">Select Role...</option>
                          <option value="Player">Player</option>
                          <option value="Field Admin">Field Manager</option>
                        </select>
                      </div>
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

