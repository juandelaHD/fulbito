export type TournamentFilters = {
  name: string
  status?: "OPEN" | "ONGOING" | "FINISHED"
}

type Props = {
  filters: TournamentFilters
  setFilters: (filters: TournamentFilters) => void
  onSearch: () => void
}

export const TournamentFiltersContainer = ({ filters, setFilters, onSearch }: Props) => {
  return (
    <div className="space-y-0">
      {/* Row 1: Name + Status */}
      <div className="flex gap-x-[10px] gap-y-[15px] pb-4">
        <div className="flex flex-col w-1/2 mb-[10px]">
          <label className="text-sm font-medium text-gray-700 mb-1">Tournament Name</label>
          <input
            type="text"
            value={filters.name}
            onChange={(e) => setFilters({ ...filters, name: e.target.value })}
            className="border p-2 rounded"
          />
        </div>

        <div className="flex flex-col w-1/2 mb-[10px]">
          <label className="text-sm font-medium text-gray-700 mb-1">Status</label>
          <select
            value={filters.status || ""}
            onChange={(e) =>
              setFilters({ ...filters, status: e.target.value === "" ? undefined : e.target.value as TournamentFilters["status"] })
            }
            className="border p-2 rounded"
          >
            <option value="">All</option>
            <option value="OPEN">Open for registration</option>
            <option value="ONGOING">Ongoing</option>
            <option value="FINISHED">Finished</option>
          </select>
        </div>
      </div>

      {/* Row 2: Search button aligned right */}
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
