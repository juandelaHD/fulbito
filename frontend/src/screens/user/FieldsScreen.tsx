import { FieldsTable} from "@/components/tables/FieldsTable.tsx"
import { FieldsFilters, FieldsFiltersContainer } from "@/components/filters/FieldsFilters.tsx"
import { useState } from "react"
import { toast } from "react-hot-toast"
import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { useGetFields } from "@/services/FieldServices.ts";
import {GetFieldsRequest} from "@/models/GetFields.ts";
import { ReviewsModal } from "@/components/modals/ReviewsModal.tsx"
import type { Field as FieldForTable } from "@/components/tables/FieldsTable.tsx";
import {ViewFieldMatchesModal} from "@/components/modals/ViewFieldMatches.tsx";

export const FieldsScreen = () => {
  const [hasSearched, setHasSearched] = useState(false);
  const [openMatchNeedsFor, setOpenMatchNeedsFor] = useState<FieldForTable | null>(null);
  const [filters, setFilters] = useState<FieldsFilters>({
    name: "",
    zone: "",
    address: "",
    grassType: "",
    isIlluminated: false,
    hasOpenMatch: false,
    isEnabled: true
  });

  const [openReviewsFor, setOpenReviewsFor] = useState<FieldForTable | null>(null);

  const {
    data: fetchedFields,
    refetch,
    isFetching,
    isError,
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
    hasOpenMatch: filters.hasOpenMatch || undefined,
    isEnabled: filters.isEnabled || undefined,
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
      hasOpenMatch: filters.hasOpenMatch,
      page: 0,
      size: 50,
    };

    setHasSearched(true);
    console.log("Payload for field search:", payload);
    await refetch();
  };

  const rowsForTable: FieldForTable[] = fetchedFields?.content?.map((item) => {
    const idNum = Number(item.id);

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
      imageUrl: Array.isArray(item.imagesUrls) && item.imagesUrls.length > 0 ? item.imagesUrls[0] : undefined,
      matchesWithMissingPlayers: item.matchesWithMissingPlayers ?? null,
    };
  }) || [];

  return (
      <CommonLayout>
        <div className="w-[1040px] mx-auto px-4">
          <h1 className="text-2xl font-bold mb-4">Search through our Available Fields</h1>

          <FieldsFiltersContainer
              filters={filters}
              setFilters={setFilters}
              onSearch={handleSearch}
          />

          {hasSearched && isError && (
              <p className="text-sm text-red-500 mt-4">❌ Error loading fields. Please try again.</p>
          )}

          {hasSearched && isFetching && (
              <p className="text-sm text-gray-500 mt-4">Loading...</p>
          )}

          {hasSearched && !isFetching && !isError && rowsForTable.length > 0 && (
              <FieldsTable
                  data={rowsForTable}
                  onReserve={(f) =>
                      toast.error(`⚠️ Reservations are not yet implemented for: ${f}`)
                  }
                  onViewReviews={(field) => setOpenReviewsFor(field)}
                  onViewMatchNeeds={(field) => setOpenMatchNeedsFor(field)}
              />
          )}

          {hasSearched && !isFetching && !isError && rowsForTable.length === 0 && (
              <p className="text-sm text-gray-500 mt-4">
                No fields found matching your criteria.
              </p>
          )}
        </div>

        <ReviewsModal
            isOpen={!!openReviewsFor}
            onClose={() => setOpenReviewsFor(null)}
            fieldName={openReviewsFor?.name}
            fieldId={openReviewsFor?.id ?? 0}
        />

        <ViewFieldMatchesModal
            isOpen={!!openMatchNeedsFor}
            onClose={() => setOpenMatchNeedsFor(null)}
            field={openMatchNeedsFor}
        />
      </CommonLayout>
  );
};
