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
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <input
        type="text"
        placeholder="Name"
        value={filters.name}
        onChange={(e) => setFilters({ ...filters, name: e.target.value })}
        className="input"
      />
      <input
        type="text"
        placeholder="Zone"
        value={filters.zone}
        onChange={(e) => setFilters({ ...filters, zone: e.target.value })}
        className="input"
      />
      <input
        type="text"
        placeholder="Address"
        value={filters.address}
        onChange={(e) => setFilters({ ...filters, address: e.target.value })}
        className="input"
      />
      <select
        value={filters.grassType}
        onChange={(e) => setFilters({ ...filters, grassType: e.target.value })}
        className="input"
      >
        <option value="">Any grass type</option>
        <option value="Césped natural">Natural</option>
        <option value="Sintético">Synthetic</option>
        <option value="Mixto">Mixed</option>
      </select>
      <label className="flex items-center space-x-2">
        <input
          type="checkbox"
          checked={filters.isIlluminated}
          onChange={(e) => setFilters({ ...filters, isIlluminated: e.target.checked })}
        />
        <span>Illuminated</span>
      </label>
      <label className="flex items-center space-x-2">
        <input
          type="checkbox"
          checked={filters.hasOpenScheduledMatch}
          onChange={(e) => setFilters({ ...filters, hasOpenScheduledMatch: e.target.checked })}
        />
        <span>Has Open Match</span>
      </label>
      <button onClick={onSearch} className="btn col-span-full md:col-span-1">
        Search
      </button>
    </div>
  )
}
