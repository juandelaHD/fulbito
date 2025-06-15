import { useState } from "react";
import {useSearchUserByUsername} from "@/services/UserServices";
import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import {useImageById} from "@/services/ImageServices.ts";

export default function SearchUsersScreen() {
  const [username, setUsername] = useState("");
  const [query, setQuery] = useState<string | null>(null);


  const { data: user, isFetching, isError } = useSearchUserByUsername(query);
  const userAvatarURL= useImageById(user?.avatarUrl);


  const handleSearch = () => {
    if (username.trim() !== "") {
      setQuery(username.trim());
    }
  };

  return (
    <CommonLayout>
    <div style={{ padding: "2rem", color: "white" }}>
      <h1 style={{ fontSize: "2rem", fontWeight: "bold", color: "#00ff84", marginBottom: "1rem" }}>
        Buscar Jugador
      </h1>

      <div style={{ marginBottom: "1rem", display: "flex", gap: "0.5rem" }}>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          style={{ padding: "0.5rem", flex: 1, borderRadius: "0.25rem", border: "1px solid #ccc" }}
        />
        <button onClick={handleSearch} style={{ padding: "0.5rem 1rem", backgroundColor: "#00ff84", border: "none", borderRadius: "0.25rem", fontWeight: "bold" }}>
          Buscar
        </button>
      </div>

      {isFetching && <p>Buscando...</p>}

      {isError && <p style={{ color: "red" }}>Usuario no encontrado.</p>}

      {user && (
        <div style={{ marginTop: "1rem", padding: "1rem", border: "1px solid #0f0", borderRadius: "0.5rem", backgroundColor: "#111" }}>
          <div style={{ display: "flex", gap: "1rem", alignItems: "center" }}>
            <img
              src={userAvatarURL || "/img/default_avatar.png"}
              alt="Avatar"
              style={{ width: "96px", height: "96px", borderRadius: "50%", border: "2px solid #0f0", objectFit: "cover" }}
            />
            <div>
              <h2 style={{ fontSize: "1.5rem", color: "#00ff84" }}>
                {user.firstName} {user.lastName}
              </h2>
              <p>@{user.username}</p>
              <p>Edad: {user.age} | Género: {user.gender}</p>
              <p>Zona: {user.zone}</p>
            </div>
          </div>
        </div>
      )}
    </div>
    </CommonLayout>
  );
}
