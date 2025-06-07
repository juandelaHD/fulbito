import { toast } from "react-hot-toast";
import { useMutation, useQuery } from "@tanstack/react-query";
import { BASE_API_URL } from "@/config/app-query-client";
import {CreateFieldRequest, CreateFieldResponseSchema} from "@/models/CreateField.ts";
import {useToken} from "@/services/TokenContext.tsx";
import {handleErrorResponse} from "@/services/ApiUtils.ts";
import {GetFieldsRequest, GetFieldsResponse, GetFieldsResponseSchema} from "@/models/GetFields.ts";
import { useEffect, useState } from "react";

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

    console.log("Requesting fields with filters:", filters);

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

        console.log("Fields response:", json);

        if (!response.ok) {
          toast.error("Failed to fetch fields. Please try again later.");
          throw new Error(json.message || "Unknown error");
        }

        return GetFieldsResponseSchema.parse(json);
      } catch (err) {
        console.error("Error fetching fields:", err);
        throw err;
      }
    },
    enabled: false,
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
        setFields(data.content.map((f: any) => ({ id: f.id, name: f.name })));
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