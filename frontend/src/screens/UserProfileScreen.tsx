
import { useGetMyProfile, useGetMyTeams } from "@/services/UserServices";
import { useImageById } from "@/services/ImageServices.ts";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";

export default function UserProfileScreen() {
  const { data: user, isLoading: loadingUser } = useGetMyProfile();
  const { data: teams, isLoading: loadingTeams } = useGetMyTeams();
  const userAvatarURL= useImageById(user?.avatarUrl);

  if (loadingUser || loadingTeams) return <p style={{ padding: "2rem", color: "white" }}>Loading...</p>;
  if (!user) return <p style={{ padding: "2rem", color: "white" }}>Error while loading profile</p>;

  return (
    <CommonLayout>
    <div style={{ padding: "2rem", color: "white" }}>
      {/* Header de perfil */}
      <div style={{ display: "flex", gap: "2rem", alignItems: "center" }}>
        {!userAvatarURL ? (
          <div
            style={{
              width: "150px",
              height: "150px",
              borderRadius: "9999px",
              background: "#222",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              border: "3px solid #0f0"
            }}
          >
            <span className="dot-typing">
              <span></span>
              <span></span>
              <span></span>
            </span>
          </div>
        ) : (
          <img
            src={userAvatarURL}
            alt="Avatar"
            style={{
              width: "150px",
              height: "150px",
              borderRadius: "9999px",
              objectFit: "cover",
              border: "3px solid #0f0"
            }}
          />
        )}
        <div>
          <h1 style={{ fontSize: "2.5rem", fontWeight: "bold", color: "#00ff84", marginBottom: "0.5rem" }}>
            {user.firstName} {user.lastName}
          </h1>
          <p style={{ marginBottom: "0.2rem" }}>@{user.username}</p>
          <p style={{ marginBottom: "0.2rem" }}>Age: {user.age} | Gender: {user.gender}</p>
          <p style={{ marginBottom: "0.2rem" }}>Zone: {user.zone}</p>
        </div>
      </div>

      {/* Sección de equipos */}
      <section style={{ marginTop: "3rem" }}>
        <h2 style={{ fontSize: "2rem", fontWeight: "bold", color: "#00ff84", marginBottom: "1rem" }}>
          My teams
        </h2>

        {teams.length === 0 ? (
          <p style={{ fontStyle: "italic", color: "#ccc" }}>
            You don't have any teams yet. Create one!
          </p>
        ) : (
          <div style={{ display: "grid", gap: "1rem", gridTemplateColumns: "repeat(auto-fill, minmax(280px, 1fr))" }}>
            {teams.map((team: any) => (
              <div key={team.id} style={{ backgroundColor: "#111", padding: "1rem", borderRadius: "0.5rem", border: "1px solid #0f0" }}>
                <div style={{ display: "flex", alignItems: "center", gap: "1rem" }}>
                  <img src={team.imageUrl} alt={team.name} style={{ width: "60px", height: "60px", borderRadius: "0.5rem" }} />
                  <div>
                    <h3 style={{ color: "#00ff84", fontWeight: "bold", fontSize: "1.2rem" }}>{team.name}</h3>
                    <p style={{ fontSize: "0.9rem", color: "#ccc" }}>
                      Capitán: {team.captain.firstName} {team.captain.lastName}
                    </p>
                    <p style={{ fontSize: "0.9rem", color: "#ccc" }}>
                      Miembros: {team.members.length}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
    </CommonLayout>
  );
}
