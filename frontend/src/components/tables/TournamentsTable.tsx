export type TournamentForTable = {
  id: number
  name: string
  startDate: string
  format: string
  status: string
}

type Props = {
  data: TournamentForTable[]
  onClickTournament?: (t: TournamentForTable) => void
}

export const TournamentTable = ({ data, onClickTournament }: Props) => {
  return (
    <div className="overflow-x-auto border rounded-lg">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Start Date</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Format</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {data.map((tournament) => (
            <tr
              key={tournament.id}
              className="hover:bg-gray-100 cursor-pointer"
              onClick={() => onClickTournament?.(tournament)}
            >
              <td className="px-4 py-3 whitespace-nowrap text-sm font-medium text-gray-900">{tournament.name}</td>
              <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{tournament.startDate}</td>
              <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{tournament.format}</td>
              <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{tournament.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}