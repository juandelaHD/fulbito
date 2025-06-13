import { z } from "zod"

export const TournamentStatusEnum = z.enum(["OPEN", "ONGOING", "FINISHED"])
export type TournamentStatus = z.infer<typeof TournamentStatusEnum>

export const TournamentFormatEnum = z.enum([
  "SINGLE_ELIMINATION",
  "GROUPS_AND_ELIMINATION",
  "ROUND_ROBIN",
])
export type TournamentFormat = z.infer<typeof TournamentFormatEnum>

export type GetTournamentsRequest = {
  name?: string
  status?: TournamentStatus
}

export const TournamentSchema = z.object({
  id: z.number(),
  name: z.string(),
  startDate: z.string(), // ISO string
  format: TournamentFormatEnum,
  status: TournamentStatusEnum,
})

export type Tournament = z.infer<typeof TournamentSchema>

export const GetTournamentsResponseSchema = z.object({
  content: z.array(TournamentSchema),
  totalElements: z.number(),
  totalPages: z.number(),
  number: z.number(), // current page
  size: z.number(),   // page size
})

export type GetTournamentsResponse = z.infer<typeof GetTournamentsResponseSchema>
