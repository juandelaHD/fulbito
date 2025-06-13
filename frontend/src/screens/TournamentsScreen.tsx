import { useState } from "react"
import { useFormContext } from "@/config/form-context"
import { CommonLayout } from "@/components/CommonLayout/CommonLayout"
import { TextField } from "@/components/form-components/TextField/TextField"
import { SelectField } from "@/components/form-components/SelectField/SelectField"
import { FormContainer } from "@/components/form-components/FormContainer/FormContainer"
import { useGetTournaments } from "@/services/TournamentServices"
import { AddTournamentModal } from "@/components/modals/AddTournamentModal"

export const TournamentsScreen = () => {
  const [showAddModal, setShowAddModal] = useState(false)
  const [filters, setFilters] = useState({ name: "", status: "" })

  const { data: tournaments = [], isLoading } = useGetTournaments(filters)

  const form = useFormContext()

  const handleSubmit = () => {
    setFilters(form.values)
  }

  return (
    <CommonLayout title="Tournaments">
      <FormContainer extraError={null} submitLabel="Buscar" onSubmit={handleSubmit}>
        <div className="flex gap-4 items-end mb-4">
          <TextField name="name" label="Nombre del torneo" />
          <SelectField
            name="status"
            label="Estado"
            options={[
              { label: "Todos", value: "" },
              { label: "Abierto para inscripción", value: "OPEN" },
              { label: "En curso", value: "ONGOING" },
              { label: "Finalizado", value: "FINISHED" },
            ]}
          />
          <button
            type="submit"
            className="bg-blue-600 text-white px-4 py-2 rounded-xl"
          >
            Buscar
          </button>

          <button
            type="button"
            onClick={() => setShowAddModal(true)}
            className="ml-auto bg-green-600 text-white px-4 py-2 rounded-xl"
          >
            + Add Tournament 🏆
          </button>
        </div>
      </FormContainer>

      {isLoading ? (
        <p>Cargando torneos...</p>
      ) : tournaments.length === 0 ? (
        <p>No hay torneos que coincidan con los filtros aplicados.</p>
      ) : (
        <div className="grid gap-4">
          {tournaments.map((tournament) => (
            <div key={tournament.id} className="border p-4 rounded-xl shadow">
              <h3 className="text-lg font-bold">{tournament.name}</h3>
              <p>📅 Inicio: {tournament.startDate}</p>
              <p>🏁 Formato: {tournament.format}</p>
              <p>📌 Estado: {tournament.status}</p>
            </div>
          ))}
        </div>
      )}

      <AddTournamentModal isOpen={showAddModal} onClose={() => setShowAddModal(false)} />
    </CommonLayout>
  )
}
