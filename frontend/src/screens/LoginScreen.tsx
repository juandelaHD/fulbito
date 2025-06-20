import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { useAppForm } from "@/config/use-app-form";
import { LoginRequestSchema } from "@/models/Login";
import { useLogin } from "@/services/UserServices";
import { useLocation } from "wouter";

export const LoginScreen = () => {
  const { mutate, isSuccess } = useLogin();
  const [, setLocation] = useLocation();

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

  if (isSuccess) {
    setLocation("/");
  }

  return (
      <CommonLayout>
          <h1 className="text-center text-2xl font-bold mb-4">Welcome back!</h1>
          <formData.AppForm>
              <formData.FormContainer extraError={null}>
                  <formData.AppField name="username" children={(field) => <field.TextField label="Email"/>}/>
                  <formData.AppField name="password" children={(field) => <field.PasswordField label="Password"/>}/>
                  <div className="text-sm text-green-700 text-left -mt-2">
                      <a href="/forgot-password" className="hover:underline">
                          Forgot your password?
                      </a>
                  </div>
              </formData.FormContainer>
          </formData.AppForm>
          <div className="text-center text-sm font-light text-green-800 mt-4">
              Don't have an account?{" "}
              <a href="/signup" className="font-medium text-green-700 hover:underline">
                  Sign Up
              </a>
          </div>
      </CommonLayout>
  );
};
