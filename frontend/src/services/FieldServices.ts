import { toast } from "react-hot-toast";
import { useMutation, useQuery } from "@tanstack/react-query";
import { BASE_API_URL } from "@/config/app-query-client";
import {CreateFieldRequest, CreateFieldResponseSchema} from "@/models/CreateField.ts";
import {useToken} from "@/services/TokenContext.tsx";
import {handleErrorResponse} from "@/services/ApiUtils.ts";
import {GetFieldsRequest, GetFieldsResponse, GetFieldsResponseSchema} from "@/models/GetFields.ts";

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

  return useQuery<GetFieldsResponse>({
    queryKey: ["fields", filters],
    queryFn: async () => {
      const params = new URLSearchParams();

      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== "") {
          params.append(key, String(value));
        }
      });

      const response = await fetch(`${BASE_API_URL}/fields?${params.toString()}`, {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      });

      if (response.ok) {
        const json = await response.json();
        toast.success("Fields fetched successfully", { duration: 5000 });
        return GetFieldsResponseSchema.parse(json);
      } else {
        await handleErrorResponse(response, "fetching fields");
      }
    },
    enabled: false,
  });
}