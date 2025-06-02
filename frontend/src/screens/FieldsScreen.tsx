import { FieldsTable, Field } from "@/components/tables/FieldsTable"
import { toast } from "react-hot-toast"

const mockFields: Field[] = [
  {
    id: 1,
    name: "Cancha 1",
    grassType: "Césped natural",
    lighting: "Yes",
    zone: "Belgrano",
    address: "Juramento 123",
    photos: "https://via.placeholder.com/150",
  },
  {
    id: 2,
    name: "Cancha 2",
    grassType: "Sintético",
    lighting: "No",
    zone: "Palermo",
    address: "Av. Santa Fe 456",
    photos: "https://via.placeholder.com/150",
  },
  {
    id: 3,
    name: "Cancha 3",
    grassType: "Sintético",
    lighting: "Yes",
    zone: "Caballito",
    address: "Av. Rivadavia 789",
    photos: "https://via.placeholder.com/150",
  },
  {
    id: 4,
    name: "Cancha 4",
    grassType: "Césped natural",
    lighting: "No",
    zone: "Recoleta",
    address: "Calle Libertad 333",
    photos: "https://via.placeholder.com/150",
  },
  {
    id: 5,
    name: "Cancha 5",
    grassType: "Sintético",
    lighting: "No",
    zone: "Villa Urquiza",
    address: "Av. Triunvirato 777",
    photos: "https://via.placeholder.com/150",
  },
  {
    id: 6,
    name: "Cancha 6",
    grassType: "Mixto",
    lighting: "No",
    zone: "Almagro",
    address: "Medrano 800",
    photos: "https://via.placeholder.com/150",
  },
  {
    id: 7,
    name: "Cancha 7",
    grassType: "Sintético",
    lighting: "Yes",
    zone: "Núñez",
    address: "Av. Del Libertador 999",
    photos: "https://via.placeholder.com/150",
  },
]

export default function FieldsScreen() {
  const handleReserve = (field: Field) => {
    toast.error(`⚠️ Reservations for ${field.name} is not implemented yet!`)
  }

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold">Available Fields</h1>
      <FieldsTable data={mockFields} onReserve={handleReserve} />
    </div>
  )
}