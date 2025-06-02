import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { useToken } from "@/services/TokenContext";
import { useLocation } from "wouter";

export const MainScreen = () => {
  const [, navigate] = useLocation();
  const [tokenData] = useToken();
  if (tokenData.state === "LOGGED_OUT") {
    return (
      <CommonLayout>
        <section className="max-w-xl mx-auto text-center mt-10 px-4">
          <h1 className="text-2xl font-semibold mb-2">Not logged in</h1>
          <p className="text-red-600">Please log in to access this screen.</p>
        </section>
      </CommonLayout>
    );
  }
  const user = tokenData.user;
  const isUser = user.role === "USER";
  const isAdmin = user.role === "ADMIN";
  return (
    <CommonLayout>
      <section className="max-w-xl mx-auto text-center mt-10 px-4">
        <h1 className="text-2xl font-semibold mb-2">
          Welcome, {user.firstName}!
        </h1>
        <h2 className="text-green-800 font-light text-sm mb-8">
          {isUser
            ? "Explore matches and tournaments available for you"
            : isAdmin
            ? "Manage your fields and reservations efficiently"
            : "Choose an option to get started"}
        </h2>
        <div className="space-y-4">
          {isUser && (
            <>
              <button
                onClick={() => navigate("/matches")}
                className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700"
              >
                View Matches
              </button>
              <button
                onClick={() => navigate("/tournaments")}
                className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700"
              >
                View Tournaments
              </button>
            </>
          )}
          {isAdmin && (
            <>
              <button
                onClick={() => navigate("/create-field")}
                className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700"
              >
                Manage Fields
              </button>
              <button
                onClick={() => navigate("/reservations")}
                className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700"
              >
                Manage Reservations
              </button>
            </>
          )}
          {!isUser && !isAdmin && (
            <p className="text-red-600">User role not defined.</p>
          )}
        </div>
      </section>
    </CommonLayout>
  );
};
