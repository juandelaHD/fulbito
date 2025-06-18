import { toast } from "react-hot-toast"
import { useQuery } from "@tanstack/react-query"
import { BASE_API_URL } from "@/config/app-query-client"
import { useToken } from "@/services/TokenContext"
import {
  GetAvailableTournamentsRequest,
  GetAvailableTournamentsResponse,
  GetAvailableTournamentsResponseSchema,
} from "@/models/GetAvailableTournaments"
import { useMutation } from "@tanstack/react-query"
import { handleErrorResponse } from "@/services/ApiUtils"
import { CreateTournamentRequest, CreateTournamentResponseSchema } from "@/models/CreateTournament"

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

  const parsed = CreateTournamentResponseSchema.parse(json)

  toast.success("Tournament created successfully!", { duration: 5000 })
  return parsed
}

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

      const res = await fetch(url, {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      })

      const json = await res.json()
      if (!res.ok) {
        throw new Error(json.message || "Failed to fetch available tournaments")
      }

      return GetAvailableTournamentsResponseSchema.parse(json)
    },
  })
}
