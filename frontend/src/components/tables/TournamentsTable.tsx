import { useState } from "react"
import { TournamentTeamRegistrationModal } from "@/components/modals/TournamentTeamRegistrationModal"

export type TournamentForTable = {
  id: number
  name: string
  startDate: string
  format: string
  status: string
  onRegister?: () => void
}

type Props = {
  data: TournamentForTable[]
  onClickTournament?: (t: TournamentForTable) => void
}

const formatLabelMap: Record<string, string> = {
  SINGLE_ELIMINATION: "Direct Elimination",
  GROUP_STAGE_WITH_ELIMINATION: "Group Stage + Elimination",
  ROUND_ROBIN: "League",
}

const statusLabelMap: Record<string, string> = {
  OPEN_FOR_REGISTRATION: "Open for Registration",
  IN_PROGRESS: "In Progress",
  FINISHED: "Finished",
  CANCELLED: "Cancelled",
}


export const TournamentTable = ({ data, onClickTournament }: Props) => {
  const [showModal, setShowModal] = useState(false)
  const [selectedTournament, setSelectedTournament] = useState<{
  id: number
  name: string
} | null>(null)

  const handleOpenRegister = (t: { id: number; name: string }) => {
    setSelectedTournament(t)
    setShowModal(true)
  }
  const handleCloseRegister = () => {
    setShowModal(false)
    setSelectedTournament(null)
  }

  const showActions = data.some((t) => t.onRegister)

  return (
    <>
      <div className="overflow-x-auto border rounded-lg">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Start Date</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Format</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
              {showActions && (
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Registration</th>
              )}
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {data.map((tournament) => (
              <tr key={tournament.id} className="hover:bg-gray-100">
                <td
                  onClick={() => onClickTournament?.(tournament)}
                  className="px-4 py-3 whitespace-nowrap text-sm font-medium text-gray-900 cursor-pointer"
                >
                  {tournament.name}
                </td>
                <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{tournament.startDate}</td>
                <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">
                  {formatLabelMap[tournament.format] ?? tournament.format}
                </td>
                <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">
                  {statusLabelMap[tournament.status] ?? tournament.status}
                </td>
                {showActions && (
                  <td className="px-4 py-3 whitespace-nowrap text-sm">
                    {tournament.onRegister && (
                      <button
                        onClick={() => handleOpenRegister(tournament)}
                        className="bg-blue-600 text-white px-3 py-1 rounded text-sm hover:bg-blue-700 transition"
                      >
                        ✍️ Register My Team
                      </button>
                    )}
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {selectedTournament !== null && (
        <TournamentTeamRegistrationModal
          isOpen={showModal}
          onClose={handleCloseRegister}
          tournamentId={selectedTournament.id}
          tournamentName={selectedTournament.name}
        />
      )}
    </>
  )
}
