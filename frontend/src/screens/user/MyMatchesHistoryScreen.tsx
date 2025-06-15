import { useEffect, useState } from "react";
import { useGetMyMatchesPlayed } from "@/services/UserServices.ts";
import { MyMatch, MyMatchesHistoryTable } from "@/components/tables/MyMatchesHistoryTable.tsx";

export const MyMatchesHistoryScreen = () => {
  const { data: RawBasicMatchDTO, isLoading, error } = useGetMyMatchesPlayed();
  const [matches, setMatches] = useState<MyMatch[]>([]);

  useEffect(() => {
    if (Array.isArray(RawBasicMatchDTO)) {
      const mapped: MyMatch[] = RawBasicMatchDTO.map((m) => {
        const start = new Date(m.startTime);
        const end = new Date(m.endTime);
        return {
          matchId: m.matchId,
          fieldName: m.fieldName,
          fieldLocation: m.fieldLocation,
          matchStatus: m.status,
          date: m.date,
          startTime: start.toLocaleTimeString("es-AR", { hour: "2-digit", minute: "2-digit" }),
          endTime: end.toLocaleTimeString("es-AR",   { hour: "2-digit", minute: "2-digit" }),
          matchType: m.matchType,
          result: m.result || "Pending",
        };
      });
      setMatches(mapped);
    }
  }, [RawBasicMatchDTO]);

  return (
    <section>
      <div className="flex flex-col items-center gap-2 mb-4">
        <h2 className="text-2xl font-semibold text-center">History Matches</h2>
      </div>
      {isLoading && <p>Loading teams...</p>}
      {error && <p className="text-red-500">Error while loading teams</p>}
      {Array.isArray(matches) && matches.length > 0 && <MyMatchesHistoryTable data={matches} />}
      {Array.isArray(matches) && matches.length === 0 && (
        <p className="text-gray-400 text-center">You don't have any teams yet.</p>
      )}
    </section>
  );
};

