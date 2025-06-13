import { toast } from "react-hot-toast"
import { useQuery } from "@tanstack/react-query"
import { BASE_API_URL } from "@/config/app-query-client"
import { useToken } from "@/services/TokenContext"
import {
  GetTournamentsRequest,
  GetTournamentsResponse,
  GetTournamentsResponseSchema,
} from "@/models/GetTournaments"

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
