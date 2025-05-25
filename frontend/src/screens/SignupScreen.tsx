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
          <a>
            Unite para jugar o organizar fútbol 5
          </a>
          <div>
            <div>
              <h1>
                Crear cuenta
              </h1>
              <formData.AppForm>
                <formData.FormContainer extraError={error} className="space-y-4 md:space-y-6">
                  <formData.AppField name="firstName">
                    {(field) => (
                        <field.TextField
                            label="Nombre"
                        />
                    )}
                  </formData.AppField>
                  <formData.AppField name="lastName">
                    {(field) => (
                        <field.TextField
                            label="Apellido"
                        />
                    )}
                  </formData.AppField>
                  <formData.AppField name="email">
                    {(field) => (
                        <field.TextField
                            label="Email"
                        />
                    )}
                  </formData.AppField>
                  <formData.AppField name="age">
                    {(field) => (
                        <field.TextField
                            label="Edad"
                        />
                    )}
                  </formData.AppField>
                  <formData.AppField name="gender">
                    {(field: any) => (
                        <div>
                          <label className="block text-sm font-medium text-green-900 mb-1">Género</label>
                          <select
                              className="w-full px-4 py-2 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-green-500 text-black bg-white"
                              {...field}
                          >
                            <option value="">Seleccionar...</option>
                            <option value="Male">Masculino</option>
                            <option value="Female">Femenino</option>
                            <option value="Other">Otro</option>
                          </select>
                        </div>
                    )}
                  </formData.AppField>
                  <formData.AppField name="location">
                    {(field) => (
                        <field.TextField
                            label="Ubicación"
                        />
                    )}
                  </formData.AppField>
                  <formData.AppField name="username">
                    {(field) => (
                        <field.TextField
                            label="Usuario"
                        />
                    )}
                  </formData.AppField>
                  <formData.AppField name="password">
                    {(field) => (
                        <field.PasswordField
                            label="Contraseña"
                        />
                    )}
                  </formData.AppField>
                </formData.FormContainer>
              </formData.AppForm>
            </div>
          </div>
          <div className="text-center text-sm font-light text-green-800 mt-4">
            ¿Ya tenés una cuenta?{" "}
            <a href="/login" className="font-medium text-green-700 hover:underline">
              Iniciar sesión
            </a>
          </div>
        </section>
      </CommonLayout>
  );
}

