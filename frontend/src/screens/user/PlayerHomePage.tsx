import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import {useLocation} from "wouter";

export const PlayerHomePage = () => {
  const [, navigate] = useLocation();

  return (
   <CommonLayout>
        <section>
            <h1 className="text-center text-2xl font-semibold mb-4">Welcome, Player</h1>
            <div className="flex flex-wrap gap-8 justify-center">
                <button
                    onClick={() => navigate("/fields")}
                    className="w-48 h-20 text-xl font-bold text-white bg-green-600 rounded-lg hover:bg-green-700 transition mb-2"
                >
                    Fields
                </button>
                <button
                    onClick={() => navigate("/matches")}
                    className="w-48 h-20 text-xl font-bold text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition mb-2"
                >
                    Matches
                </button>
                <button
                    onClick={() => navigate("/teams")}
                    className="w-48 h-20 text-xl font-bold text-white bg-green-600 rounded-lg hover:bg-green-700 transition mb-2"
                >
                    Teams
                </button>
                <button
                    onClick={() => navigate("/tournaments")}
                    className="w-48 h-20 text-xl font-bold text-white bg-green-600 rounded-lg hover:bg-green-700 transition mb-2"
                >
                    Tournaments
                </button>
            </div>
        </section>
   </CommonLayout>
  );
};