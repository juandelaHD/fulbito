import { z } from "zod"
import { TournamentFormatEnum, TournamentStatusEnum } from "./GetAvailableTournaments" 

// === REQUEST ===
export const UpdateTournamentRequestSchema = z.object({
  name: z.string(),
  startDate: z.string(),
  endDate: z.string(),
  format: z.enum(["SINGLE_ELIMINATION", "GROUP_STAGE_WITH_ELIMINATION", "ROUND_ROBIN"]),
  maxTeams: z.number(),
  rules: z.string(),
  prizes: z.string(),
  registrationFee: z.number(),
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
