import { useEffect, useState } from "react";
import { useGetMyJoinedMatches } from "@/services/UserServices";
import { MyJoinedMatch } from "@/components/tables/MyJoinedMatchesTable.tsx";
import { MyJoinedMatchesTable } from "@/components/tables/MyJoinedMatchesTable.tsx";
import { useGetMatchInviteLink, useLeaveMatch } from "@/services/MatchesServices.ts";
import { toast } from "react-hot-toast";


export const MyJoinedMatchesScreen = () => {
  const { data: RawBasicMatchDTO, isLoading, error, refetch } = useGetMyJoinedMatches();
  const { mutateAsync: getInviteLink } = useGetMatchInviteLink();
  const { mutateAsync: leaveMatch } = useLeaveMatch();
  const [matches, setMatches] = useState<MyJoinedMatch[]>([]);

  useEffect(() => {
    if (Array.isArray(RawBasicMatchDTO)) {
      const mapped: MyJoinedMatch[] = RawBasicMatchDTO.map((m) => {
        const start = new Date(m.startTime);
        const end = new Date(m.endTime);
        return {
          matchId: m.matchId,
          fieldName: m.fieldName,
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
  }, [RawBasicMatchDTO]
  );

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

  const handleLeaveMatch = async (matchId: number) => {
    try {
      await leaveMatch(matchId);
      toast.success("You have left the match successfully.");
      refetch();
    } catch (err) {
      console.error(err);
      toast.error("Error while leaving the match.");
    }
  }

  return (
    <section>
      <div className="flex flex-col items-center gap-2 mb-4">
        <h2 className="text-2xl font-semibold text-center">Joined Matches</h2>
      </div>
      {isLoading && <p>Loading matches...</p>}
      {error && <p className="text-red-500">Error while loading matches</p>}
      {Array.isArray(matches) && matches.length > 0 &&
        <MyJoinedMatchesTable
          data={matches}
          onGetInviteLink={handleGetInviteLink}
          onLeave={handleLeaveMatch}
          leaveId={null}
        />}
      {Array.isArray(matches) && matches.length === 0 && (
        <p className="text-gray-400 text-center">You don't have any joined matches yet.</p>
      )}
    </section>
  );
};
