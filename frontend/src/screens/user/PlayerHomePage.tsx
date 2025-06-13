import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { useLocation } from "wouter";

export const PlayerHomePage = () => {
  const [, navigate] = useLocation();

  return (
   <CommonLayout>
        <section>
            <h1 className="text-center text-2xl font-semibold mb-4">Welcome, Player</h1>
            <div className="grid grid-cols-2 gap-12 justify-items-center">
                <button
                onClick={() => navigate("/matches")}
                className="w-50 h-50 text-xl font-bold text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition mb-2"
                >
                View Matches
                </button>
                <button
                onClick={() => navigate("/tournaments")}
                className="w-50 h-50 text-xl font-bold text-white bg-green-600 rounded-lg hover:bg-green-700 transition mb-2"
                >
                Join Tournaments
                </button>
            </div>
        </section>
   </CommonLayout>
  );
};