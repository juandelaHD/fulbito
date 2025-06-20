import { useState } from "react";
import { ColumnDef } from "@tanstack/react-table";
import { Table } from "@/components/tables/Table";
import { useImageById } from "@/services/ImageServices.ts";
import { RawTeamDTO } from "@/services/UserServices.ts";
import { TeamsModal } from "@/components/modals/TeamsModal.tsx";
import { useLocation } from "wouter";


type TeamsTableProps = {
  data: RawTeamDTO[];
};

export function TeamsTable({ data }: TeamsTableProps) {
  const [selectedTeam, setSelectedTeam] = useState<RawTeamDTO | null>(null);
  const isOpen = selectedTeam !== null;
  const [, navigate] = useLocation();

  const columns: ColumnDef<RawTeamDTO>[] = [
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
    {
      id: "name",
      header: "Name",
      cell: ({ row }) => row.original.name,
    },
    {
      id: "actions",
      header: "Actions",
      cell: ({ row }) => (
        <button
          className="text-blue-600 underline"
          onClick={() => setSelectedTeam(row.original)}
        >
          View Team
        </button>
      ),
    },{
      accessorKey: "imageUrl",
      header: "Edit",
      cell: ({ row }) => {
        return <button
          className="text-blue-600 underline"
          onClick={() => navigate('/teams/edit/'+row.original.id)}
        >
          Editar Team
        </button>
      }
    }
  ];

  return (
    <>
      <Table columns={columns} data={data} />
      {selectedTeam && (
        <TeamsModal
          team={selectedTeam}
          onClose={() => setSelectedTeam(null)}
          isOpen={isOpen}
        />
      )}
    </>
  );
}
