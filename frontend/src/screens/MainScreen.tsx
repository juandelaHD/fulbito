import { CommonLayout } from "@/components/CommonLayout/CommonLayout";
import { useLocation } from "wouter";

export const MainScreen = () => {
  const [, navigate] = useLocation();
  return (
 <CommonLayout>
      <section>
        <h1 className="text-center text-2xl font-semibold mb-4">Select your role</h1>
        <h2 className="text-center text-2xl font-semibold mb-8">
          Select the role that applies to you to access the appropriate options
        </h2>
      </section>
      <section>
        <div className="grid grid-cols-2 gap-12 justify-items-center">
          <div className="flex flex-col items-center">
            <button
              onClick={() => navigate("/admin")}
              className="w-50 h-50 text-xl font-bold text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition mb-2"
            >
              Admin
            </button>
            <p className="text-center text-sm text-gray-600 w-40">
              Manage courts, reservations, <br/>
              and overall system settings.
            </p>
          </div>
          <div className="flex flex-col items-center">
            <button
              onClick={() => navigate("/player")}
              className="w-50 h-50 text-xl font-bold text-white bg-green-600 rounded-lg hover:bg-green-700 transition mb-2"
            >
              Player
            </button>
            <p className="text-center text-sm text-gray-600 w-40">
              View and join matches or <br/>
              tournaments available to players.
            </p>
          </div>
        </div>
      </section>
    </CommonLayout>
  );
};
