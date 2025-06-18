import { z } from "zod"

// === Enums del backend ===
export const TournamentStatusEnum = z.enum([
  "OPEN_FOR_REGISTRATION",
  "IN_PROGRESS",
  "FINISHED",
  "CANCELLED",
])
export type TournamentStatus = z.infer<typeof TournamentStatusEnum>

export const TournamentFormatEnum = z.enum([
  "SINGLE_ELIMINATION",
  "GROUP_STAGE_WITH_ELIMINATION",
  "ROUND_ROBIN",
])
export type TournamentFormat = z.infer<typeof TournamentFormatEnum>

// === Request schema ===
export const GetAvailableTournamentsRequestSchema = z.object({
  organizerUsername: z.string().optional(),
  openForRegistration: z.boolean().optional(),
})
export type GetAvailableTournamentsRequest = z.infer<typeof GetAvailableTournamentsRequestSchema>

// === Organizer ===
const OrganizerSchema = z.object({
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
})

// === Tournament ===
export const TournamentAvailableSchema = z.object({
  id: z.number(),
  name: z.string(),
  startDate: z.string(),
  endDate: z.string(),
  format: TournamentFormatEnum,
  maxTeams: z.number(),
  status: TournamentStatusEnum,
  rules: z.string(),
  organizer: OrganizerSchema,
})
export type TournamentAvailable = z.infer<typeof TournamentAvailableSchema>

// === Response schema ===
export const GetAvailableTournamentsResponseSchema = z.array(TournamentAvailableSchema)
export type GetAvailableTournamentsResponse = z.infer<typeof GetAvailableTournamentsResponseSchema>
