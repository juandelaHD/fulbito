import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { forgotPasswordService } from "@/services/UserServices";
import { ForgotPasswordRequestSchema } from "@/models/PasswordReset";
import { useAppForm } from "@/config/use-app-form";
import { toast } from "react-hot-toast";

export const ForgotPasswordScreen = () => {
  const formData = useAppForm({
    defaultValues: { email: "" },
    validators: {
      onSubmit: () => {
        const values = formData.store.state.values;
        try {
          ForgotPasswordRequestSchema.parse(values);
        } catch (err: any) {
          toast.error(err.errors?.[0]?.message || "Invalid email");
          return { isValid: false, error: "Validation failed" };
        }
        return undefined;
      },
    },
    onSubmit: async ({ value }) => {
      await forgotPasswordService({ email: value.email });
    },
  });

  return (
    <CommonLayout>
      <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow">
        <h1 className="text-2xl font-bold mb-4">Forgot your password?</h1>
        <formData.AppForm>
          <formData.FormContainer extraError={null} className="space-y-4">
            <formData.AppField name="email">
              {(field) => (
                <field.TextField
                  label="Email"
                  type="email"
                  required
                />
              )}
            </formData.AppField>
          </formData.FormContainer>
        </formData.AppForm>
      </div>
    </CommonLayout>
  );
};