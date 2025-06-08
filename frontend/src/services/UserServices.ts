import { useMutation } from "@tanstack/react-query";
import { toast } from "react-hot-toast";
import { BASE_API_URL } from "@/config/app-query-client";
import { useToken } from "@/services/TokenContext";
import { LoginRequest, LoginResponseSchema } from "@/models/Login";
import { SignupRequest, SignupResponseSchema } from "@/models/Signup";
import { ForgotPasswordRequest, ForgotPasswordRequestSchema, ResetPasswordRequest, ResetPasswordRequestSchema } from "@/models/PasswordReset";
import {handleErrorResponse} from "@/services/ApiUtils.ts";

export function useSignup() {
  return useMutation({
    mutationFn: signupService,
  });
}

export function useLogin() {
  const [, setToken] = useToken();

  return useMutation({
    mutationFn: async (req: LoginRequest) => {
      const tokenData = await loginService(req);
      setToken({ state: "LOGGED_IN", ...tokenData });
    }
  });
}

export async function loginService(req: LoginRequest) {
  const response = await fetch(`${BASE_API_URL}/sessions/login`, {
    method: "POST",
    headers: {
      "Accept": "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(req),
  });

  if (!response.ok) {
    await handleErrorResponse(response, "in login");
  }

  const json = await response.json();
  toast.success("Login successful!");
  return LoginResponseSchema.parse(json);
}

export async function signupService(req: SignupRequest) {
  const formData = new FormData();

  formData.append("user", JSON.stringify({
    firstName: req.firstName,
    lastName: req.lastName,
    username: req.username,
    password: req.password,
    age: req.age,
    gender: req.gender,
    zone: req.zone,
    role: req.role,
  }));

  if (req.avatar instanceof File) {
    formData.append("avatar", req.avatar);
  }

  const response = await fetch(`${BASE_API_URL}/sessions/sign-up`, {
    method: "POST",
    body: formData,
  });

  if (!response.ok) {
    await handleErrorResponse(response, "in sign up")
  }

  const json = await response.json();
  toast.success("User created successfully! Please check your email to verify.", { duration: 5000 });
  return SignupResponseSchema.parse(json);
}

export async function forgotPasswordService(req: ForgotPasswordRequest) {
  // Validar antes de enviar
  ForgotPasswordRequestSchema.parse(req);

  const response = await fetch(`${BASE_API_URL}/sessions/forgot-password`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(req),
  });

  if (!response.ok) {
    await handleErrorResponse(response, "in forgot password");
  }
}

export async function resetPasswordService(req: ResetPasswordRequest) {
  ResetPasswordRequestSchema.parse(req);

  const response = await fetch(`${BASE_API_URL}/sessions/reset-password`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(req),
  });

  if (!response.ok) {
    await handleErrorResponse(response, "in reset password");
  }
}