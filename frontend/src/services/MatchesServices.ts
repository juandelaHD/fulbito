// src/services/MatchesServices.ts
import { toast } from "react-hot-toast";
import { useMutation, useQuery } from "@tanstack/react-query";
import { BASE_API_URL } from "@/config/app-query-client";
import { useToken } from "@/services/TokenContext.tsx";
import { handleErrorResponse } from "@/services/ApiUtils.ts";

export type RawMatchDTO = {
  id: number;
  field: {
    id: number;
    name: string;
    grassType: string;
    illuminated: boolean;
    location: {
      zone: string;
      address: string;
    };
    imageIds: number[];
  };
  organizer: {
    id: number;
    firstName: string;
    lastName: string;
    username: string;
    avatarId: number;
    zone: string;
    age: number;
    gender: string;
    role: string;
    emailConfirmed: boolean;
  };
  players: Array<{
    id: number;
    firstName: string;
    lastName: string;
    username: string;
    avatarId: number;
    zone: string;
    age: number;
    gender: string;
    role: string;
    emailConfirmed: boolean;
  }>;
  status: string;
  matchType: string;
  minPlayers: number;
  maxPlayers: number;
  date: string;
  startTime: string;
  endTime: string;
  confirmationSent: boolean;
};

export async function getOpenMatchesService(token: string): Promise<RawMatchDTO[]> {
  const response = await fetch(`${BASE_API_URL}/matches/open-available`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: "application/json",
    },
  });

  if (!response.ok) {
    await handleErrorResponse(response, "fetching open matches");
  }
  return (await response.json()) as RawMatchDTO[];
}

export function useGetOpenMatches() {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useQuery<RawMatchDTO[], Error>({
    queryKey: ["openMatches"],
    queryFn: async () => getOpenMatchesService(token),
    enabled: token !== "",
  });
}

export async function joinMatchService(matchId: number, token: string): Promise<RawMatchDTO> {
  const response = await fetch(`${BASE_API_URL}/matches/${matchId}/join`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: "application/json",
    },
  });

  if (!response.ok) {
    await handleErrorResponse(response, "joining match");
  }
  return (await response.json()) as RawMatchDTO;
}

export function useJoinMatch() {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useMutation({
    mutationFn: async (matchId: number) => joinMatchService(matchId, token),
    onSuccess: () => {
      toast.success("Inscripción exitosa 🎉");
    },
    onError: () => {
      toast.error("Error al inscribirse. Por favor, intenta de nuevo.");
    },
  });
}
