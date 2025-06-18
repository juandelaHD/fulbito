import { z } from "zod"

// Parámetros necesarios
export const RegisterTeamToTournamentParamsSchema = z.object({
  tournamentId: z.number().int().min(1),
  teamId: z.number().int().min(1),
})
export type RegisterTeamToTournamentParams = z.infer<typeof RegisterTeamToTournamentParamsSchema>

// Respuesta vacía exitosa
export const RegisterTeamToTournamentResponseSchema = z.object({})
export type RegisterTeamToTournamentResponse = z.infer<typeof RegisterTeamToTournamentResponseSchema>
