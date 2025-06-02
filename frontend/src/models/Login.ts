import { z } from "zod";

export const LoginRequestSchema = z.object({
  username: z.string().min(1, "Email must not be empty"),
  password: z.string().min(1, "Password must not be empty"),
});

export type LoginRequest = z.infer<typeof LoginRequestSchema>;

export const UserSchema = z.object({
  id: z.number(),
  username: z.string(),
  firstName: z.string(),
  lastName: z.string(),
  gender: z.string(),
  zone: z.string(),
  age: z.number(),
  role: z.string(),
});

export const LoginResponseSchema = z.object({
  accessToken: z.string().min(1),
  refreshToken: z.string().nullable(),
  user: UserSchema,
});

export type LoginResponse = z.infer<typeof LoginResponseSchema>;
