// src/screens/OpenMatchesScreen.tsx (versión de mock rápido)
import { useState } from "react"

import { OpenMatchesTable, Match } from "@/components/tables/OpenMatchesTable"

const mockMatches: Match[] = [
  {
    id: 1,
    fieldName: "Canchita de Prueba",
    date: "2025-06-15",
    startTime: "19:00",
    endTime: "20:00",
    inscritos: 1,
    minPlayers: 5,
    maxPlayers: 10,
  },
  {
    id: 2,
    fieldName: "Otra Canchita",
    date: "2025-06-16",
    startTime: "18:00",
    endTime: "19:00",
    inscritos: 8,
    minPlayers: 8,
    maxPlayers: 10,
  },
]

export default function OpenMatchesScreen() {
  const [matches, setMatches] = useState<Match[]>(mockMatches)
  const [joiningId, setJoiningId] = useState<number | null>(null)

  const handleJoin = (matchId: number) => {
    setJoiningId(matchId)
    setTimeout(() => {
      // Simulamos que se anotó un jugador más
      setMatches((prev) =>
        prev.map((m) =>
          m.id === matchId
            ? { ...m, inscritos: m.inscritos + 1 }
            : m
        )
      )
      setJoiningId(null)
    }, 500)
  }

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold">Open Matches</h1>
      <OpenMatchesTable
        data={matches}
        onJoin={handleJoin}
        joiningId={joiningId}
      />
    </div>
  )
}
