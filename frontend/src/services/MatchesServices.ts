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
    enabled: boolean;
    imagesUrls: string[];
    matchesWithMissingPlayers: Record<string, number>;
  };
  organizer: {
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
  players: Array<{
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
  }>;
  homeTeam?: {
    id: number;
    name: string;
    imageUrl: string;
    mainColor: string;
    secondaryColor: string;
    ranking: number;
    captain: {
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
    members: Array<{
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
    }>;
  };
  awayTeam?: {
    id: number;
    name: string;
    imageUrl: string;
    mainColor: string;
    secondaryColor: string;
    ranking: number;
    captain: {
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
    members: Array<{
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
    }>;
  };
  status: string;
  matchType: string;
  minPlayers: number;
  maxPlayers: number;
  date: string;
  startTime: string;
  endTime: string;
  confirmationSent: boolean;
  invitation?: {
    token: string;
    matchId: number;
    valid: boolean;
  };
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
    queryFn: () => getOpenMatchesService(token),
    enabled: token !== "",
  });
}

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

export function useJoinMatch() {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useMutation({
    mutationFn: (matchId: number) => joinMatchService(matchId, token),
    onSuccess: () => {
      toast.success("Successfully joined match!", { duration: 5000 });
    },
    onError: (err: unknown) => {
      console.log("Error while joining match:", err);
    },
  });
}

export async function getMatchInviteLinkService(matchId: number, token: string): Promise<string> {
  const response = await fetch(`${BASE_API_URL}/matches/${matchId}/link-invite`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: "application/json",
    },
  });

  if (!response.ok) {
    await handleErrorResponse(response, "fetching invite link");
  }
  return await response.text();
}


export function useGetMatchInviteLink() {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useMutation({
    mutationFn: (matchId: number) => getMatchInviteLinkService(matchId, token),
  });
}


export type CreateMatchPayload = {
  matchType: string;
  fieldId: number;
  minPlayers: number;
  maxPlayers: number;
  date: string; // yyyy-MM-dd
  startTime: string; // yyyy-MM-ddTHH:mm:ss
  endTime: string;
};

export function useCreateMatch() {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useMutation({
    mutationFn: async (match: CreateMatchPayload) => {
      const res = await fetch(`${BASE_API_URL}/matches/create`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(match),
      });

      if (!res.ok) {
        const errorMessage = await res.text();
        toast.error(`Error creating match: ${errorMessage}`, { duration: 5000 });
        throw new Error("Error creating match");
      }

      toast.success("Match created successfully!", { duration: 5000 });
      return res.json();
    },
  });
}



export type TeamFormationRequestDTO = {
  strategy: string;
  teamAPlayerIds?: number[];
  teamBPlayerIds?: number[];
};

export async function formTeamsService(
  matchId: number,
  payload: TeamFormationRequestDTO,
  token: string
) {
  const response = await fetch(`${BASE_API_URL}/matches/${matchId}/form-teams`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    await handleErrorResponse(response, "forming teams");
  }
  return await response.json();
}

export function useFormTeams() {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useMutation({
    mutationFn: ({ matchId, payload }: { matchId: number; payload: TeamFormationRequestDTO }) =>
      formTeamsService(matchId, payload, token),
    onSuccess: () => {
      toast.success("Teams formed successfully!", { duration: 5000 });
    },
    onError: (err: unknown) => {
      console.error("Error while forming teams:", err);
      toast.error("Error forming teams. Please try again.", { duration: 5000 });
    },
  });
}


export async function getMatchByIdService(matchId: number, token: string): Promise<RawMatchDTO> {
  const response = await fetch(`${BASE_API_URL}/matches/${matchId}`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: "application/json",
    },
  });

  if (!response.ok) {
    await handleErrorResponse(response, "fetching match by id");
  }
  return await response.json();
}

export function useGetMatchById(matchId?: number) {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useQuery<RawMatchDTO, Error>({
    queryKey: ["match", matchId],
    queryFn: () => getMatchByIdService(matchId!, token),
    enabled: !!token && !!matchId,
  });
}