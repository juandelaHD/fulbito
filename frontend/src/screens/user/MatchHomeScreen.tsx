import { useLocation } from "wouter";
import { Match as TableMatch, OpenMatchesTable } from "@/components/tables/OpenMatchesTable.tsx";
import { useEffect, useState } from "react";
import { useGetMatchInviteLink, useGetOpenMatches, useJoinMatch } from "@/services/MatchesServices.ts";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { toast } from "react-hot-toast";

export const MatchHomeScreen = () => {
  const { data: rawMatches, isLoading: isFetchingMatches, isError, refetch } = useGetOpenMatches();
  const { mutateAsync: joinMatch } = useJoinMatch();
  const { mutateAsync: getInviteLink } = useGetMatchInviteLink();
  const [matches, setMatches] = useState<TableMatch[]>([]);
  const [, navigate] = useLocation();

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
    try {
      await joinMatch(matchId);
      toast.success("You have joined the match successfully.");
      refetch();
    } catch (err) {
      console.error(err);
      toast.error("Error while joining the match.");
    }
  };

  const handleGetInviteLink = async (matchId: number) => {
    try {
      const link = await getInviteLink(matchId);
      await navigator.clipboard.writeText(link);
      toast.success("¡Link copied to clipboard!");
    } catch (err) {
      console.log(err);
      toast.error("Error while getting invite link.");
    }
  };

  return (
    <CommonLayout>
      <section>
        <h1 className="text-center text-2xl font-semibold mb-4">Want to create a match?</h1>
        <div className="flex justify-center gap-8 mt-8">
          <div className="flex flex-col items-center">
            <button
              className="w-40 h-16 text-lg font-bold text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition mb-2"
              onClick={() => navigate("/matches/create/open")}
            >
              OPEN
            </button>
            <p className="text-center text-sm text-gray-600 w-40">
              Anyone can join this match.
            </p>
          </div>
          <div className="flex flex-col items-center">
            <button
              className="w-40 h-16 text-lg font-bold text-white bg-green-600 rounded-lg hover:bg-green-700 transition mb-2"
              onClick={() => navigate("/matches/create/closed")}
            >
              CLOSED
            </button>
            <p className="text-center text-sm text-gray-600 w-40">
              Only invited players can join this match.
            </p>
          </div>
        </div>
      </section>
      <div className="p-6 space-y-6">
        <h1 className="text-2xl font-bold">Open Matches</h1>
        {isFetchingMatches && <div>Loading open matches</div>}
        {isError && <div className="text-red-500">Error while loading open matches.</div>}
        {!isFetchingMatches && !isError && (
          <OpenMatchesTable
            data={matches}
            onJoin={handleJoin}
            onGetInviteLink={handleGetInviteLink}
            joiningId={null}
          />
        )}
      </div>
    </CommonLayout>
  );
};