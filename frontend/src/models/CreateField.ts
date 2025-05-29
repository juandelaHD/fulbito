import { z } from "zod";

export const CreateFieldSchema = z.object({
  name: z.string().min(2, "Name is required"),
  grassType: z.enum(["Synthetic", "Natural"], {
    errorMap: () => ({ message: "Select grass type" }),
  }),
  lighting: z.enum(["Yes", "No"], {
    errorMap: () => ({ message: "Specify if there is lighting" }),
  }),
  zone: z.string().min(2, "Zone is required"),
  address: z.string().min(2, "Address is required"),
  photos: z.any().optional(), // Esto se puede validar como File[] si querés
});

export type CreateFieldRequest = z.infer<typeof CreateFieldSchema>;
