import { useEffect, useState } from "react";
import { useGetMyJoinedMatches } from "@/services/UserServices";
import { MyMatch, MyMatchesTable } from "@/components/tables/MyMatchesTable.tsx";

export const MyJoinedMatchesScreen = () => {
  const { data: RawBasicMatchDTO, isLoading, error } = useGetMyJoinedMatches();
  const [matches, setMatches] = useState<MyMatch[]>([]);

  useEffect(() => {
    if (Array.isArray(RawBasicMatchDTO)) {
      const mapped: MyMatch[] = RawBasicMatchDTO.map((m) => {
        const start = new Date(m.startTime);
        const end = new Date(m.endTime);
        return {
          matchId: m.matchId,
          fieldName: m.fieldName,
          matchStatus: m.matchStatus,
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
      {Array.isArray(matches) && matches.length > 0 && <MyMatchesTable data={matches} />}
      {Array.isArray(matches) && matches.length === 0 && (
        <p className="text-gray-400 text-center">You don't have any teams yet.</p>
      )}
    </section>
  );
};
