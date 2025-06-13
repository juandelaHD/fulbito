import { z } from "zod"

export const CreateReviewRequestSchema = z.object({
  rating: z.coerce.number().min(1).max(10),
  comment: z.string().min(1).max(100),
})

export type CreateReviewRequest = z.infer<typeof CreateReviewRequestSchema>

export const ReviewItemSchema = z.object({
  rating: z.number(),
  comment: z.string(),
  fieldId: z.number(),
  userId: z.number(),
  createdAt: z.string(),
})

export const GetReviewsResponseSchema = z.object({
  content: z.array(ReviewItemSchema),
  totalPages: z.number().optional(),
  totalElements: z.number().optional(),
  pageable: z
    .object({
      pageNumber: z.number(),
      pageSize: z.number(),
    })
    .optional(),
})

export type GetReviewsResponse = z.infer<typeof GetReviewsResponseSchema>
