import { useState } from "react";
import { usePlayers, useCreatePlayer, useUpdatePlayer, useDeletePlayer } from "../hooks/usePlayers";
import { Card } from "../components/Card";
import type { Player } from "../types";

export function PlayersPage() {
  const { data: players, isLoading } = usePlayers();
  const createPlayer = useCreatePlayer();
  const updatePlayer = useUpdatePlayer();
  const deletePlayer = useDeletePlayer();

  const [editingId, setEditingId] = useState<number | null>(null);
  const [name, setName] = useState("");
  const [nickname, setNickname] = useState("");

  function resetForm() {
    setEditingId(null);
    setName("");
    setNickname("");
  }

  function handleEdit(player: Player) {
    setEditingId(player.id);
    setName(player.name);
    setNickname(player.nickname ?? "");
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    const payload = { name, nickname: nickname || undefined };

    if (editingId) {
      updatePlayer.mutate({ id: editingId, data: payload }, { onSuccess: resetForm });
    } else {
      createPlayer.mutate(payload, { onSuccess: resetForm });
    }
  }

  return (
    <div className="grid md:grid-cols-3 gap-6">
      <Card className="md:col-span-1 h-fit">
        <h2 className="font-semibold mb-3">{editingId ? "Editar jogador" : "Novo jogador"}</h2>
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
            <label className="text-sm text-gray-400">Apelido</label>
            <input
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
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
        <h2 className="font-semibold mb-3">Jogadores</h2>
        {isLoading ? (
          <p className="text-gray-400 text-sm">Carregando...</p>
        ) : (
          <table className="w-full text-sm">
            <thead className="text-gray-400 text-left">
              <tr>
                <th className="pb-2">Nome</th>
                <th className="pb-2">Apelido</th>
                <th className="pb-2"></th>
              </tr>
            </thead>
            <tbody>
              {players?.map((player) => (
                <tr key={player.id} className="border-t border-gray-800">
                  <td className="py-2">{player.name}</td>
                  <td className="text-gray-400">{player.nickname ?? "-"}</td>
                  <td className="text-right space-x-3">
                    <button
                      onClick={() => handleEdit(player)}
                      className="text-blue-400 hover:text-blue-300 text-xs"
                    >
                      Editar
                    </button>
                    <button
                      onClick={() => deletePlayer.mutate(player.id)}
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