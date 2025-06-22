import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { useState, useRef, useEffect } from "react";
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
  const {
    data: stats,
    isLoading: isLoadingStats,
    refetch: refetchStats
  } = useGetFieldStats(fieldId);

  // Graficos de la cancha 
  const canvasRef = useRef<HTMLCanvasElement>(null);
  useEffect(() => {
    if (!stats || !canvasRef.current) return;
    const ctx = canvasRef.current.getContext("2d");
    if (!ctx) return;

    // Limpiar
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);

    // Datos a graficar: ocupación pasada vs futura en la semana
    const labels = ["Past wk", "Next wk"];
    const values = [stats.pastWeeklyPct, stats.futureWeeklyPct];

    // Configuración básica de barras horizontales
    const barHeight = 20;
    const gap = 20;
    const maxWidth = ctx.canvas.width - 100; // deja margen para etiquetas
    const maxValue = 100; // escalamos a 100%

    values.forEach((val, i) => {
      const y = 20 + i * (barHeight + gap);
      const width = (val / maxValue) * maxWidth;

      // barra
      ctx.fillStyle = "#06b6d4";
      ctx.fillRect(80, y, width, barHeight);

      // etiqueta de valor al inicio de barra
      ctx.fillStyle = "#fff";
      ctx.font = "14px sans-serif";
      ctx.fillText(`${val.toFixed(1)}%`, 10, y + barHeight - 4);

      // label al final de barra
      ctx.fillStyle = "#ccc";
      ctx.fillText(labels[i], 80 + width + 8, y + barHeight - 4);
    });
  }, [stats]);



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
  
  const refreshAll = () => {
    refetchPending();
    refetchFiltered();
    refetchStats();
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
                               refetch={refreshAll} />
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
                               refetch={refreshAll} />
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
        <h2 className="text-xl font-semibold mb-4 text-center">Field Stats</h2>

        {isLoadingStats ? (
          <p className="text-gray-400">Loading statistics...</p>
        ) : stats ? (
          <div className="flex flex-col md:flex-row md:space-x-8 items-start justify-center">
            {/* — BLOQUE DE TEXTO */}
            <div className="inline-block text-left space-y-4 text-green-200 mb-6 md:mb-0">
              {/* — SEMANA PASADA */}
              <h3 className="font-semibold underline">Last Week</h3>
              <p>🔸 Occupancy: <strong>{stats.pastWeeklyPct}%</strong></p>
              <p>🔸 Reserved vs Available: <strong>{stats.pastReservedHoursWeek}h / {stats.pastAvailableHoursWeek}h</strong></p>
              <p>🔸 Cancelled Matches: <strong>{stats.pastCancelledWeek}</strong></p>

              {/* — MES PASADO */}
              <h3 className="font-semibold underline mt-4">Last Month</h3>
              <p>🔸 Occupancy: <strong>{stats.pastMonthlyPct}%</strong></p>
              <p>🔸 Reserved vs Available: <strong>{stats.pastReservedHoursMonth}h / {stats.pastAvailableHoursMonth}h</strong></p>
              <p>🔸 Cancelled Matches: <strong>{stats.pastCancelledMonth}</strong></p>

              {/* — PRÓXIMA SEMANA */}
              <h3 className="font-semibold underline mt-4">Next Week</h3>
              <p>🔸 Occupancy: <strong>{stats.futureWeeklyPct}%</strong></p>
              <p>🔸 Reserved vs Available: <strong>{stats.futureReservedHoursWeek}h / {stats.futureAvailableHoursWeek}h</strong></p>
              <p>🔸 Cancelled Matches: <strong>{stats.futureCancelledWeek}</strong></p>

              {/* — PRÓXIMO MES */}
              <h3 className="font-semibold underline mt-4">Next Month</h3>
              <p>🔸 Occupancy: <strong>{stats.futureMonthlyPct}%</strong></p>
              <p>🔸 Reserved vs Available: <strong>{stats.futureReservedHoursMonth}h / {stats.futureAvailableHoursMonth}h</strong></p>
              <p>🔸 Cancelled Matches: <strong>{stats.futureCancelledMonth}</strong></p>
            </div>

            {/* — BLOQUE DEL CANVAS */}
            <div className="w-full md:w-1/2 border border-gray-600 bg-black p-2">
              <canvas
                ref={canvasRef}
                width={400}
                height={120}
                className="mx-auto block"
              />
              <p className="text-center text-sm text-gray-400 mt-2">
                Weekly occupancy: past vs next
              </p>
            </div>
          </div>
        ) : (
          <p className="text-red-400">No statistics available for this field.</p>
        )}
      </section>

    </CommonLayout>
  );
}