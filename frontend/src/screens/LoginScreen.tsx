import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { useAppForm } from "@/config/use-app-form";
import { LoginRequestSchema } from "@/models/Login";
import { useLogin } from "@/services/UserServices";

export const LoginScreen = () => {
  const { mutate, error } = useLogin();

  const formData = useAppForm({
    defaultValues: {
      username: "",
      password: "",
    },
    validators: {
      onChange: LoginRequestSchema,
    },
    onSubmit: async ({ value }) => mutate(value),
  });

  return (
    <CommonLayout>
      <h1>Log In</h1>
      <formData.AppForm>
        <formData.FormContainer extraError={error}>
          <formData.AppField name="username" children={(field) => <field.TextField label="Username" />} />
          <formData.AppField name="password" children={(field) => <field.PasswordField label="Password" />} />
        </formData.FormContainer>
      </formData.AppForm>
      <div className="text-sm font-light text-green-800 mt-4">
        ¿No tienes una cuenta?{" "}
        <a href="/singup" className="font-medium text-green-700 hover:underline">
          Regístrate
        </a>
      </div>
    </CommonLayout>
  );
};
