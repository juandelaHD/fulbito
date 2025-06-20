import { TeamCreateRequest } from "@/models/CreateTeam.ts";
import { TeamEditRequest } from "@/models/EditTeam.ts";
import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-hot-toast";
import { BASE_API_URL } from "@/config/app-query-client";
import { useToken } from "@/services/TokenContext.tsx";
import { RawTeamDTO } from "@/services/UserServices.ts";

export type TeamMember = {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  avatarUrl: string;
  zone: string;
  age: number;
  gender: string;
  role: string;
  emailConfirmed: boolean;
};

export type Team = {
  id: number;
  name: string;
  imageUrl: string;
  mainColor: string;
  secondaryColor: string;
  ranking: number;
  captain: TeamMember;
  members: TeamMember[];
};

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

export function useUpdateTeam(){
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";
  //
  return useMutation({
    mutationFn: async (req: TeamEditRequest) => {
      const formData = new FormData();

      formData.append("id",String(req.id));

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
      const response = await fetch(`${BASE_API_URL}/teams/${req.id}`, {
        method: "PUT",
        headers: {
          "Accept": "application/json",
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        body: formData,
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        //toast.error(`Error updating team: ${errorMessage}`, { duration: 5000 });
        throw new Error(errorMessage);
      }

      const json = await response.json();
      //toast.success("Team updated successfully!");

      return json;
    }
  });
}

export function useGetMyTeams(options?: { enabled?: boolean }) {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useQuery<RawTeamDTO[]>({
    queryKey: ["teams"],
    queryFn: async () => {
      const response = await fetch(`${BASE_API_URL}/teams/owned`, {
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
    enabled: options?.enabled ?? !!token,
  });
}

export function useGetTeams() {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useQuery({
    queryKey: ["teams"],
    queryFn: async () => {
      const response = await fetch(`${BASE_API_URL}/teams`, {
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

export function useEditTeam(id: String){
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";
  //
  return useQuery<RawTeamDTO>({
    queryKey: ["teamEdit"],
    queryFn: async () => {
      const response = await fetch(`${BASE_API_URL}/teams/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        toast.error(`Error fetching team: ${errorMessage}`, { duration: 5000 });
        throw new Error("Error fetching team");
      }

      return response.json();
    }
  });
}
