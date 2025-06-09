// src/screens/OpenMatchesScreen.tsx
import { useEffect, useState } from "react";
import { useGetOpenMatches, useJoinMatch } from "@/services/MatchesServices";
import { OpenMatchesTable, Match as TableMatch } from "@/components/tables/OpenMatchesTable";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout";

export default function OpenMatchesScreen() {
  const { data: rawMatches, isLoading: isFetchingMatches, isError, refetch } = useGetOpenMatches();
  const { mutateAsync: joinMatch } = useJoinMatch();
  const [matches, setMatches] = useState<TableMatch[]>([]);

  useEffect(() => {
    if (Array.isArray(rawMatches)) {
      const mapped: TableMatch[] = rawMatches.map((m) => {
        const start = new Date(m.startTime);
        const end = new Date(m.endTime);
        return {
          id: m.id,
          fieldName: m.field.name,
          date: m.date,
          startTime: start.toLocaleTimeString("es-AR", { hour: "2-digit", minute: "2-digit" }),
          endTime: end.toLocaleTimeString("es-AR",   { hour: "2-digit", minute: "2-digit" }),
          registered: m.players.length,
          minPlayers: m.minPlayers,
          maxPlayers: m.maxPlayers,
        };
      });
      setMatches(mapped);
    }
  }, [rawMatches]);

  const handleJoin = async (matchId: number) => {
    await joinMatch(matchId);
    refetch();
  };

  return (
    <CommonLayout>
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold">Open Matches</h1>

      {isFetchingMatches && <div>Loading open matches</div>}
      {isError && <div className="text-red-500">Error while loading open matches.</div>}

      {!isFetchingMatches && !isError && (
        <OpenMatchesTable data={matches} onJoin={handleJoin} joiningId={null} />
      )}
    </div>
    </CommonLayout>
  );
}
