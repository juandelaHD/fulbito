import { ColumnDef } from "@tanstack/react-table";
import { Table } from "@/components/tables/Table";
import { ScheduleSlot } from "@/services/FieldServices";

type FieldDetailsSchedulesTableProps = {
  schedules: ScheduleSlot[];
};

export function FieldDetailsSchedulesTable({ schedules }: FieldDetailsSchedulesTableProps) {
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
  ];

  return <Table columns={columns} data={schedules} />;
}
