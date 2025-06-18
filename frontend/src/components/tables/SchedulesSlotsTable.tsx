import { ColumnDef } from "@tanstack/react-table";
import { Table } from "@/components/tables/Table";
import { ScheduleSlot } from "@/services/FieldServices";

type SchedulesSlotsTableProps = {
  schedules: ScheduleSlot[];
  onAction?: (slot: ScheduleSlot, newStatus: "AVAILABLE" | "BLOCK") => void;
  onDelete?: (slot: ScheduleSlot) => void;
};

export function SchedulesSlotsTable({ schedules, onAction, onDelete}: SchedulesSlotsTableProps) {
  const columns: ColumnDef<ScheduleSlot>[] = [
    {
      accessorKey: "date",
      header: "Date",
    },
    {
      accessorKey: "startTime",
      header: "Start Time",
    },
    {
      accessorKey: "endTime",
      header: "End Time",
    },
    {
      accessorKey: "status",
      header: "Status",
    },
    {
      id: "actions",
      header: "Actions",
      cell: ({ row }) => {
        const slot = row.original;
        return (
          <div className="flex gap-2 justify-center">
            {/* Botones de estado */}
            {slot.status === "AVAILABLE" && (
              <button
                style={{ backgroundColor: "#ca8a04", color: "#fff" }} // Amarillo
                className="px-2 py-1 rounded hover:brightness-110"
                onClick={() => onAction?.(slot, "BLOCKED")}
              >
                BLOCK
              </button>
            )}
            {slot.status === "BLOCKED" && (
              <button
                style={{ backgroundColor: "#16a34a", color: "#fff" }} // Verde
                className="px-2 py-1 rounded hover:brightness-110"
                onClick={() => onAction?.(slot, "AVAILABLE")}
              >
                AVAILABLE
              </button>
            )}
            {slot.status !== "RESERVED" && (
              <button
                style={{ backgroundColor: "#dc2626", color: "#fff" }} // Rojo
                className="px-2 py-1 rounded hover:brightness-110"
                onClick={() => onDelete?.(slot)}
              >
                DELETE
              </button>
            )}
          </div>
        );
      },
    },
  ];

  return <Table columns={columns} data={schedules} />;
}