import { toast } from "react-hot-toast";
import { useMutation, useQuery } from "@tanstack/react-query";
import { BASE_API_URL } from "@/config/app-query-client";
import {CreateFieldRequest, CreateFieldResponseSchema} from "@/models/CreateField.ts";
import {useToken} from "@/services/TokenContext.tsx";
import {handleErrorResponse} from "@/services/ApiUtils.ts";
import {GetFieldsRequest, GetFieldsResponse, GetFieldsResponseSchema} from "@/models/GetFields.ts";
import { useEffect, useState } from "react";
import { RawMatchDTO } from "@/services/UserServices.ts";

export function useCreateField() {
    const [tokenState] = useToken();
    const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

    return useMutation({
        mutationFn: async (req: CreateFieldRequest) => {
            await createFieldService(req, token);
        }
    });
}

export async function createFieldService(req: CreateFieldRequest, token: string) {
    const formData = new FormData();

    formData.append("field", JSON.stringify({
        name:       req.name,
        grassType:  req.grassType,
        illuminated:req.illuminated,
        zone:    req.zone,
        address: req.address,
    }));

    if (req.photos && req.photos.length > 0) {
        (Array.from(req.photos) as File[]).forEach((file: File) => {
            formData.append("images", file);
        });
    }

    const response = await fetch(`${BASE_API_URL}/fields`, {
        method: "POST",
        body: formData,
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (response.ok) {
      const json = await response.json();
      toast.success("Field created successfully", { duration: 5000 });
      return CreateFieldResponseSchema.parse(json);
    } else {
        await handleErrorResponse(response, "creating field")
    }
}

export function useGetFields(filters: GetFieldsRequest) {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useQuery<GetFieldsResponse, Error>({
    queryKey: ["fields", filters],
    queryFn: async ({ queryKey }) => {
    const [, rawFilters] = queryKey;
    const filters = rawFilters as GetFieldsRequest;
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== "") {
        params.append(key, String(value));
        }
    });
      const url = `${BASE_API_URL}/fields/filters?${params.toString()}`;
      try {
        const response = await fetch(url, {
          headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
          },
        });
        const json = await response.json();
        const parsed = GetFieldsResponseSchema.parse(json);
        if (parsed.content.length === 0) {
        toast("No fields matched your search.", {
            icon: "ℹ️",
            duration: 4000,
        });
        }
        if (!response.ok) {
          toast.error("Failed to fetch fields. Please try again later.");
          throw new Error(json.message || "Unknown error");
        }
        return parsed;
      } catch (err) {
        console.error("Error fetching fields:", err);
        throw err;
      }
    },
    enabled: false,
  });
}

export function useAvailableFields(token: string) {
  const [fields, setFields] = useState<Record<number, string>>({});
  const [loadingFields, setLoadingFields] = useState(true);

  useEffect(() => {
    const fetchFields = async () => {
      try {
        const res = await fetch(`${BASE_API_URL}/fields`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!res.ok) throw new Error("Error fetching fields");
        const data = await res.json();
        const dict: Record<number, string> = {};
        data.content.forEach((f: any) => {
          dict[f.id] = f.name;
        });
        setFields(dict)
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


export type ScheduleSlot = {
  id: number;
  date: string; // "YYYY-MM-DD"
  startTime: string; // "HH:mm:ss"
  endTime: string;   // "HH:mm::ss"
  status: string; // "AVAILABLE"
};

export async function getFieldSchedulesService(fieldId: number, date: string, token: string): Promise<ScheduleSlot[]> {
  const res = await fetch(`${BASE_API_URL}/fields/${fieldId}/schedules/slots?date=${date}`, {
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: "application/json",
    },
  });
  if (!res.ok) throw new Error("Error fetching schedules");
  const slots: ScheduleSlot[] = await res.json();
  return slots
}

export type CreateFieldScheduleRequest = {
  startDate: string; // "YYYY-MM-DD"
  endDate: string;   // "YYYY-MM-DD"
  openingTime: string; // "HH:mm"
  closingTime: string; // "HH:mm"
  slotDurationMinutes: number;
  breakDurationMinutes: number;
  daysOfWeek: string[]; // Ej: ["MONDAY", "TUESDAY"]
};

export async function createFieldScheduleService(
  fieldId: number,
  data: CreateFieldScheduleRequest,
  token: string
): Promise<ScheduleSlot[]> {
  const res = await fetch(`${BASE_API_URL}/fields/${fieldId}/schedules`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error("Error creating schedule");
  return await res.json();
}

export async function updateScheduleSlotStatusService(
  fieldId: number,
  scheduleId: number,
  status: "AVAILABLE" | "BLOCKED",
  token: string
) {
  const res = await fetch(
    `${BASE_API_URL}/fields/${fieldId}/schedules/${scheduleId}/status?status=${status}`,
    {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: "application/json",
      },
    }
  );
  if (!res.ok) throw new Error("Error updating schedule status");
  return await res.json();
}

export async function deleteFieldScheduleService(
  fieldId: number,
  scheduleId: number,
  token: string
): Promise<void> {
  const res = await fetch(
    `${BASE_API_URL}/fields/${fieldId}/schedules/${scheduleId}`,
    {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: "application/json",
      },
    }
  );
  if (!res.ok) throw new Error("Error while deleting schedule");
}

export function useGetOwnedFields() {
    const [tokenState] = useToken();
    const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

    return useQuery<GetFieldsResponse, Error>({
        queryKey: ["ownedFields"],
        queryFn: async () => {
        const response = await fetch(`${BASE_API_URL}/fields/owned`, {
            headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
            },
        });

        if (!response.ok) {
            await handleErrorResponse(response, "fetching owned fields");
        }

        const json = await response.json();
        const parsed =  GetFieldsResponseSchema.parse(json);
        console.log("Owned fields response:", parsed);

        if (parsed.content.length === 0) {
            toast("You don't have any field yet", {
                icon: "ℹ️",
                duration: 4000,
            });
        }

        return parsed;
        }
    });
}

export function useDeleteField() {
    const [tokenState] = useToken();
    const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

    return useMutation({
        mutationFn: async (fieldId: number) => {
            const response = await fetch(`${BASE_API_URL}/fields/${fieldId}`, {
                method: "DELETE",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                await handleErrorResponse(response, "deleting field");
            } else {
                toast.success("Field deleted successfully", { duration: 5000 });
            }
        },
    });
}

export function useUpdateField() {
    const [tokenState] = useToken();
    const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

    return useMutation({
        mutationFn: async ({ id, data }: { id: number; data: FormData }) => {
            const response = await fetch(`${BASE_API_URL}/fields/${id}`, {
                method: "PUT",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                body: data,
            });

            if (!response.ok) {
                await handleErrorResponse(response, "updating field");
            } else {
                toast.success("Field updated successfully");
            }
        },
    });
}

export type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // página actual (0-based)
  size: number;
};

export async function getMatchesByFieldService(
  fieldId: number,
  token: string,
  status?: string,
  day?: string,
  page: number = 0,
  size: number = 10
): Promise<Page<RawMatchDTO>> {
  const params = new URLSearchParams();
  if (status) params.append("status", status);
  if (day) params.append("day", day);
  params.append("page", page.toString());
  params.append("size", size.toString());

  const url = `${BASE_API_URL}/fields/${fieldId}/matches?${params.toString()}`;

  const response = await fetch(url, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: "application/json",
    },
  });

  if (!response.ok) {
    await handleErrorResponse(response, "fetching matches by field");
  }
  return await response.json();
}

export function useGetMatchesByField(
  fieldId?: number,
  status?: string,
  day?: string,
  page: number = 0,
  size: number = 10
) {
  const [tokenState] = useToken();
  const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

  return useQuery<Page<RawMatchDTO>, Error>({
    queryKey: ["fieldMatches", fieldId, status, day, page, size],
    queryFn: () => getMatchesByFieldService(fieldId!, token, status, day, page, size),
    enabled: !!token && !!fieldId,
  });
}