import { z } from "zod";

export const SignupRequestSchema = z.object({
  firstName: z.string().min(2),
  lastName: z.string().min(2),
  username: z.string().email(),
  password: z.string().min(6),
  age: z.coerce.number().min(13),
  gender: z.enum(["Male", "Female", "Other"], {
    errorMap: () => ({ message: "Must select gender" }),
  }),
  zone: z.string().min(2),
  role: z.enum(["USER", "ADMIN"], {
    errorMap: () => ({ message: "Must select Account Role!" }),
  }),
  avatar: z.any().optional(),
});

export type SignupRequest = z.infer<typeof SignupRequestSchema>;

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

export const SignupResponseSchema = z.object({
  accessToken: z.string().min(1),
  refreshToken: z.string().nullable(),
  user: UserSchema,
});

export type SignupResponse = z.infer<typeof SignupResponseSchema>;
