import { z } from "zod";

export const SignupRequestSchema = z.object({
  firstName: z.string().min(2),
  lastName: z.string().min(2),
  email: z.string().email(),
  age: z.coerce.number().min(13),
  gender: z.enum(["Male", "Female", "Other"], {
    errorMap: () => ({ message: "Must select gender" }),
  }),
  location: z.string().min(2),
  username: z.string().min(3),
  password: z.string().min(6),
  photo: z.any().optional(),
  userType: z.enum(["Player", "Field"], {
    errorMap: () => ({ message: "Must select Account Role!" }),
  })
});

export type SignupRequest = z.infer<typeof SignupRequestSchema>;