import { z } from "zod";

export const GetFieldsRequestSchema = z.object({
  name: z.string().optional(),
  zone: z.string().optional(),
  address: z.string().optional(),
  grassType: z.enum(["NATURAL_GRASS", "SYNTHETIC_TURF", "HYBRID_TURF"]).optional(),
  isIlluminated: z.boolean().optional(),
  hasOpenScheduledMatch: z.boolean().optional(),
  enabled: z.boolean().optional(),
  page: z.number().int().min(0).optional(),
  size: z.number().int().min(1).max(100).optional()
});

export type GetFieldsRequest = z.infer<typeof GetFieldsRequestSchema>;

export const FieldItemSchema = z.object({
    id: z.number().min(1, "Field ID is required"),
    name: z.string().min(1, "Field name is required"),
    grassType: z.enum(["NATURAL_GRASS", "SYNTHETIC_TURF", "HYBRID_TURF"], {
        errorMap: () => ({ message: "Select grass type" }),
    }),
    illuminated: z.boolean(),
    location: z.object({
        zone: z.string().min(2, "Zone is required"),
        address: z.string().min(2, "Address is required"),
    }),
    enabled: z.boolean(),
    imagesUrls: z.array(z.string()),
    matchesWithMissingPlayers: z.record(z.string(), z.number()).nullable().optional(),
});

export const GetFieldsResponseSchema = z.object({
    content: z.array(FieldItemSchema),
    totalPages: z.number().optional(),
    totalElements: z.number().optional(),
    pageable: z
        .object({
            pageNumber: z.number(),
            pageSize: z.number(),
        })
        .optional(),
});
export type GetFieldsResponse = z.infer<typeof GetFieldsResponseSchema>;