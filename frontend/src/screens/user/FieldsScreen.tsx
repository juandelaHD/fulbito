import { useState } from "react";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { FieldsTable } from "@/components/tables/FieldsTable.tsx";
import { FieldsFilters, FieldsFiltersContainer } from "@/components/filters/FieldsFilters.tsx";
import { useGetFields } from "@/services/FieldServices.ts";
import { GetFieldsRequest } from "@/models/GetFields.ts";
import { ReviewsModal } from "@/components/modals/ReviewsModal.tsx";
import { ViewFieldMatchesModal } from "@/components/modals/ViewFieldMatches.tsx";
import { FieldDetailsModal } from "@/components/modals/FieldDetailsModal.tsx";
import type { Field as FieldForTable } from "@/components/tables/FieldsTable.tsx";
import { useLocation } from "wouter";

export const FieldsScreen = () => {
  const [hasSearched, setHasSearched] = useState(false);
  const [filters, setFilters] = useState<FieldsFilters>({
    name: "",
    zone: "",
    address: "",
    grassType: "",
    isIlluminated: false,
    hasOpenMatch: false,
    isEnabled: true,
  });

  const [openMatchNeedsFor, setOpenMatchNeedsFor] = useState<FieldForTable | null>(null);
  const [openReviewsFor, setOpenReviewsFor] = useState<FieldForTable | null>(null);
  const [selectedFieldForDetails, setSelectedFieldForDetails] = useState<FieldForTable | null>(null);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [, navigate] = useLocation();

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
    setHasSearched(true);
    await refetch();
  };

  const handleOpenDetails = (field: FieldForTable) => {
    setSelectedFieldForDetails(field);
    setShowDetailsModal(true);
  };

  const handleCloseDetails = () => {
    setSelectedFieldForDetails(null);
    setShowDetailsModal(false);
  };

  const rowsForTable: FieldForTable[] =
    fetchedFields?.content?.map((item) => ({
      id: Number(item.id),
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
      imageUrl:
        Array.isArray(item.imagesUrls) && item.imagesUrls.length > 0
          ? item.imagesUrls[0]
          : undefined,
      matchesWithMissingPlayers: item.matchesWithMissingPlayers ?? null,
    })) || [];

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
          <p className="text-sm text-red-500 mt-4">Error loading fields. Please try again.</p>
        )}

        {hasSearched && isFetching && (
          <p className="text-sm text-gray-500 mt-4">Loading...</p>
        )}

        {hasSearched && !isFetching && !isError && rowsForTable.length > 0 && (
          <FieldsTable
            data={rowsForTable}
            onReserve={() => navigate("/matches/create/open")}
            onViewReviews={(field) => setOpenReviewsFor(field)}
            onViewMatchNeeds={(field) => setOpenMatchNeedsFor(field)}
            onViewDetails={handleOpenDetails}
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

      {showDetailsModal && selectedFieldForDetails && (
        <FieldDetailsModal
          isOpen={showDetailsModal}
          onClose={handleCloseDetails}
          field={selectedFieldForDetails}
        />
      )}
    </CommonLayout>
  );
};
