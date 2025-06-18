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

export const TournamentTable = ({ data, onClickTournament }: Props) => {
  const showActions = data.some((t) => t.onRegister)

  return (
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
            <tr
              key={tournament.id}
              className="hover:bg-gray-100"
            >
              <td
                onClick={() => onClickTournament?.(tournament)}
                className="px-4 py-3 whitespace-nowrap text-sm font-medium text-gray-900 cursor-pointer"
              >
                {tournament.name}
              </td>
              <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{tournament.startDate}</td>
              <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{tournament.format}</td>
              <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{tournament.status}</td>
              {showActions && (
                <td className="px-4 py-3 whitespace-nowrap text-sm">
                  {tournament.onRegister && (
                    <button
                      onClick={tournament.onRegister}
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
  )
}
