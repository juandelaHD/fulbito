import { ColumnDef } from "@tanstack/react-table";
import { Table } from "@/components/tables/Table";
import { useDeleteField, useGetOwnedFields } from "@/services/FieldServices";
import { useImageById } from "@/services/ImageServices.ts";
import { DeleteFieldConfirmationModal } from "@/components/modals/DeleteFieldConfirmationModal.tsx";
import { useState } from "react";
import { useMemo } from "react";
import {EditFieldModal} from "@/components/modals/EditFieldModal.tsx";
import { useLocation } from "wouter";

export type Field = {
  id: number
  name: string
  grassType: string
  lighting: string
  zone: string
  address: string
  enabled: boolean
  imageUrl?: string
  allImagesUrls?: string[]
}

export function ManageFieldsTable() {
  const { data, isLoading, isError, refetch } = useGetOwnedFields();
  const { mutateAsync: deleteField } = useDeleteField();
  const [fieldToDelete, setFieldToDelete] = useState<Field | null>(null);
  const [editingField, setEditingField] = useState<Field | null>(null);
  const [, navigate] = useLocation();

  function mapFieldDTOtoField(dto: any): Field {
    return {
      id: dto.id,
      name: dto.name,
      grassType: dto.grassType,
      lighting: dto.illuminated ? "Yes" : "No",
      zone: dto.location.zone,
      address: dto.location.address,
      enabled: dto.enabled,
      imageUrl: Array.isArray(dto.imagesUrls) && dto.imagesUrls.length > 0 ? dto.imagesUrls[0] : undefined,
      allImagesUrls: dto.imagesUrls,
    };
  }

  const handleDeleteConfirmed = async (fieldId: number) => {
    await deleteField(fieldId);
    refetch();
  };

  const handleSchedulesClick = (field: Field) => {
    navigate(`/schedules/${field.id}`);
  }

  const columns: ColumnDef<Field>[] = useMemo(() => [
    { accessorKey: "name", header: "Name" },
    { accessorKey: "grassType", header: "Grass Type" },
    { accessorKey: "lighting", header: "Lighting" },
    { accessorKey: "zone", header: "Zone" },
    { accessorKey: "address", header: "Address" },
    { accessorKey: "enabled", header: "Enabled", cell: ({ row }) => (row.original.enabled ? "Yes" : "No") },
    {
      id: "actions",
      header: "Actions",
      cell: ({ row }) => (
          <div className="flex space-x-2">
            <button
                onClick={() => setEditingField(row.original)}
                className="text-sm bg-blue-600 text-white px-2 py-1 rounded hover:bg-blue-700"
            >
              Edit
            </button>
            <button
                onClick={() => setFieldToDelete(row.original)}
                className="text-sm bg-red-600 text-white px-2 py-1 rounded hover:bg-red-700"
            >
              Delete
            </button>
          </div>
      ),
    },
    {
      id: "image",
      header: "Image",
      cell: ({row}) => {
        const imageEndpoint = row.original.imageUrl;
        const imageUrl = useImageById(imageEndpoint);

        return (
            <div
                className="w-[120px] h-[100px] overflow-hidden rounded bg-black/10 flex items-center justify-center"
            >
              {imageUrl ? (
                  <img
                      src={imageUrl}
                      alt={row.original.name}
                      className="w-full h-full object-cover block"
                  />
              ) : (
                  <span className="text-xs">🖼️</span>
              )}
            </div>
        );
      },
    }
  ], [setFieldToDelete]);

  return (
      <>
        {isLoading && <div>Loading fields...</div>}
        {isError && <div className="text-red-500">Error loading fields</div>}
        {!isLoading && data && <Table columns={columns} data={data.content.map(mapFieldDTOtoField)} />}
        {fieldToDelete && (
            <DeleteFieldConfirmationModal
                isOpen={!!fieldToDelete}
                fieldName={fieldToDelete.name}
                onCancel={() => setFieldToDelete(null)}
                onConfirm={async () => {
                  await handleDeleteConfirmed(fieldToDelete.id);
                  setFieldToDelete(null);
                }}
            />
        )}
        {editingField && (
            <EditFieldModal
                field={editingField}
                onClose={() => setEditingField(null)}
                onSaved={() => {
                  setEditingField(null);
                  refetch();
                }}
            />
        )}
    </>
  );
}
