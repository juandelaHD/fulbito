import { z } from "zod"

// Parámetros necesarios para eliminar un torneo
export const DeleteTournamentParamsSchema = z.object({
  tournamentId: z.number().int().min(1),
  confirm: z.literal(true), // obligatorio que sea true
})
export type DeleteTournamentParams = z.infer<typeof DeleteTournamentParamsSchema>

// Respuesta vacía (204)
export const DeleteTournamentResponseSchema = z.object({})
export type DeleteTournamentResponse = z.infer<typeof DeleteTournamentResponseSchema>