import { BASE_API_URL } from "@/config/app-query-client"
import { useMutation } from "@tanstack/react-query"
import { useToken } from "@/services/TokenContext"
import { toast } from "react-hot-toast"
import { handleErrorResponse } from "@/services/ApiUtils"
import { CreateReviewRequest } from "@/models/CreateReview"

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
        toast.success("Review created successfully")
      }
    },
  })
}
