import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx";
import { useLocation } from "wouter";

export const AdminHomePage = () => {
  const [, navigate] = useLocation();

  return (
     <CommonLayout>
        <section className="p-6">
            <h1 className="text-2xl font-bold mb-4 text-center">Admin Panel</h1>
            <div className="grid grid-cols-2 gap-12 justify-items-center">
                <button
                onClick={() => navigate("/fields")}
                className="w-50 h-50 text-xl font-bold text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition mb-2"
                >
                Manage Fields
                </button>
                <button
                onClick={() => navigate("/reservations")}
                className="w-50 h-50 text-xl font-bold text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition mb-2"
                >
                Manage Reservations
                </button>
            </div>
        </section>
     </CommonLayout>
  );
};
