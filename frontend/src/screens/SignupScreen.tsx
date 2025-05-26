import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { useAppForm } from "@/config/use-app-form";
import { useSignup } from "@/services/UserServices";
import { SignupRequestSchema } from "@/models/Signup";

export const SignupScreen = () => {
  const { mutate, error } = useSignup();

  const formData = useAppForm({
    defaultValues: {
      username: "",
      password: "",
      firstName: "",
      lastName: "",
      email: "",
      age: "",
      gender: "",
      location: "",
    },
    validators: {
      onChange: (values) => {
        const result = SignupRequestSchema.safeParse(values);
        if (!result.success) {
          const error = result.error.format();
          return {
            isValid: false,
            error: Object.values(error)
              .map((e: any) => e?._errors?.[0])
              .filter(Boolean)
              .join(", "),
          };
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

