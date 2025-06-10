import { TeamCreateRequest } from "@/models/CreateTeam";
import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-hot-toast";
import { BASE_API_URL } from "@/config/app-query-client";
import { useToken } from "@/services/TokenContext.tsx";


export function useCreateTeam() {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useMutation({
    mutationFn: async (req: TeamCreateRequest) => {
      const formData = new FormData();

      const teamPayload: any = {
        name: req.name,
        mainColor: req.mainColor,
        secondaryColor: req.secondaryColor,
        ranking: req.ranking,
      };

      formData.append("team", JSON.stringify(teamPayload));

      if (req.logo instanceof File) {
        formData.append("image", req.logo);
      }

      console.log(req.logo);
      const response = await fetch(`${BASE_API_URL}/teams/create`, {
        method: "POST",
        headers: {
          "Accept": "application/json",
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        body: formData,
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        toast.error(`Error creating team: ${errorMessage}`, { duration: 5000 });
        throw new Error(errorMessage);
      }

      const json = await response.json();
      toast.success("Team created successfully!");

      return json;
    }
  });
}

export function useGetMyTeams() {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useQuery({
    queryKey: ["teams"],
    queryFn: async () => {
      const response = await fetch(`${BASE_API_URL}/teams/my`, {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        toast.error(`Error fetching teams: ${errorMessage}`, { duration: 5000 });
        throw new Error("Error fetching teams");
      }

      return response.json();
    },
    enabled: !!token,
  });
}