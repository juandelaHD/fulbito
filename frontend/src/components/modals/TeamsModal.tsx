import { RawTeamDTO } from "@/services/UserServices.ts";

type TeamsModalProps = {
  team: RawTeamDTO;
  onClose: () => void;
};

export function TeamsModal({ team, onClose }: TeamsModalProps) {
  return (
    <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md shadow-lg relative">
        <button
          onClick={onClose}
          className="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
        >
          ×
        </button>
        <div className="flex items-center gap-4 mb-4">
          <div>
            <h2 className="text-xl font-bold">{team.name}</h2>
            <div className="flex items-center gap-2 mt-1">
              <span
                className="inline-block w-5 h-5 rounded-full border"
                style={{ background: team.mainColor }}
                title="Color principal"
              />
              <span
                className="inline-block w-5 h-5 rounded-full border"
                style={{ background: team.secondaryColor }}
                title="Color secundario"
              />
            </div>
          </div>
        </div>
        <p className="mb-2">
          <span className="font-semibold">Ranking:</span> {team.ranking}
        </p>
        <p className="mb-2">
          <span className="font-semibold">Capitán:</span> {team.captain.firstName} {team.captain.lastName}
        </p>
        <div>
          <span className="font-semibold">Jugadores:</span>
          <ul className="mt-1 space-y-1">
            {team.members.map((member) => (
              <li key={member.id} className="flex items-center gap-2">
                <img
                  src={member.avatarUrl}
                  alt={member.username}
                  className="w-6 h-6 rounded-full object-cover"
                />
                <span>{member.firstName} {member.lastName}</span>
                <span className="text-xs text-gray-500">({member.username})</span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
}