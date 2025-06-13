import { z } from "zod"

export const CreateReviewSchema = z.object({
  rating: z
    .string()
    .refine((val) => Number(val) >= 1 && Number(val) <= 10, {
      message: "Rating must be between 1 and 10",
    }),
  comment: z.string().min(1, "Comment is required").max(100, "Max 100 characters"),
})

export type CreateReviewRequest = z.infer<typeof CreateReviewSchema>