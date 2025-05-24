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
      <div className="max-w-xl mx-auto bg-white p-6 shadow-md rounded space-y-4">
        <h1 className="text-2xl font-bold mb-4">Create Account</h1>
        <formData.AppForm>
          <formData.FormContainer extraError={error}>
            <formData.AppField name="firstName" children={(field) => (
              <field.TextField label="First Name" />
            )} />
            <formData.AppField name="lastName" children={(field) => (
              <field.TextField label="Last Name" />
            )} />
            <formData.AppField name="email" children={(field) => (
              <field.TextField label="Email" />
            )} />
            <formData.AppField name="age" children={(field) => (
              <field.TextField label="Age" />
            )} />
            <formData.AppField name="gender">
              {(field: any) => (
                <div>
                  <label className="block text-sm font-medium mb-1">Gender</label>
                  <select
                    className="w-full p-2 border border-gray-300 rounded"
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
            <formData.AppField name="location" children={(field) => (
              <field.TextField label="Location" />
            )} />
            <formData.AppField name="username" children={(field) => (
              <field.TextField label="Username" />
            )} />
            <formData.AppField name="password" children={(field) => (
              <field.PasswordField label="Password" />
            )} />
          </formData.FormContainer>
        </formData.AppForm>
      </div>
    </CommonLayout>
  );
};
