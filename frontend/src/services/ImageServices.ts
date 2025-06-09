import { BASE_API_URL } from "@/config/app-query-client";
import {handleErrorResponse} from "@/services/ApiUtils.ts";
import { useEffect, useState } from "react";
import {useToken} from "@/services/TokenContext.tsx";

export function useImageById(imageEndpoint?: string) {
    const [url, setUrl] = useState<string | undefined>();
    const [tokenState] = useToken();
    const token = tokenState.state === "LOGGED_IN" ? tokenState.accessToken : "";

    useEffect(() => {
        if (!imageEndpoint) return;

        let isActive = true;
        let currentUrl: string;

        fetchImageBlobById(imageEndpoint, token)
            .then((blobUrl) => {
                if (isActive) {
                    currentUrl = blobUrl;
                    setUrl(blobUrl);
                }
            })
            .catch(() => {
                if (isActive) setUrl(undefined);
            });

        return () => {
            isActive = false;
            if (currentUrl) {
                URL.revokeObjectURL(currentUrl);
            }
        };
    }, [imageEndpoint, token]);

    return url;
}

export async function fetchImageBlobById(imageEndpoint: string, token: string): Promise<string> {
    const response = await fetch(`${BASE_API_URL}${imageEndpoint}`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        await handleErrorResponse(response, "fetching image");
    }

    const blob = await response.blob();
    return URL.createObjectURL(blob);
}
