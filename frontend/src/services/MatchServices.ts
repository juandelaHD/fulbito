// MatchServices.ts (actualizado)
import { useMutation } from "@tanstack/react-query";
import { toast } from "react-hot-toast";
import { useEffect, useState } from "react";
import { BASE_API_URL } from "@/config/app-query-client";
import { useToken } from "@/services/TokenContext";



// ✅ Tipo final que espera el backend
export type CreateMatchPayload = {
  matchType: string;
  fieldId: number;
  minPlayers: number;
  maxPlayers: number;
  date: string; // yyyy-MM-dd
  startTime: string; // yyyy-MM-ddTHH:mm:ss
  endTime: string;
  organizerId: number;
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

export function useAvailableFields() {
  const [fields, setFields] = useState<{ id: number; name: string }[]>([]);
  const [loadingFields, setLoadingFields] = useState(true);
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  useEffect(() => {
    const fetchFields = async () => {
      try {
        const res = await fetch(`${BASE_API_URL}/fields`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!res.ok) throw new Error("Error fetching fields");
        const data = await res.json();
        setFields(data.map((f: any) => ({ id: f.id, name: f.name })));
      } catch (e) {
        toast.error("Error loading fields");
      } finally {
        setLoadingFields(false);
      }
    };
    fetchFields();
  }, [token]);

  return { fields, loadingFields };
}
