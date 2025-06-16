import { useEffect, useState } from "react";
import { useGetMyReservations, RawMatchDTO } from "@/services/UserServices.ts";
import { useGetMatchInviteLink, useCancelMatch } from "@/services/MatchesServices.ts";
import { MyReservationsTable } from "@/components/tables/ReservationsTable.tsx";
import { toast } from "react-hot-toast";
import { useLocation } from "wouter";

export const ReservationsScreen = () => {
  const { data: reservations, refetch } = useGetMyReservations();
  const { mutateAsync: getInviteLink } = useGetMatchInviteLink();
  const { mutateAsync: cancelMatch } = useCancelMatch();
  const [matches, setMatches] = useState<RawMatchDTO[]>([]);
  const [, navigate] = useLocation();

  useEffect(() => {
    try {
      if (Array.isArray(reservations)) {
        const mapped: RawMatchDTO[] = reservations.map((m) => ({
          ...m,
          startTime: new Date(m.startTime).toLocaleTimeString("es-AR", { hour: "2-digit", minute: "2-digit" }),
          endTime: new Date(m.endTime).toLocaleTimeString("es-AR", { hour: "2-digit", minute: "2-digit" }),
        }));
        setMatches(mapped);
      }
    } catch (error) {
      console.error("Error mapping reservations:", error);
      toast.error("Error while processing reservations.");
    }
  }, [reservations]);

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

  const handleCancelMatch = async (matchId: number) => {
    try {
      await cancelMatch(matchId);
      refetch();
    } catch (err) {
      console.error(err);
      toast.error("Error while cancelling the match.");
    }
  }

  const handleFormTeams = (matchId: number) => {
    navigate(`/matches/${matchId}/teams`);
  };


  return (
    <section>
      {Array.isArray(matches) && matches.length > 0 &&
        <MyReservationsTable
          data={matches}
          onGetInviteLink={handleGetInviteLink}
          onCancel={handleCancelMatch}
          cancelId={null}
          onFormTeams={handleFormTeams}
        />}
      {Array.isArray(matches) && matches.length === 0 && (
        <p className="text-gray-400 text-center">You don't have any reservations yet.</p>
      )}
    </section>
  );
};
