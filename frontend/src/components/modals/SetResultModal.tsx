import Modal from "react-modal";

interface Team {
  name?: string;
}

interface Props {
  isOpen: boolean;
  onClose: () => void;
  homeTeam?: Team;
  awayTeam?: Team;
  homeScore: number;
  awayScore: number;
  onChangeHomeScore: (score: number) => void;
  onChangeAwayScore: (score: number) => void;
  onSubmit: () => void;
  isSubmitting: boolean;
}

export function SetResultModal({
   isOpen,
   onClose,
   homeTeam,
   awayTeam,
   homeScore,
   awayScore,
   onChangeHomeScore,
   onChangeAwayScore,
   onSubmit,
   isSubmitting,
  }: Props) {
  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onClose}
      contentLabel="Set Match Result"
      overlayClassName="fixed inset-0 bg-black/50 flex items-center justify-center z-50"
      className="bg-white rounded-lg p-6 max-w-lg mx-auto relative"
    >
      <h2 className="text-xl font-bold mb-4">Ingresar Resultado</h2>
      <div className="flex gap-6 mb-4 justify-center">
        <div className="flex flex-col items-center">
          <label className="mb-1 font-medium">
            {homeTeam?.name ?? "Home"}
          </label>
          <input
            type="number"
            min={0}
            className="w-20 text-center border rounded p-1"
            value={homeScore}
            onChange={e => onChangeHomeScore(Number(e.target.value))}
          />
        </div>
        <div className="flex flex-col items-center">
          <label className="mb-1 font-medium">
            {awayTeam?.name ?? "Away"}
          </label>
          <input
            type="number"
            min={0}
            className="w-20 text-center border rounded p-1"
            value={awayScore}
            onChange={e => onChangeAwayScore(Number(e.target.value))}
          />
        </div>
      </div>
      <div className="flex justify-end gap-2">
        <button
          className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"
          onClick={onClose}
          disabled={isSubmitting}
        >
          Cancelar
        </button>
        <button
          className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          onClick={onSubmit}
          disabled={isSubmitting}
        >
          {isSubmitting ? "Enviando…" : "Aceptar"}
        </button>
      </div>
    </Modal>
  );
}