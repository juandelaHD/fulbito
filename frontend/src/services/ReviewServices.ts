import { toast } from "react-hot-toast"
import { useMutation, useQuery } from "@tanstack/react-query"
import { BASE_API_URL } from "@/config/app-query-client"
import { useToken } from "@/services/TokenContext"
import { handleErrorResponse } from "@/services/ApiUtils"
import {
  GetReviewsResponseSchema,
  GetReviewsResponse,
  CreateReviewRequest,
} from "@/models/GetReviews"

export function useCreateReview(fieldId: number) {
  const [tokenState] = useToken()
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : ""

  return useMutation({
    mutationFn: async (req: CreateReviewRequest) => {
      const res = await fetch(`${BASE_API_URL}/fields/${fieldId}/reviews`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(req),
      })

      if (!res.ok) {
        await handleErrorResponse(res, "creating review")
      } else {
        toast.success("Review created successfully!", { duration: 4000 })
      }
    },
  })
}

export function useGetReviews(fieldId: number) {
  const [tokenState] = useToken()
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : ""

  return useQuery<GetReviewsResponse, Error>({
    queryKey: ["reviews", fieldId],
    queryFn: async () => {
      const url = `${BASE_API_URL}/fields/${fieldId}/reviews`

      try {
        const response = await fetch(url, {
          headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
          },
        })

        const json = await response.json()

        if (!response.ok) {
          await handleErrorResponse(response, "fetching reviews")
        }

        const parsed = GetReviewsResponseSchema.parse(json)

        if (parsed.content.length === 0) {
          toast("No reviews found for this field.", {
            icon: "ℹ️",
            duration: 4000,
          })
        }

        return parsed
      } catch (err) {
        console.error("Error fetching reviews:", err)
        throw err
      }
    },
  })
}
