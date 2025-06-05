import { z } from "zod";

export const CreateMatchSchema = z.object({
  matchType: z.string(),
  fieldId: z.string(),
  minPlayers: z.number().min(1),
  maxPlayers: z.number().min(1),
  date: z.date(),      
  startTime: z.date(),    
  endTime: z.date(),      
});

export type CreateMatch = z.infer<typeof CreateMatchSchema>
