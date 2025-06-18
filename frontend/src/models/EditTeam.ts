import { z } from "zod";

export const TeamEditSchema = z.object({
  id: z.number().min(1,"Id is required"),
  name: z.string().min(1, "Team name is required"),
  mainColor: z.string().min(1, "Main color is required"),
  secondaryColor: z.string().min(1, "Secondary color is required"),
  ranking: z.union([z.string(), z.number()]).optional(),
  logo: z.any().optional(),
}).refine(
  (data) => data.mainColor !== data.secondaryColor,
  {
    message: "Main color and secondary color must be different",
    path: ["secondaryColor"],
  }
);

export type TeamEditRequest = z.infer<typeof TeamEditSchema>;