import { z } from "zod";

const MessageSchema = z.union([
    z.string(),
    z.record(z.string()),
]);

export const ErrorResponseSchema = z.object({
    status: z.number(),
    error: z.string(),
    message: MessageSchema,
    path: z.string(),
    timestamp: z.string()
});

export type ErrorResponse = z.infer<typeof ErrorResponseSchema>;