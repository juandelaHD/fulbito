import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { resetPasswordService } from "@/services/UserServices";
import { ResetPasswordRequestSchema } from "@/models/PasswordReset";
import { useAppForm } from "@/config/use-app-form";
import { toast } from "react-hot-toast";

export const ResetPasswordScreen = () => {
  const token = new URLSearchParams(window.location.search).get("token") || "";

  const formData = useAppForm({
    defaultValues: {
      newPassword: "",
      confirmPassword: "",
    },
    validators: {
      onSubmit: () => {
        const values = { ...formData.store.state.values, token };
        try {
          ResetPasswordRequestSchema.parse(values);
        } catch (err: any) {
          toast.error(err.errors?.[0]?.message || "Invalid data");
          return { isValid: false, error: "Validation failed" };
        }
        return undefined;
      },
    },
    onSubmit: async ({ value }) => {
      await resetPasswordService({ token, ...value });
    },
  });

  return (
    <CommonLayout>
      <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow">
        <h1 className="text-2xl font-bold mb-4">Reset your password</h1>
        <formData.AppForm>
          <formData.FormContainer extraError={null} className="space-y-4">
            <formData.AppField name="newPassword">
              {(field) => (
                <field.PasswordField label="New password" required />
              )}
            </formData.AppField>
            <formData.AppField name="confirmPassword">
              {(field) => (
                <field.PasswordField label="Confirm new password" required />
              )}
            </formData.AppField>
          </formData.FormContainer>
        </formData.AppForm>
      </div>
    </CommonLayout>
  );
};