import { useMutation } from "@tanstack/react-query";
import { toast } from "react-hot-toast";
import { BASE_API_URL } from "@/config/app-query-client";
import { useToken } from "@/services/TokenContext";
import { LoginRequest, LoginResponseSchema } from "@/models/Login";
import { SignupRequest, SignupResponseSchema } from "@/models/Signup";
import { handleErrorResponse } from "@/services/ApiUtils.ts";

export function useLogin() {
  const [, setToken] = useToken();

  return useMutation({
    mutationFn: async (req: LoginRequest) => {
      const tokenData = await loginService(req);
      setToken({
        state: "LOGGED_IN",
        accessToken: tokenData.accessToken,
        refreshToken: tokenData.refreshToken,
        user: tokenData.user,
      });
    }
  });
}

export function useSignup() {
  const [, setToken] = useToken();

  return useMutation({
    mutationFn: async (req: SignupRequest) => {
      const tokenData = await signupService(req);
      setToken({
        state: "LOGGED_IN",
        accessToken: tokenData.accessToken,
        refreshToken: tokenData.refreshToken,
        user: tokenData.user,
      });
    },
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
  return LoginResponseSchema.parse(json); // 👈 asegurate que `json` incluya `user`
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
    await handleErrorResponse(response, "in sign up");
  }

  const json = await response.json();
  toast.success("User created successfully!");
  return SignupResponseSchema.parse(json); // 👈 asegurate que `json` incluya `user`
}
