import { z } from "zod"

// === User (organizer, captain, members) ===
export const UserSchema = z.object({
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

// === Team ===
export const TeamSchema = z.object({
  id: z.number(),
  name: z.string(),
  imageUrl: z.string().url(),
  mainColor: z.string(),
  secondaryColor: z.string(),
  ranking: z.number(),
  captain: UserSchema,
  members: z.array(UserSchema),
})

// === Owned Tournament ===
export const GetOwnedTournamentsResponseSchema = z.array(
  z.object({
    id: z.number(),
    name: z.string(),
    startDate: z.string(),
    endDate: z.string(),
    format: z.enum(["SINGLE_ELIMINATION", "GROUP_STAGE_WITH_ELIMINATION", "ROUND_ROBIN"]),
    maxTeams: z.number(),
    status: z.enum(["OPEN_FOR_REGISTRATION", "IN_PROGRESS", "FINISHED", "CANCELLED"]),
    rules: z.string(),
    prizes: z.string(),
    registrationFee: z.number(),
    organizer: UserSchema,
    registeredTeams: z.array(TeamSchema),
  })
)

// === Types ===
export type TournamentOrganizer = z.infer<typeof UserSchema>
export type RegisteredTeam = z.infer<typeof TeamSchema>
export type OwnedTournament = z.infer<typeof GetOwnedTournamentsResponseSchema>[number]
