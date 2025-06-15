
import { useGetMyProfile } from "@/services/UserServices";
import { useImageById } from "@/services/ImageServices.ts";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { TeamsScreen } from "@/screens/user/TeamsScreen.tsx";

export default function UserProfileScreen() {
  const { data: user, isLoading: loadingUser } = useGetMyProfile();
  const userAvatarURL= useImageById(user?.avatarUrl);

  if (loadingUser) return <p style={{ padding: "2rem", color: "white" }}>Loading...</p>;
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
        {/* Equipos */}
        {user.role === "USER" && <TeamsScreen />}
      </section>

    </div>
    </CommonLayout>
  );
}
