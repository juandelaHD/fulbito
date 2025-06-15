import { useEffect, useState } from "react";
import { MyUpcomingMatch } from "@/components/tables/MyUpcomingMatchesTable.tsx";
import { MyUpcomingMatchesTable } from "@/components/tables/MyUpcomingMatchesTable.tsx";
import { useGetMyUpcomingMatches } from "@/services/UserServices.ts";

export const MyUpcomingMatchesScreen = ({ refreshKey }: { refreshKey: number }) => {
  const { data: RawBasicMatchDTO, isLoading, error, refetch } = useGetMyUpcomingMatches();
  const [matches, setMatches] = useState<MyUpcomingMatch[]>([]);

  useEffect(() => {
    if (refreshKey !== 0) {
      refetch();
    }
    if (Array.isArray(RawBasicMatchDTO)) {
      const mapped: MyUpcomingMatch[] = RawBasicMatchDTO.map((m) => {
        const start = new Date(m.startTime);
        const end = new Date(m.endTime);
        return {
          matchId: m.matchId,
          fieldName: m.fieldName,
          fieldLocation: m.fieldLocation,
          status: m.status,
          date: m.date,
          startTime: start.toLocaleTimeString("es-AR", { hour: "2-digit", minute: "2-digit" }),
          endTime: end.toLocaleTimeString("es-AR",   { hour: "2-digit", minute: "2-digit" }),
          matchType: m.matchType,
          result: m.result || "Pending",
        };
      });
      setMatches(mapped);
    }
  }, [RawBasicMatchDTO, refreshKey]
  );

  return (
    <section>
      <div className="flex flex-col items-center gap-2 mb-4">
        <h2 className="text-2xl font-semibold text-center">Upcoming Matches</h2>
      </div>
      {isLoading && <p>Loading matches...</p>}
      {error && <p className="text-red-500">Error while loading matches</p>}
      {Array.isArray(matches) && matches.length > 0 &&
        <MyUpcomingMatchesTable
          data={matches}
        />}
      {Array.isArray(matches) && matches.length === 0 && (
        <p className="text-gray-400 text-center">You don't have any upcoming matches yet.</p>
      )}
    </section>
  );
};
