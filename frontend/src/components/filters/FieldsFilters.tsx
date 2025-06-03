export type FieldsFilters = {
  name: string
  zone: string
  address: string
  grassType: string
  isIlluminated: boolean
  hasOpenScheduledMatch: boolean
}

type Props = {
  filters: FieldsFilters
  setFilters: (filters: FieldsFilters) => void
  onSearch: () => void
}

export const FieldsFiltersContainer = ({ filters, setFilters, onSearch }: Props) => {
  return (
    <div className="space-y-0">
      {/* Row 1: Name + Zone */}
      <div className="flex flex-wrap gap-x-[10px] gap-y-[15px] pb-4">
        <input
          type="text"
          placeholder="Name"
          value={filters.name}
          onChange={(e) => setFilters({ ...filters, name: e.target.value })}
          className="border p-2 rounded mb-[10px] flex-1 min-w-[200px]"
        />
        <input
          type="text"
          placeholder="Zone"
          value={filters.zone}
          onChange={(e) => setFilters({ ...filters, zone: e.target.value })}
          className="border p-2 rounded mb-[10px] flex-1 min-w-[200px]"
        />
      </div>

      {/* Row 2: Address (100% width) */}
      <div className="pb-4">
        <input
          type="text"
          placeholder="Address"
          value={filters.address}
          onChange={(e) => setFilters({ ...filters, address: e.target.value })}
          className="border p-2 rounded w-full mb-[10px]"
        />
      </div>

      {/* Row 3: Grass type + Checkboxes */}
      <div className="flex flex-wrap items-center gap-y-[15px] pb-4">
        <select
          value={filters.grassType}
          onChange={(e) => setFilters({ ...filters, grassType: e.target.value })}
          className="border p-2 rounded mr-[30px] mb-[10px] min-w-[200px]"
        >
          <option value="">Any grass type</option>
          <option value="Césped natural">Natural</option>
          <option value="Sintético">Synthetic</option>
          <option value="Mixto">Mixed</option>
        </select>

        <label className="flex items-center gap-2 mr-[15px] mb-[10px]">
          <input
            type="checkbox"
            checked={filters.isIlluminated}
            onChange={(e) => setFilters({ ...filters, isIlluminated: e.target.checked })}
          />
          <span>Illuminated</span>
        </label>

        <label className="flex items-center gap-2 mr-[5px] mb-[10px]">
          <input
            type="checkbox"
            checked={filters.hasOpenScheduledMatch}
            onChange={(e) => setFilters({ ...filters, hasOpenScheduledMatch: e.target.checked })}
          />
          <span>Has Open Match</span>
        </label>
      </div>

      {/* Row 4: Search button aligned right */}
      <div className="w-full flex justify-end">
        <button
          onClick={onSearch}
          className="bg-green-700 text-white px-4 py-2 rounded hover:bg-green-800 transition mb-[25px]"
        >
          🔎Search
        </button>
      </div>
    </div>
  )
}
