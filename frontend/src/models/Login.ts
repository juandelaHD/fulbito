import { z } from "zod";

export const LoginRequestSchema = z.object({
  username: z.string().min(1, "Email must not be empty"),
  password: z.string().min(1, "Password must not be empty"),
});

export type LoginRequest = z.infer<typeof LoginRequestSchema>;

export const LoginResponseSchema = z.object({
  accessToken: z.string().min(1),
  refreshToken: z.string().nullable(),
  role: z.enum(["USER", "ADMIN"]),
});

export type LoginResponse = z.infer<typeof LoginResponseSchema>;
