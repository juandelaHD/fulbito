import { FieldsTable} from "@/components/tables/FieldsTable"
import { FieldsFilters, FieldsFiltersContainer } from "@/components/filters/FieldsFilters"
import { useState } from "react"
import { toast } from "react-hot-toast"
import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { useGetFields } from "@/services/FieldServices";
import {GetFieldsRequest} from "@/models/GetFields.ts";

import type { Field as FieldForTable } from "@/components/tables/FieldsTable";
import {BASE_API_URL} from "@/config/app-query-client.ts";

export const FieldsScreen = () => {
  const [filters, setFilters] = useState<FieldsFilters>({
    name: "",
    zone: "",
    address: "",
    grassType: "",
    isIlluminated: false,
    hasOpenScheduledMatch: false,
  });

  const {
    data: fetchedFields,
    refetch,
    isFetching,
  } = useGetFields({
    name: filters.name || undefined,
    zone: filters.zone || undefined,
    address: filters.address || undefined,
    grassType:
      filters.grassType === "natural"
        ? "NATURAL_GRASS"
        : filters.grassType === "synthetic"
        ? "SYNTHETIC_TURF"
        : filters.grassType === "mixed"
        ? "HYBRID_TURF"
        : undefined,
    isIlluminated: filters.isIlluminated || undefined,
    hasOpenScheduledMatch: filters.hasOpenScheduledMatch || undefined,
    page: 0,
    size: 50,
  } as GetFieldsRequest);

  const handleSearch = async () => {
    const payload = {
      name: filters.name,
      zone: filters.zone,
      address: filters.address,
      grassType:
        filters.grassType === "natural"
          ? "NATURAL_GRASS"
          : filters.grassType === "synthetic"
          ? "SYNTHETIC_TURF"
          : filters.grassType === "hybrid"
          ? "HYBRID_TURF"
          : undefined,
      isIlluminated: filters.isIlluminated,
      hasOpenScheduledMatch: filters.hasOpenScheduledMatch,
      page: 0,
      size: 50,
    };

    console.log("📦 Payload for field search:", payload);
    await refetch(); 
  };

  const rowsForTable: FieldForTable[] = fetchedFields?.content?.map((item) => {
    const idNum = Number(item.id);
    const photoUrl =
        item.imageIds && item.imageIds.length > 0
            ? `${BASE_API_URL}/images/${item.imageIds[0]}`
            : "";

    return {
      id: idNum,
      name: item.name,
      grassType:
          item.grassType === "NATURAL_GRASS"
              ? "Natural"
              : item.grassType === "SYNTHETIC_TURF"
              ? "Synthetic"
              : "Hybrid",
      lighting: item.illuminated ? "Illuminated" : "No lighting",
      zone: item.location.zone,
      address: item.location.address,
      photos: photoUrl,
    };
  }) || [];

  return (
    <CommonLayout>
      <div className="w-[1040px] mx-auto px-4">
        <h1 className="text-2xl font-bold">Search through our Available Fields</h1>
        <FieldsFiltersContainer filters={filters} setFilters={setFilters} onSearch={handleSearch} />
        {isFetching && <p className="text-sm text-gray-500">Loading...</p>}

        {/* Si la respuesta trae contenido, paso el array mapeado a la tabla */}
        {!isFetching && rowsForTable.length > 0 && (
            <FieldsTable
                data={rowsForTable}
                onReserve={(f) =>
                    toast.error(
                        `⚠️ Reservations are not yet implemented for: ${f.name}`
                    )
                }
            />
        )}
      </div>
    </CommonLayout>
  );
};
