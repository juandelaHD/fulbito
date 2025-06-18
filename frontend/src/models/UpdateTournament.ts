import { z } from "zod"
import { TournamentFormatEnum, TournamentStatusEnum } from "./GetAvailableTournaments" 

// === REQUEST ===
export const UpdateTournamentRequestSchema = z.object({
  name: z.string().min(1),
  startDate: z.string(), // formato ISO
  endDate: z.string(),
  format: TournamentFormatEnum,
  maxTeams: z.number().int().min(2),
  rules: z.string().min(1),
  prizes: z.string().min(1),
  registrationFee: z.number().nonnegative(),
})

export type UpdateTournamentRequest = z.infer<typeof UpdateTournamentRequestSchema>

// === RESPONSE ===
export const UpdateTournamentResponseSchema = z.object({
  id: z.number(),
  name: z.string(),
  startDate: z.string(),
  endDate: z.string(),
  format: TournamentFormatEnum,
  maxTeams: z.number(),
  status: TournamentStatusEnum,
  rules: z.string(),
  organizer: z.object({
    id: z.number(),
    firstName: z.string(),
    lastName: z.string(),
    username: z.string(),
    avatarUrl: z.string(),
    zone: z.string(),
    age: z.number(),
    gender: z.string(),
    role: z.string(),
    emailConfirmed: z.boolean(),
    activeUser: z.boolean(),
  }),
})

export type UpdateTournamentResponse = z.infer<typeof UpdateTournamentResponseSchema>
