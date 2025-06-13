import { toast } from "react-hot-toast"
import { useQuery } from "@tanstack/react-query"
import { BASE_API_URL } from "@/config/app-query-client"
import { useToken } from "@/services/TokenContext"
import {
  GetTournamentsRequest,
  GetTournamentsResponse,
  GetTournamentsResponseSchema,
} from "@/models/GetTournaments"
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

export function useGetTournaments(filters: GetTournamentsRequest) {
  const [tokenState] = useToken()
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : ""

  return useQuery<GetTournamentsResponse, Error>({
    queryKey: ["tournaments", filters],
    queryFn: async ({ queryKey }) => {
      const [, rawFilters] = queryKey
      const filters = rawFilters as GetTournamentsRequest
      const params = new URLSearchParams()

      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== "") {
          params.append(key, String(value))
        }
      })

      const url = `${BASE_API_URL}/tournaments/filters?${params.toString()}`

      try {
        const response = await fetch(url, {
          headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
          },
        })

        const json = await response.json()

        if (!response.ok) {
          toast.error("Error fetching tournaments")
          throw new Error(json.message || "Unknown error")
        }

        const parsed = GetTournamentsResponseSchema.parse(json)

        if (parsed.content.length === 0) {
          toast("No tournaments matched your search.", {
            icon: "⚠️",
            duration: 4000,
          })
        }

        return parsed
      } catch (err) {
        console.error("Error fetching tournaments:", err)
        throw err
      }
    },
    enabled: false,
  })
}
