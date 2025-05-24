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
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="w-full max-w-md bg-white p-8 rounded-xl shadow-xl">
          <h1 className="text-3xl font-bold text-gray-800 text-center mb-2">Create your account</h1>
          <p className="text-sm text-gray-500 text-center mb-6">
            Join to organize or play 5-a-side football
          </p>

          <formData.AppForm>
            <formData.FormContainer extraError={error} className="space-y-4">
              <formData.AppField name="firstName">
                {(field) => <field.TextField label="First Name" className="w-full" />}
              </formData.AppField>
              <formData.AppField name="lastName">
                {(field) => <field.TextField label="Last Name" className="w-full" />}
              </formData.AppField>
              <formData.AppField name="email">
                {(field) => <field.TextField label="Email" className="w-full" />}
              </formData.AppField>
              <formData.AppField name="age">
                {(field) => <field.TextField label="Age" className="w-full" />}
              </formData.AppField>
              <formData.AppField name="gender">
                {(field: any) => (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Gender</label>
                    <select
                      className="w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                      {...field}
                    >
                      <option value="">Select...</option>
                      <option value="Male">Male</option>
                      <option value="Female">Female</option>
                      <option value="Other">Other</option>
                    </select>
                  </div>
                )}
              </formData.AppField>
              <formData.AppField name="location">
                {(field) => <field.TextField label="Location" className="w-full" />}
              </formData.AppField>
              <formData.AppField name="username">
                {(field) => <field.TextField label="Username" className="w-full" />}
              </formData.AppField>
              <formData.AppField name="password">
                {(field) => <field.PasswordField label="Password" className="w-full" />}
              </formData.AppField>
            </formData.FormContainer>
          </formData.AppForm>
        </div>
      </div>
    </CommonLayout>
  );
};
