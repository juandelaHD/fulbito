import { z } from "zod";

export const CreateFieldScheduleRequestSchema = z.object({
  startDate: z.string(), // "YYYY-MM-DD"
  endDate: z.string(),   // "YYYY-MM-DD"
  openingTime: z.string(), // "HH:mm"
  closingTime: z.string(), // "HH:mm"
  slotDurationMinutes: z.number(),
  breakDurationMinutes: z.number(),
  daysOfWeek: z.array(z.string()), // Ej: ["MONDAY", "TUESDAY"]
});

export type CreateFieldScheduleRequest = z.infer<typeof CreateFieldScheduleRequestSchema>;