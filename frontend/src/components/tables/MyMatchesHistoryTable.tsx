import { ColumnDef } from "@tanstack/react-table"
import { Table } from "@/components/tables/Table"
import { RawMatchDTO, RawTeamDTO } from "@/services/UserServices.ts";
import { useState } from "react";
import { TeamsModal } from "@/components/modals/TeamsModal.tsx";

type MyMatchesTableProps = {
  data: RawMatchDTO[]
}

export function MyMatchesHistoryTable({ data }: MyMatchesTableProps) {
  const [selectedTeam, setSelectedTeam] = useState<RawTeamDTO | null>(null);
  const isOpen = selectedTeam !== null;

  const columns: ColumnDef<RawMatchDTO>[] = [
    {
      id: "fieldName",
      header: "Field",
      cell: ({ row }) => row.original.field.name,
    },
    {
      id: "fieldLocation",
      header: "Field Location",
      cell: ({ row }) => {
        const location = row.original.field.location
        return location ? `${location.zone} - ${location.address}` : "N/A"
      },
    },
    {
      id: "date",
      header: "Date",
      cell: ({ row }) => row.original.date,
    },
    {
      id: "hour",
      header: "Hour",
      cell: ({ row }) => (
        <>{row.original.startTime} – {row.original.endTime}</>
      ),
    },
    {
      id: "matchType",
      header: "Match Type",
      cell: ({ row }) => row.original.matchType,
    },
    {
      id: "result",
      header: "Result",
      cell: ({ row }) => row.original.result || "Pending",
    },
    {
      id: "homeTeam",
      header: "Home Team",
      cell: ({ row }) =>
        row.original.homeTeam ? (
          <button
            className="text-blue-600 underline"
            onClick={() => setSelectedTeam(row.original.homeTeam ?? null)}
          >
            View Team
          </button>
        ) : (
          <span className="text-gray-400">No team</span>
        ),
    },
    {
      id: "awayTeam",
      header: "Away Team",
      cell: ({ row }) =>
        row.original.awayTeam ? (
          <button
            className="text-blue-600 underline"
            onClick={() => setSelectedTeam(row.original.awayTeam ?? null)}
          >
            View Team
          </button>
        ) : (
          <span className="text-gray-400">No team</span>
        ),
    },
  ]

  return(
    <>
      <Table columns={columns} data={data} />
      {selectedTeam && (
        <TeamsModal team={selectedTeam} onClose={() => setSelectedTeam(null)} isOpen={isOpen} />
      )}
    </>
  );
}