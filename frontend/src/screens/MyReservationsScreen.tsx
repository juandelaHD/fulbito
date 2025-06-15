
import { useGetMyProfile } from "@/services/UserServices";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { ReservationsScreen } from "@/screens/ReservationsScreen.tsx";

export default function MyReservationsScreen() {
  const { data: user } = useGetMyProfile();
  if (!user) return <p style={{ padding: "2rem", color: "white" }}>Error while loading profile</p>;

  return (
    <CommonLayout>
      <div style={{ padding: "2rem", color: "white" }}>
        {/* Presentación */}
        <div style={{ marginBottom: "2rem", textAlign: "center" }}>
          <h1 style={{ fontSize: "2rem", fontWeight: "bold" }}>Your Reservations</h1>
          <p style={{ color: "#d1d5db", marginTop: "0.5rem" }}>
            You can view and manage your reservations here.
          </p>
        </div>

        <section style={{ marginTop: "3rem" }}>
          {user.role === "USER" && <ReservationsScreen />}
        </section>
        <hr style={{ margin: "2rem 0", borderColor: "#444" }} />

      </div>
    </CommonLayout>
  );
}
