
import { useGetMyProfile } from "@/services/UserServices.ts";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { MyMatchesHistoryScreen } from "@/screens/user/MyMatchesHistoryScreen.tsx";
import { MyJoinedMatchesScreen } from "@/screens/user/MyJoinedMatchesScreen.tsx";
import { MyUpcomingMatchesScreen } from "@/screens/user/MyUpcomingMatchesScreen.tsx";
import { useState } from "react";

export default function MyMatchesScreen() {
  const { data: user } = useGetMyProfile();
  const [refreshKey, setRefreshKey] = useState(0);

  const handleRefresh = () => setRefreshKey((k) => k + 1);

  if (!user) return <p style={{ padding: "2rem", color: "white" }}>Error while loading profile</p>;

  return (
    <CommonLayout>
      <div style={{ padding: "2rem", color: "white" }}>
        {/* Presentación */}
        <div style={{ marginBottom: "2rem", textAlign: "center" }}>
          <h1 style={{ fontSize: "2rem", fontWeight: "bold" }}>Your Matches</h1>
          <p style={{ color: "#d1d5db", marginTop: "0.5rem" }}>
            You can view all your matches here: upcoming, those you've joined, and your complete match history.
          </p>
        </div>

        <hr style={{ margin: "2rem 0", borderColor: "#444" }} />
        <section style={{ marginTop: "3rem" }}>
          {user.role === "USER" && <MyUpcomingMatchesScreen refreshKey={refreshKey} />}
        </section>
        <hr style={{ margin: "2rem 0", borderColor: "#444" }} />
        <section style={{ marginTop: "3rem" }}>
          {user.role === "USER" && <MyJoinedMatchesScreen onRefresh={handleRefresh} />}
        </section>
        <hr style={{ margin: "2rem 0", borderColor: "#444" }} />
        <section style={{ marginTop: "3rem" }}>
          {user.role === "USER" && <MyMatchesHistoryScreen />}
        </section>
      </div>
    </CommonLayout>
  );
}
