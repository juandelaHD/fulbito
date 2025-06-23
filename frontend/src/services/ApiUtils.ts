import { toast } from "react-hot-toast";
import { ErrorResponseSchema } from "@/models/ErrorResponse";

export async function handleErrorResponse(
  response: Response,
  context: string,
): Promise<never> {
    let errorPayload: unknown;
    try {
        errorPayload = await response.json();
    } catch (e) {
        const text = await response.text();
        toast.error(`Error ${context}: ${text}`, { duration: 5000 });
        throw new Error(`Failed with status ${response.status}: ${text}`);
    }

    const result = ErrorResponseSchema.safeParse(errorPayload);
    if (result.success) {
        const error = result.data;
        let msg = error.message;
        if (msg && typeof msg === "object") {
            msg = Object.values(msg).join(" | ");
        }
        toast.error(`Error ${context}: ${msg}`, { duration: 5000 });
        throw new Error(msg || `Failed with status ${error.status}: Error ${context}`);
    } else {
        const payloadStr = JSON.stringify(errorPayload);
        toast.error(`Unexpected error ${context}: ${payloadStr}`, { duration: 5000 });
        throw new Error(`Unexpected error: ${payloadStr}`);
    }
}