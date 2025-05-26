import { z } from "zod";

export const SignupRequestSchema = z.object({
  username: z.string().min(3),
  password: z.string().min(6),
  firstName: z.string().min(2),
  lastName: z.string().min(2),
  email: z.string().email(),
  age: z.coerce.number().min(13),
  gender: z.enum(["Male", "Female", "Other"]),
  location: z.string().min(2),
  photo: z.any().optional(), // We'll handle this differently if backend needs it
});

export type SignupRequest = z.infer<typeof SignupRequestSchema>;