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
        toast.error(`Error ${context}: ${await response.text()}`, { duration: 5000 });
        throw new Error(`Failed with status ${response.status}: ${await response.text()}`);
    }

    const result = ErrorResponseSchema.safeParse(errorPayload);
    if (result.success) {
        const error = result.data;
        toast.error(`Error ${context}: ${error.message}`, { duration: 5000 });
        throw new Error(`Failed with status ${error.status}: ${error.message}`);
    } else {
        toast.error(`Unexpected error ${context}: ${JSON.stringify(errorPayload)}`, { duration: 5000 });
        throw new Error(`Unexpected error: ${JSON.stringify(errorPayload)}`);
    }
}
