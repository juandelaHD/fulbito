import { z } from "zod";

export const ForgotPasswordRequestSchema = z.object({
  email: z.string().email("Invalid email format").min(1, "Email must not be empty"),
});
export type ForgotPasswordRequest = z.infer<typeof ForgotPasswordRequestSchema>;

export const ResetPasswordRequestSchema = z.object({
  token: z.string().min(1),
  newPassword: z.string().min(6, "Password must be at least 6 characters long"),
  confirmPassword: z.string().min(6, "Confirm Password must be at least 6 characters long"),
});
export type ResetPasswordRequest = z.infer<typeof ResetPasswordRequestSchema>;