export type FieldsFilters = {
  name: string
  zone: string
  address: string
  grassType: string
  isIlluminated: boolean
  hasOpenMatch: boolean
  isEnabled: boolean
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
      <div className="flex gap-x-[10px] gap-y-[15px] pb-4">
        <div className="flex flex-col w-1/2 mb-[10px]">
          <label className="text-sm font-medium text-gray-700 mb-1">Name</label>
          <input
            type="text"
            value={filters.name}
            onChange={(e) => setFilters({ ...filters, name: e.target.value })}
            className="border p-2 rounded"
          />
        </div>
        <div className="flex flex-col w-1/2 mb-[10px]">
          <label className="text-sm font-medium text-gray-700 mb-1">Zone</label>
          <input
            type="text"
            value={filters.zone}
            onChange={(e) => setFilters({ ...filters, zone: e.target.value })}
            className="border p-2 rounded"
          />
        </div>
      </div>

      {/* Row 2: Address */}
      <div className="pb-4">
        <div className="flex flex-col w-full mb-[10px]">
          <label className="text-sm font-medium text-gray-700 mb-1">Address</label>
          <input
            type="text"
            value={filters.address}
            onChange={(e) => setFilters({ ...filters, address: e.target.value })}
            className="border p-2 rounded"
          />
        </div>
      </div>

      {/* Row 3: Grass type + Checkboxes */}
      <div className="flex flex-wrap items-start gap-y-[15px] gap-x-[20px] pb-4">
        <div className="flex flex-col mb-[10px] min-w-[200px]">
          <label className="text-sm font-medium text-gray-700 mb-1 mr-[100px]">Grass Type</label>
          <select
            value={filters.grassType}
            onChange={(e) => setFilters({ ...filters, grassType: e.target.value })}
            className="border p-2 rounded"
          >
            <option value="">Any</option>
            <option value="natural">Natural</option>
            <option value="synthetic">Synthetic</option>
            <option value="mixed">Mixed</option>
          </select>
        </div>

        <label className="flex items-center gap-2 mr-[15px] mb-[10px] mt-[25px]">
          <input
            type="checkbox"
            checked={filters.isIlluminated}
            onChange={(e) => setFilters({ ...filters, isIlluminated: e.target.checked })}
          />
          <span>Illuminated</span>
        </label>

        <label className="flex items-center gap-2 mr-[5px] mb-[10px] mt-[25px]">
          <input
            type="checkbox"
            checked={filters.hasOpenMatch}
            onChange={(e) => setFilters({ ...filters, hasOpenMatch: e.target.checked })}
          />
          <span>Has Open Match</span>
        </label>

        <label className="flex items-center gap-2 mr-[5px] mb-[10px] mt-[25px]">
          <input
            type="checkbox"
            checked={filters.isEnabled}
            onChange={(e) => setFilters({ ...filters, isEnabled: e.target.checked })}
          />
          <span>Enabled</span>
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
