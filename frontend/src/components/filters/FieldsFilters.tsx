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
    <div className="space-y-4">
      {/* Row 1: Name + Zone */}
      <div className="flex flex-wrap gap-4">
        <input
          type="text"
          placeholder="Name"
          value={filters.name}
          onChange={(e) => setFilters({ ...filters, name: e.target.value })}
          className="border p-2 rounded flex-1 min-w-[200px]"
        />
        <input
          type="text"
          placeholder="Zone"
          value={filters.zone}
          onChange={(e) => setFilters({ ...filters, zone: e.target.value })}
          className="border p-2 rounded flex-1 min-w-[200px]"
        />
      </div>

      {/* Row 2: Address */}
      <div className="flex flex-wrap gap-4">
        <input
          type="text"
          placeholder="Address"
          value={filters.address}
          onChange={(e) => setFilters({ ...filters, address: e.target.value })}
          className="border p-2 rounded flex-1 min-w-[200px]"
        />
      </div>

      {/* Row 3: GrassType + Checkboxes + Button */}
      <div className="flex flex-wrap items-center gap-4">
        <select
          value={filters.grassType}
          onChange={(e) => setFilters({ ...filters, grassType: e.target.value })}
          className="border p-2 rounded min-w-[200px]"
        >
          <option value="">Any grass type</option>
          <option value="Césped natural">Natural</option>
          <option value="Sintético">Synthetic</option>
          <option value="Mixto">Mixed</option>
        </select>

        <label className="flex items-center gap-2">
          <input
            type="checkbox"
            checked={filters.isIlluminated}
            onChange={(e) => setFilters({ ...filters, isIlluminated: e.target.checked })}
          />
          <span>Illuminated</span>
        </label>

        <label className="flex items-center gap-2">
          <input
            type="checkbox"
            checked={filters.hasOpenScheduledMatch}
            onChange={(e) => setFilters({ ...filters, hasOpenScheduledMatch: e.target.checked })}
          />
          <span>Has Open Match</span>
        </label>

        <button
          onClick={onSearch}
          className="bg-green-700 text-white px-4 py-2 rounded hover:bg-green-800 transition"
        >
          Search
        </button>
      </div>
    </div>
  )
}
