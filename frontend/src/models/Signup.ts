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
  invitationToken: z.string().uuid().optional(),
});

export type SignupRequest = z.infer<typeof SignupRequestSchema>;

export const SignupResponseSchema = z.object({
  accessToken: z.string().min(1),
  refreshToken: z.string().nullable(),
});

export type SignupResponse = z.infer<typeof SignupResponseSchema>;