import { useState } from "react";
import { useTeams, useCreateTeam, useUpdateTeam, useDeleteTeam } from "../hooks/useTeams";
import { Card } from "../components/Card";
import type { Team } from "../types";

export function TeamsPage() {
  const { data: teams, isLoading } = useTeams();
  const createTeam = useCreateTeam();
  const updateTeam = useUpdateTeam();
  const deleteTeam = useDeleteTeam();

  const [editingId, setEditingId] = useState<number | null>(null);
  const [name, setName] = useState("");
  const [country, setCountry] = useState("");
  const [logoUrl, setLogoUrl] = useState("");

  function resetForm() {
    setEditingId(null);
    setName("");
    setCountry("");
    setLogoUrl("");
  }

  function handleEdit(team: Team) {
    setEditingId(team.id);
    setName(team.name);
    setCountry(team.country ?? "");
    setLogoUrl(team.logoUrl ?? "");
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    const payload = { name, country: country || undefined, logoUrl: logoUrl || undefined };

    if (editingId) {
      updateTeam.mutate({ id: editingId, data: payload }, { onSuccess: resetForm });
    } else {
      createTeam.mutate(payload, { onSuccess: resetForm });
    }
  }

  return (
    <div className="grid md:grid-cols-3 gap-6">
      <Card className="md:col-span-1 h-fit">
        <h2 className="font-semibold mb-3">{editingId ? "Editar time" : "Novo time"}</h2>
        <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="text-sm text-gray-400">Nome</label>
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              className="w-full mt-1 bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="text-sm text-gray-400">País</label>
            <input
              value={country}
              onChange={(e) => setCountry(e.target.value)}
              className="w-full mt-1 bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="text-sm text-gray-400">URL do logo</label>
            <input
              value={logoUrl}
              onChange={(e) => setLogoUrl(e.target.value)}
              className="w-full mt-1 bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm"
            />
          </div>
          <div className="flex gap-2">
            <button
              type="submit"
              className="bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-medium px-4 py-2 rounded-lg"
            >
              {editingId ? "Salvar" : "Criar"}
            </button>
            {editingId && (
              <button
                type="button"
                onClick={resetForm}
                className="text-sm text-gray-400 hover:text-gray-200 px-4 py-2"
              >
                Cancelar
              </button>
            )}
          </div>
        </form>
      </Card>

      <Card className="md:col-span-2">
        <h2 className="font-semibold mb-3">Times</h2>
        {isLoading ? (
          <p className="text-gray-400 text-sm">Carregando...</p>
        ) : (
          <table className="w-full text-sm">
            <thead className="text-gray-400 text-left">
              <tr>
                <th className="pb-2">Nome</th>
                <th className="pb-2">País</th>
                <th className="pb-2"></th>
              </tr>
            </thead>
            <tbody>
              {teams?.map((team) => (
                <tr key={team.id} className="border-t border-gray-800">
                  <td className="py-2 flex items-center gap-2">
                    {team.logoUrl && (
                      <img src={team.logoUrl} alt="" className="w-5 h-5 rounded-full object-cover" />
                    )}
                    {team.name}
                  </td>
                  <td className="text-gray-400">{team.country ?? "-"}</td>
                  <td className="text-right space-x-3">
                    <button
                      onClick={() => handleEdit(team)}
                      className="text-blue-400 hover:text-blue-300 text-xs"
                    >
                      Editar
                    </button>
                    <button
                      onClick={() => deleteTeam.mutate(team.id)}
                      className="text-red-400 hover:text-red-300 text-xs"
                    >
                      Excluir
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>
    </div>
  );
}