import { ColumnDef } from "@tanstack/react-table";
import { Table } from "@/components/tables/Table";
import { useGetOwnedFields } from "@/services/FieldServices";
import {useImageById} from "@/services/ImageServices.ts";
import {toast} from "react-hot-toast";

export type Field = {
  id: number
  name: string
  grassType: string
  lighting: string
  zone: string
  address: string
  imageUrl?: string
  allImagesUrls?: string[]
}

export function ManageFieldsTable() {
  const { data, isLoading, isError } = useGetOwnedFields();

  function mapFieldDTOtoField(dto: any): Field {
    return {
      id: dto.id,
      name: dto.name,
      grassType: dto.grassType,
      lighting: dto.illuminated ? "Yes" : "No",
      zone: dto.location.zone,
      address: dto.location.address,
      imageUrl: Array.isArray(dto.imagesUrls) && dto.imagesUrls.length > 0 ? dto.imagesUrls[0] : undefined,
      allImagesUrls: dto.imagesUrls,
    };
  }

  const handleDelete = async (fieldId: number) => {
    toast.error(`⚠️ Field deletion of field ${fieldId} is not yet implemented`)
  };

  const columns: ColumnDef<Field>[] = [
    { accessorKey: "name", header: "Name" },
    { accessorKey: "grassType", header: "Grass Type" },
    { accessorKey: "lighting", header: "Lighting" },
    { accessorKey: "zone", header: "Zone" },
    { accessorKey: "address", header: "Address" },
    {
      id: "actions",
      header: "Actions",
      cell: ({ row }) => (
        <div className="flex space-x-2">
          <button
            onClick={() => {
              toast.error(`⚠️ Editing field ${row.original.id} is not yet implemented`);
            }}
            className="text-sm bg-blue-600 text-white px-2 py-1 rounded hover:bg-blue-700"
          >
            Edit
          </button>
          <button
            onClick={() => handleDelete(row.original.id)}
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
      cell: ({ row }) => {
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
  ];

  return (
      <>
        {isLoading && <div>Loading fields...</div>}
        {isError && <div className="text-red-500">Error loading fields</div>}
      {!isLoading && data && <Table columns={columns} data={data.content.map(mapFieldDTOtoField)} />}
    </>
  );
}
