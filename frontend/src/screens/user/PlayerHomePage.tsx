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
                    onClick={() => navigate("/match")}
                    className="w-48 h-20 text-xl font-bold text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition mb-2"
                >
                    View Matches
                </button>
                <button
                    onClick={() => navigate("/fields")}
                    className="w-48 h-20 text-xl font-bold text-white bg-green-600 rounded-lg hover:bg-green-700 transition mb-2"
                >
                    View Fields
                </button>
                <button
                    onClick={() => navigate("/teams")}
                    className="w-48 h-20 text-xl font-bold text-white bg-green-600 rounded-lg hover:bg-green-700 transition mb-2"
                >
                    View Teams
                </button>
            </div>
        </section>
   </CommonLayout>
  );
};