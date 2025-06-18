export type TournamentFilters = {
  organizerUsername: string
  openForRegistration: boolean
}

type Props = {
  filters: TournamentFilters
  setFilters: (filters: TournamentFilters) => void
  onSearch: () => void
}

export const TournamentFiltersContainer = ({ filters, setFilters, onSearch }: Props) => {
  return (
    <div className="space-y-0">
      {/* Row 1: Organizer name + checkbox */}
      <div className="flex gap-x-[10px] gap-y-[15px] pb-4">
        <div className="flex flex-col w-2/3 mb-[10px]">
          <label className="text-sm font-medium text-gray-700 mb-1">Organizer Username</label>
          <input
            type="text"
            value={filters.organizerUsername}
            onChange={(e) =>
              setFilters({ ...filters, organizerUsername: e.target.value })
            }
            className="border p-2 rounded"
          />
        </div>

        <div className="flex items-end w-1/3 mb-[10px]">
          <label className="flex items-center space-x-2 text-sm font-medium text-gray-700">
            <input
              type="checkbox"
              checked={filters.openForRegistration}
              onChange={(e) =>
                setFilters({ ...filters, openForRegistration: e.target.checked })
              }
              className="w-4 h-4"
            />
            <span>Open for Registration</span>
          </label>
        </div>
      </div>

      {/* Row 2: Search button */}
      <div className="w-full flex justify-end">
        <button
          onClick={onSearch}
          className="bg-green-700 text-white px-4 py-2 rounded hover:bg-green-800 transition mb-[25px]"
        >
          🔎 Search
        </button>
      </div>
    </div>
  )
}
