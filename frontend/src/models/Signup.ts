import { z } from "zod";

export const SignupRequestSchema = z.object({
  firstName: z.string().min(2),
  lastName: z.string().min(2),
  email: z.string().email(),
  password: z.string().min(6),
  age: z.coerce.number().min(13),
  gender: z.enum(["Male", "Female", "Other"], {
    errorMap: () => ({ message: "Must select gender" }),
  }),
  location: z.string().min(2),
  photo: z.any().optional(),
  userType: z.enum(["Player", "Field"], {
    errorMap: () => ({ message: "Must select Account Role!" }),
  }),
  avatar: z.any().optional(),
});

export type SignupRequest = z.infer<typeof SignupRequestSchema>;