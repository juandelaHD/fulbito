import { CommonLayout } from "@/components/CommonLayout/CommonLayout.tsx"
import { useLocation } from "wouter"

export const PlayerHomePage = () => {
  const [, navigate] = useLocation()

  return (
    <CommonLayout>
      <section className="flex flex-col items-center">
        <h1 className="text-center text-3xl font-bold mb-10 text-green-400">Welcome back, Player!</h1>

        <img
          src="/img/logo_2-no-background.webp"
          alt="Balonini Logo"
          className="mb-8"
          style={{
            minWidth: "4rem",
            minHeight: "4rem",
            maxWidth: "15rem",
            maxHeight: "15rem",
            display: "block",
          }}
        />

        <div className="flex flex-wrap justify-center gap-6 mb-12">
          <button
            onClick={() => navigate("/fields")}
            className="w-48 h-20 text-xl font-bold text-white bg-green-600 rounded-lg hover:bg-green-700 transition"
          >
            Fields
          </button>
          <button
            onClick={() => navigate("/matches")}
            className="w-48 h-20 text-xl font-bold text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition"
          >
            Matches
          </button>
          <button
            onClick={() => navigate("/teams")}
            className="w-48 h-20 text-xl font-bold text-white bg-green-600 rounded-lg hover:bg-green-700 transition"
          >
            Teams
          </button>
          <button
            onClick={() => navigate("/tournaments")}
            className="w-48 h-20 text-xl font-bold text-white bg-green-600 rounded-lg hover:bg-green-700 transition"
          >
            Tournaments
          </button>
        </div>
      </section>
    </CommonLayout>
  )
}
