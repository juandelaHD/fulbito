import { z } from "zod";

export const ForgotPasswordRequestSchema = z.object({
  email: z.string().email(),
});
export type ForgotPasswordRequest = z.infer<typeof ForgotPasswordRequestSchema>;

export const ResetPasswordRequestSchema = z.object({
  token: z.string().min(1),
  newPassword: z.string().min(6),
  confirmPassword: z.string().min(6),
});
export type ResetPasswordRequest = z.infer<typeof ResetPasswordRequestSchema>;