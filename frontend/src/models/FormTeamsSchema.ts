import { z } from "zod";

export const FormTeamsSchema = z.object({
  strategy: z.enum([
    "MANUAL",
    "RANDOM",
    "BY_AGE",
    "BY_EXPERIENCE",
    "BY_GENDER",
    "BY_ZONE",
  ]),
  teamAPlayerIds: z.array(z.number()).optional(),
  teamBPlayerIds: z.array(z.number()).optional(),
}).superRefine((data, ctx) => {
  if (data.strategy === "MANUAL") {
    if (!data.teamAPlayerIds || !data.teamBPlayerIds) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: "Debes asignar jugadores a ambos equipos",
        path: [],
      });
    } else if (data.teamAPlayerIds.length !== data.teamBPlayerIds.length) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: "Ambos equipos deben tener la misma cantidad de jugadores",
        path: [],
      });
    }
  }
});

export type FormTeams = z.infer<typeof FormTeamsSchema>;