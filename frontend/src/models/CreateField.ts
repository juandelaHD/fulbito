import { z } from "zod";

export const CreateFieldSchema = z.object({
  name: z.string().min(2, "Name is required"),
  grassType: z.enum(["NATURAL_GRASS", "SYNTHETIC_TURF", "HYBRID_TURF"], {
    errorMap: () => ({ message: "Select grass type" }),
  }),
  illuminated: z.enum(["Yes", "No"], {
    errorMap: () => ({ message: "Select Lighting" })
  }).transform((val) => val === "Yes"),
  zone: z.string().min(2, "Zone is required"),
  address: z.string().min(2, "Address is required"),
  photos: z.any().optional(), // Esto se puede validar como File[] si querés
});

export type CreateFieldRequest = z.infer<typeof CreateFieldSchema>;

const LocationSchema = z.object({
  zone: z.string().min(2, "Zone is required"),
  address: z.string().min(2, "Address is required"),
});

export const CreateFieldResponseSchema = z.object({
  id: z.string().min(1, "Field ID is required"),
  name: z.string().min(1, "Field name is required"),
  grassType: z.enum(["NATURAL_GRASS", "SYNTHETIC_TURF", "HYBRID_TURF"], {
    errorMap: () => ({ message: "Select grass type" }),
  }),
  illuminated: z.boolean(),
  location: LocationSchema,
  imageUrls: z.array(z.string()).optional(),
});

export type CreateFieldResponse = z.infer<typeof CreateFieldResponseSchema>;