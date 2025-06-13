import { useMutation } from "@tanstack/react-query";
import { toast } from "react-hot-toast";
import { BASE_API_URL } from "@/config/app-query-client";
import { useToken } from "@/services/TokenContext";
import { LoginRequest, LoginResponseSchema } from "@/models/Login";
import { SignupRequest, SignupResponseSchema } from "@/models/Signup";
import { ForgotPasswordRequest, ForgotPasswordRequestSchema, ResetPasswordRequest, ResetPasswordRequestSchema } from "@/models/PasswordReset";
import {handleErrorResponse} from "@/services/ApiUtils.ts";
import { useQuery } from "@tanstack/react-query";

export interface RawPlayerDTO {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  avatarUrl: string;
  zone: string;
  age: number;
  gender: string;
  role: string;
  emailConfirmed: boolean;
}

export interface RawBasicMatchDTO {
  matchId: number;
  matchType: string;
  matchStatus: string;
  date: string;
  startTime: string;
  endTime: string;
  fieldName: string;
  fieldLocation: number;
  result: string;
  players: RawPlayerDTO[];
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

export function useSignup() {
  const [, setToken] = useToken();

  return useMutation({
    mutationFn: async (req: SignupRequest & { invitationToken?: string }) => {
      const tokenData = await signupService(req);
      setToken({ state: "LOGGED_IN", ...tokenData });
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
  return LoginResponseSchema.parse(json);
}

export async function signupService(req: SignupRequest & { invitationToken?: string }) {
  const formData = new FormData();

  const userPayload: any = {
    firstName: req.firstName,
    lastName: req.lastName,
    username: req.username,
    password: req.password,
    age: req.age,
    gender: req.gender,
    zone: req.zone,
    role: req.role,
  };

  if (req.invitationToken) {
    userPayload.invitationToken = req.invitationToken;
  }

  formData.append("user", JSON.stringify(userPayload));

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


export const useGetMyProfile = () => {
  const [tokenState] = useToken();

  return useQuery({
    queryKey: ["myProfile"],
    enabled: tokenState.state === "LOGGED_IN", // no hace la query si no estás logueado
    queryFn: async () => {
      if (tokenState.state !== "LOGGED_IN") {
        throw new Error("You are not logged in");
      }

      const response = await fetch(`${BASE_API_URL}/users/me`, {
        headers: { Authorization: `Bearer ${tokenState.accessToken}` },
      });

      if (!response.ok) {
        await handleErrorResponse(response, " while fetching profile");
      }

      return await response.json();
    },
  });
};

export async function getMyMatchesHistoryService(token: string): Promise<RawBasicMatchDTO[]> {
  const response = await fetch(`${BASE_API_URL}/users/me/played-matches`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: "application/json",
    },
  });

  if (!response.ok) {
    await handleErrorResponse(response, "fetching open matches");
  }
  return (await response.json()) as RawBasicMatchDTO[];
}

export function useGetMyMatchesPlayed() {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useQuery<RawBasicMatchDTO[], Error>({
    queryKey: ["openMatches"],
    queryFn: () => getMyMatchesHistoryService(token),
    enabled: token !== "",
  });
}


export async function getMyMatchesJoinedService(token: string): Promise<RawBasicMatchDTO[]> {
  const response = await fetch(`${BASE_API_URL}/users/me/joined-matches`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: "application/json",
    },
  });

  if (!response.ok) {
    await handleErrorResponse(response, "fetching open matches");
  }
  return (await response.json()) as RawBasicMatchDTO[];
}

export function useGetMyJoinedMatches() {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useQuery<RawBasicMatchDTO[], Error>({
    queryKey: ["openMatches"],
    queryFn: () => getMyMatchesJoinedService(token),
    enabled: token !== "",
  });
}


// --------------------------------SEARCH USERS---------------------------------------------
export const useSearchUserByUsername = (username: string | null) => {
  const [tokenState] = useToken();

  return useQuery({
    queryKey: ["searchUser", username],
    enabled: !!username && tokenState.state === "LOGGED_IN",
    queryFn: async () => {
      if (!username || tokenState.state !== "LOGGED_IN") return;

      const response = await fetch(`${BASE_API_URL}/users/${username}`, {
        headers: {
          Authorization: `Bearer ${tokenState.accessToken}`,
        },
      });

      if (!response.ok) {
        throw new Error("No se encontró el usuario");
      }

      return await response.json();
    },
  });
};
