// MatchServices.ts (actualizado)
import { useMutation } from "@tanstack/react-query";
import { toast } from "react-hot-toast";

import { BASE_API_URL } from "@/config/app-query-client";
import { useToken } from "@/services/TokenContext";


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

      toast.success("Match created successfully", { duration: 5000 });
      return res.json();
    },
  });
}
