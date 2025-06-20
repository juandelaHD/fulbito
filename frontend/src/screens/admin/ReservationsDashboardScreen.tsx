import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { useState } from "react";
import { useGetMatchesByField } from "@/services/FieldServices";
import { AdminDashboardTable } from "@/components/tables/AdminDashboardTable";
import { RawMatchDTO } from "@/services/UserServices.ts";
import { ColumnDef } from "@tanstack/react-table";
import "react-datepicker/dist/react-datepicker.css";
import DatePicker from "react-datepicker";
import type { Page } from "@/services/FieldServices";
import { useGetFieldStats } from "@/services/FieldServices";


export const ReservationsDashboardScreen = () => {
  // Extrae /fields/:id/matches/:name de la URL
  const pathMatch = window.location.pathname.match(/^\/fields\/(\d+)\/reservations\/(.+)$/);
  const fieldId = pathMatch ? parseInt(pathMatch[1], 10) : undefined;
  const fieldName = pathMatch ? decodeURIComponent(pathMatch[2]) : "";

  // Estados de paginación
  const [pendingPage, setPendingPage] = useState(0);
  const [filteredPage, setFilteredPage] = useState(0);
  const [size, setSize] = useState(10);

  // Estadisticas de la cancha
  const { data: stats, isLoading: isLoadingStats } = useGetFieldStats(fieldId);


  // Filtros
  const [status, setStatus] = useState<string | undefined>("SCHEDULED");
  const [date, setDate] = useState<string | undefined>();
  const [searchParams, setSearchParams] = useState<{ status?: string; date?: string }>({ status: "SCHEDULED" });
  // Para la tabla de pendientes
  // Tabla de pendientes (paginada)
  const { data: pendingMatches, isFetching: isFetchingPending, refetch: refetchPending } = useGetMatchesByField(
    fieldId,
    "PENDING",
    undefined,
    pendingPage,
    size
  ) as { data: Page<RawMatchDTO> | undefined, isFetching: boolean, refetch: () => void  };

  // Para la tabla filtrada
  const { data: filteredMatches, isFetching: isFetchingFiltered, refetch: refetchFiltered } = useGetMatchesByField(
    fieldId,
    searchParams.status,
    searchParams.date,
    filteredPage,
    size
  ) as { data: Page<RawMatchDTO> | undefined, isFetching: boolean, refetch: () => void  };

  // Columnas base
  const columns: ColumnDef<RawMatchDTO, any>[] = [
    { accessorKey: "date", header: "Date" },
    { accessorKey: "startTime", header: "Start time" },
    { accessorKey: "endTime", header: "End time" },
    { accessorKey: "matchType", header: "Type" },
    {
      accessorKey: "organizer",
      header: "Organizer",
      cell: ({ row }) => (
        <span>
        {row.original.organizer?.firstName ?? "N/A"} {row.original.organizer?.lastName ?? ""}
      </span>
      ),
    },
    { accessorKey: "status", header: "Status" },
  ];

  // TODO: Change the result of a finished match
  const handleSetResult = (match: RawMatchDTO) => {
    alert(`Set result for match ${match.id}`);
  };

  return (
    <CommonLayout>
      <div className="p-6 text-white">
        <div className="mb-8 text-center">
          <h1 className="text-2xl font-bold">Reservations Dashboard</h1>
          <p className="text-gray-300 mt-2">
            Here you can manage the reservations for the field: <strong>{fieldName}</strong>
          </p>
        </div>

        {/* Pending matches table */}
        <section className="mb-10">
          <h2 className="text-xl font-semibold mb-2 text-center">Pending Reservations</h2>
          <AdminDashboardTable matches={pendingMatches?.content ?? []} columns={columns}
                               onSetResult={handleSetResult} refetch={refetchPending} />
          <div className="flex justify-center items-center gap-4 mt-4">
            <button
              className="px-2 py-1 bg-gray-600 text-white rounded"
              onClick={() => setPendingPage(p => Math.max(0, p - 1))}
              disabled={pendingPage === 0 || isFetchingPending}
            >
              Previous
            </button>
            <span>
            Page {pendingPage + 1} of {pendingMatches?.totalPages ?? 1}
          </span>
            <button
              className="px-2 py-1 bg-gray-600 text-white rounded"
              onClick={() => setPendingPage(p => (pendingMatches && p + 1 < pendingMatches.totalPages ? p + 1 : p))}
              disabled={pendingMatches ? pendingPage + 1 >= pendingMatches.totalPages : true || isFetchingPending}
            >
              Next
            </button>
            <select value={size} onChange={e => {
              setSize(Number(e.target.value));
              setPendingPage(0);
            }}>
              {[5, 10, 20, 50].map(opt => (
                <option key={opt} value={opt}>{opt} per page</option>
              ))}
            </select>
          </div>
        </section>

        <hr className="my-8 border-gray-700" />

        {/* Filtered matches table */}
        <section className="mb-10">
          <h2 className="text-xl font-semibold mb-2 text-center">Search by date and status</h2>
          <div className="flex flex-col items-center gap-4 mb-4">
            <div className="flex gap-4">
              <div>
                <label className="block mb-1 text-center">Date</label>
                <DatePicker
                  selected={date ? new Date(date) : null}
                  onChange={dateObj => setDate(dateObj ? dateObj.toISOString().split("T")[0] : undefined)}
                  dateFormat="yyyy-MM-dd"
                  className="border rounded p-2 text-black"
                  placeholderText="Select a date"
                  isClearable
                />
              </div>
              <div>
                <label className="block mb-1 text-center">Status</label>
                <select
                  value={status ?? ""}
                  onChange={e => setStatus(e.target.value || undefined)}
                  className="border rounded p-2 text-black"
                >
                  <option value="">All</option>
                  <option value="ACCEPTED">Accepted</option>
                  <option value="SCHEDULED">Scheduled</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="FINISHED">Finished</option>
                </select>
              </div>
            </div>
            <button
              className="px-4 py-2 bg-blue-600 text-white rounded"
              onClick={() => {
                setSearchParams({ status, date });
                setFilteredPage(0);
              }}
              disabled={isFetchingFiltered}
            >
              Search
            </button>
          </div>
          <AdminDashboardTable matches={filteredMatches?.content ?? []} columns={columns}
                               onSetResult={handleSetResult} refetch={refetchFiltered} />
          <div className="flex justify-center items-center gap-4 mt-4">
            <button
              className="px-2 py-1 bg-gray-600 text-white rounded"
              onClick={() => setFilteredPage(p => Math.max(0, p - 1))}
              disabled={filteredPage === 0 || isFetchingFiltered}
            >
              Previous
            </button>
            <span>
            Page {filteredPage + 1} of {filteredMatches?.totalPages ?? 1}
          </span>
            <button
              className="px-2 py-1 bg-gray-600 text-white rounded"
              onClick={() => setFilteredPage(p => (filteredMatches && p + 1 < filteredMatches.totalPages ? p + 1 : p))}
              disabled={filteredMatches ? filteredPage + 1 >= filteredMatches.totalPages : true || isFetchingFiltered}
            >
              Next
            </button>
            <select value={size} onChange={e => {
              setSize(Number(e.target.value));
              setFilteredPage(0);
            }}>
              {[5, 10, 20, 50].map(opt => (
                <option key={opt} value={opt}>{opt} per page</option>
              ))}
            </select>
          </div>
        </section>
      </div>

      <hr className="my-8 border-gray-700" />

      {/* Field Stats */}
      <section className="mb-10">
        <h2 className="text-xl font-semibold mb-2 text-center">Field Stats</h2>
      {isLoadingStats ? (
        <p className="text-gray-400">Cargando estadísticas…</p>
      ) : stats ? (
        <div className="inline-block text-left space-y-2 text-green-200">
          <p>🔸 Ocupación semanal: <strong>{stats.weeklyPercentage}%</strong></p>
          <p>🔸 Ocupación mensual: <strong>{stats.monthlyPercentage}%</strong></p>
          <p>
            🔸 Horas reservadas vs disponibles:{" "}
            <strong>{stats.reservedHours}h / {stats.availableHours}h</strong>
          </p>
        </div>
      ) : (
        <p className="text-red-400">No hay estadísticas disponibles.</p>
      )}
      </section>


    </CommonLayout>
  );
}