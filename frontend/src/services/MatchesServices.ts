// src/services/MatchesServices.ts
import { useQuery, useMutation } from "@tanstack/react-query";
import { toast } from "react-hot-toast";
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
  };
  players: Array<{
    id: number;
    firstName: string;
    lastName: string;
    username: string;
  }>;
  status: string;
  matchType: string;
  minPlayers: number;
  maxPlayers: number;
  date: string;
  startTime: string;
  endTime: string;
};

// 1) Función que hace el fetch de partidos abiertos
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

// 2) Función que hace el “join” a un partido
export async function joinMatchService(matchId: number, token: string): Promise<void> {
  const response = await fetch(`${BASE_API_URL}/matches/${matchId}/join`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    await handleErrorResponse(response, "joining match");
  }
}

// 3) Hook para “GET /matches/open-available”
export function useGetOpenMatches() {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useQuery<RawMatchDTO[], Error>({
    queryKey: ["openMatches"],
    queryFn: () => getOpenMatchesService(token),
    enabled: token !== "",
  });
}

// 4) Hook para “join” a un partido
export function useJoinMatch() {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useMutation({
    mutationFn: (matchId: number) => joinMatchService(matchId, token),
    onSuccess: () => {
      toast.success("Inscripción exitosa 🎉");
    },
    onError: (err: unknown) => {
      console.error("❌ Error al inscribirse al partido:", err);
      toast.error("Error al inscribirse. Por favor, intenta de nuevo.");
    },
  });
}
