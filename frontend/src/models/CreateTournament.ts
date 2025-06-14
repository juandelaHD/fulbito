import { z } from "zod"
import { TournamentFormatEnum, TournamentStatusEnum } from "@/models/GetTournaments"

export const CreateTournamentRequestSchema = z.object({
  name: z.string(),
  startDate: z.string(), // ISO format (ej: "2025-07-01")
  format: TournamentFormatEnum,
  maxTeams: z.number(),

  // opcionales
  endDate: z.string().optional(),
  description: z.string().optional(),
  prizes: z.string().optional(),
  entryFee: z.number().optional(),
})

export type CreateTournamentRequest = z.infer<typeof CreateTournamentRequestSchema>


export const CreateTournamentResponseSchema = z.object({
  id: z.number(),
  name: z.string(),
  startDate: z.string(),
  format: TournamentFormatEnum,
  maxTeams: z.number(),
  status: TournamentStatusEnum,
})

export type CreateTournamentResponse = z.infer<typeof CreateTournamentResponseSchema>
