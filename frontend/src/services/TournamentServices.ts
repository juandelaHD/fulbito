import { toast } from "react-hot-toast"
import { useMutation, useQuery } from "@tanstack/react-query"
import { BASE_API_URL } from "@/config/app-query-client"
import { useToken } from "@/services/TokenContext"
import { handleErrorResponse } from "@/services/ApiUtils"

import {
  CreateTournamentRequest,
  CreateTournamentResponseSchema,
} from "@/models/CreateTournament"

import {
  GetAvailableTournamentsRequest,
  GetAvailableTournamentsResponse,
  GetAvailableTournamentsResponseSchema,
} from "@/models/GetAvailableTournaments"

import {
  UpdateTournamentRequest,
  UpdateTournamentResponseSchema,
} from "@/models/UpdateTournament"

import {
  RegisterTeamToTournamentParams,
  RegisterTeamToTournamentResponseSchema,
} from "@/models/RegisterTeamToTournament"

import {
  DeleteTournamentParams,
  DeleteTournamentResponseSchema,
} from "@/models/DeleteTournament"

// ================== CREATE ==================
export function useCreateTournament() {
  const [tokenState] = useToken()
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : ""

  return useMutation({
    mutationFn: async (req: CreateTournamentRequest) => {
      return await createTournamentService(req, token)
    },
  })
}

export async function createTournamentService(req: CreateTournamentRequest, token: string) {
  const response = await fetch(`${BASE_API_URL}/tournaments`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: JSON.stringify(req),
  })

  const json = await response.json()

  if (!response.ok) {
    await handleErrorResponse(response, "creating tournament")
  }

  toast.success("Tournament created successfully", { duration: 5000 })
  return CreateTournamentResponseSchema.parse(json)
}

// ================== GET AVAILABLE ==================
export function useGetAvailableTournaments(filters: GetAvailableTournamentsRequest) {
  const [tokenState] = useToken()
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : ""

  return useQuery<GetAvailableTournamentsResponse, Error>({
    queryKey: ["availableTournaments", filters],
    queryFn: async () => {
      const params = new URLSearchParams()
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined) params.append(key, String(value))
      })

      const url = `${BASE_API_URL}/tournaments/available?${params.toString()}`

      const response = await fetch(url, {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      })

      const json = await response.json()

      if (!response.ok) {
        await handleErrorResponse(response, "fetching tournaments")
      }

      const parsed = GetAvailableTournamentsResponseSchema.parse(json)

      if (parsed.length === 0) {
        toast("No tournaments matched your search", {
          icon: "ℹ️",
          duration: 4000,
        })
      }

      return parsed
    },
    enabled: false,
  })
}

// ================== UPDATE ==================
export function useUpdateTournament(tournamentId: number) {
  const [tokenState] = useToken()
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : ""

  return useMutation({
    mutationFn: async (req: UpdateTournamentRequest) => {
      const response = await fetch(`${BASE_API_URL}/tournaments/${tournamentId}`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        body: JSON.stringify(req),
      })

      const json = await response.json()

      if (!response.ok) {
        await handleErrorResponse(response, "updating tournament")
      }

      toast.success("Tournament updated successfully", { duration: 5000 })
      return UpdateTournamentResponseSchema.parse(json)
    },
  })
}

// ================== REGISTER TEAM ==================
export function useRegisterTeamToTournament() {
  const [tokenState] = useToken()
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : ""

  return useMutation({
    mutationFn: async ({ tournamentId, teamId }: RegisterTeamToTournamentParams) => {
      const url = `${BASE_API_URL}/tournaments/${tournamentId}/register?teamId=${teamId}`

      const response = await fetch(url, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      })

      const json = await response.json().catch(() => ({}))

      if (!response.ok) {
        await handleErrorResponse(response, "registering team to tournament")
      }

      toast.success("Team registered successfully", { duration: 5000 })
      return RegisterTeamToTournamentResponseSchema.parse(json)
    },
  })
}

// ================== DELETE ==================
export function useDeleteTournament() {
  const [tokenState] = useToken()
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : ""

  return useMutation({
    mutationFn: async ({ tournamentId, confirm }: DeleteTournamentParams) => {
      const url = `${BASE_API_URL}/tournaments/${tournamentId}?confirm=${String(confirm)}`

      const response = await fetch(url, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      })

      if (!response.ok) {
        const json = await response.json().catch(() => ({}))
        await handleErrorResponse(response, "deleting tournament")
      }

      toast.success("Tournament deleted successfully", { duration: 5000 })
      return DeleteTournamentResponseSchema.parse({})
    },
  })
}
