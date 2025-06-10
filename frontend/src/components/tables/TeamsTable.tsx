import { ColumnDef } from "@tanstack/react-table";
import { Table } from "@/components/tables/Table";
import { useImageById } from "@/services/ImageServices.ts";

export type Team = {
  id: number;
  name: string;
  imageUrl?: string;
  mainColor: string;
  secondaryColor: string;
  ranking?: number;
  captain?: {
    firstName: string;
    lastName: string;
    username: string;
    avatarUrl?: string;
  };
  members?: any[];
};

type TeamsTableProps = {
  data: Team[];
};

export function TeamsTable({ data }: TeamsTableProps) {
  const columns: ColumnDef<Team>[] = [
    {
      accessorKey: "imageUrl",
      header: "Logo",
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
              <span className="text-xs">No logo</span>
            )}
          </div>
        );
      }
    },
    { accessorKey: "name", header: "Nombre" },
    {
      id: "colors",
      header: "Colors",
      cell: ({ row }) => (
        <div className="flex items-center gap-2">
          <span
            className="color-circle"
            style={{ "--circle-color": row.original.mainColor || "#cccccc" } as React.CSSProperties}
            title={row.original.mainColor || "Sin color"}
          />
          <span
            className="color-circle"
            style={{ "--circle-color": row.original.secondaryColor || "#cccccc" } as React.CSSProperties}
            title={row.original.secondaryColor || "Sin color"}
          />
        </div>
      ),
    },
    {
      accessorKey: "ranking",
      header: "Ranking",
      cell: ({ row }) =>
        row.original.ranking ? (
          <span>{row.original.ranking}</span>
        ) : (
          <span className="text-gray-400">-</span>
        ),
    },
    {
      id: "captain",
      header: "Captain",
      cell: ({ row }) => {
        const imageAvatarEndpoint = row.original.captain?.avatarUrl;
        const imageAvatarUrl = useImageById(imageAvatarEndpoint);
        return (
          <div
            className="w-[120px] h-[100px] overflow-hidden rounded bg-black/10 flex items-center justify-center"
          >
            {imageAvatarUrl ? (
              <img
                src={imageAvatarUrl}
                alt={row.original.captain?.username}
                className="w-full h-full object-cover block"
              />
            ) : (
              <span className="text-xs">No logo</span>
            )}
          </div>
        );
      },
    },
    {
      accessorKey: "members",
      header: "Members",
      cell: ({ row }) => <span>{row.original.members?.length ?? 0}</span>,
    },
  ];

  return <Table columns={columns} data={data} />;
}