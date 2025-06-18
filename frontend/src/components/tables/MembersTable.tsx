import { ColumnDef } from "@tanstack/react-table";
import { Table } from "@/components/tables/Table";
import { RawUserDTO } from "@/services/UserServices.ts";
import { useImageById } from "@/services/ImageServices.ts";


type TeamMembersTableProps = {
  data: RawUserDTO[];
};

export function TeamMembersTable({ data }: TeamMembersTableProps) {

  const columns: ColumnDef<RawUserDTO>[] = [
    {
        accessorKey: "avatarUrl",
        header: "Logo",
        cell: ({ row }) => {
        const imageEndpoint = row.original.avatarUrl;
        const imageUrl = useImageById(imageEndpoint);
        return (
            <div
            className="w-[120px] h-[100px] overflow-hidden rounded bg-black/10 flex items-center justify-center"
            >
            {imageUrl ? (
                <img
                src={imageUrl}
                alt={row.original.firstName+' '+row.original.lastName}
                className="w-full h-full object-cover block"
                />
            ) : (
                <span className="text-xs">No logo</span>
            )}
            </div>
        );
        }
    },
    {
      id: "fullName",
      header: "Full Name",
      cell: ({ row }) => row.original.firstName+' '+row.original.lastName
    },
    {
      accessorKey: "username",
      header: "Username",
      cell: ({ row }) => row.original.username
    },
    
  ];

  return (
    <>
      <Table columns={columns} data={data} />
    </>
  );
}
