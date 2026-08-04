import { useState } from "react";
import { useMatches, useCreateMatch, useDeleteMatch } from "../hooks/useMatches";
import { usePlayers } from "../hooks/usePlayers";
import { useTeams } from "../hooks/useTeams";
import { Card } from "../components/Card";

export function MatchesPage() {
  const { data: matches, isLoading } = useMatches();
  const { data: players } = usePlayers();
  const { data: teams } = useTeams();
  const createMatch = useCreateMatch();
  const deleteMatch = useDeleteMatch();

  const [date, setDate] = useState("");
  const [homePlayerId, setHomePlayerId] = useState("");
  const [awayPlayerId, setAwayPlayerId] = useState("");
  const [homeTeamId, setHomeTeamId] = useState("");
  const [awayTeamId, setAwayTeamId] = useState("");
  const [homeTeamName, setHomeTeamName] = useState("");
  const [awayTeamName, setAwayTeamName] = useState("");
  const [homeScore, setHomeScore] = useState("");
  const [awayScore, setAwayScore] = useState("");

  function resetForm() {
    setDate("");
    setHomePlayerId("");
    setAwayPlayerId("");
    setHomeTeamId("");
    setAwayTeamId("");
    setHomeTeamName("");
    setAwayTeamName("");
    setHomeScore("");
    setAwayScore("");
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    createMatch.mutate(
      {
        date,
        homePlayerId: Number(homePlayerId),
        awayPlayerId: Number(awayPlayerId),
        homeTeamId: homeTeamId ? Number(homeTeamId) : undefined,
        awayTeamId: awayTeamId ? Number(awayTeamId) : undefined,
        homeTeamName: homeTeamId ? undefined : homeTeamName,
        awayTeamName: awayTeamId ? undefined : awayTeamName,
        homeScore: Number(homeScore),
        awayScore: Number(awayScore),
      },
      { onSuccess: resetForm }
    );
  }

  return (
    <div className="grid md:grid-cols-3 gap-6">
      <Card className="md:col-span-1 h-fit">
        <h2 className="font-semibold mb-3">Nova partida</h2>
        <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="text-sm text-gray-400">Data/hora</label>
            <input
              type="datetime-local"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              required
              className="w-full mt-1 bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm"
            />
          </div>

          <div className="grid grid-cols-2 gap-2">
            <div>
              <label className="text-sm text-gray-400">Jogador casa</label>
              <select
                value={homePlayerId}
                onChange={(e) => setHomePlayerId(e.target.value)}
                required
                className="w-full mt-1 bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm"
              >
                <option value="">Selecione</option>
                {players?.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.name}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="text-sm text-gray-400">Jogador visitante</label>
              <select
                value={awayPlayerId}
                onChange={(e) => setAwayPlayerId(e.target.value)}
                required
                className="w-full mt-1 bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm"
              >
                <option value="">Selecione</option>
                {players?.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.name}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div>
            <label className="text-sm text-gray-400">Time casa (catálogo, opcional)</label>
            <select
              value={homeTeamId}
              onChange={(e) => setHomeTeamId(e.target.value)}
              className="w-full mt-1 bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm"
            >
              <option value="">Nenhum (digitar nome)</option>
              {teams?.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.name}
                </option>
              ))}
            </select>
            {!homeTeamId && (
              <input
                placeholder="Nome do time casa"
                value={homeTeamName}
                onChange={(e) => setHomeTeamName(e.target.value)}
                required={!homeTeamId}
                className="w-full mt-2 bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm"
              />
            )}
          </div>

          <div>
            <label className="text-sm text-gray-400">Time visitante (catálogo, opcional)</label>
            <select
              value={awayTeamId}
              onChange={(e) => setAwayTeamId(e.target.value)}
              className="w-full mt-1 bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm"
            >
              <option value="">Nenhum (digitar nome)</option>
              {teams?.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.name}
                </option>
              ))}
            </select>
            {!awayTeamId && (
              <input
                placeholder="Nome do time visitante"
                value={awayTeamName}
                onChange={(e) => setAwayTeamName(e.target.value)}
                required={!awayTeamId}
                className="w-full mt-2 bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm"
              />
            )}
          </div>

          <div className="grid grid-cols-2 gap-2">
            <div>
              <label className="text-sm text-gray-400">Placar casa</label>
              <input
                type="number"
                min={0}
                value={homeScore}
                onChange={(e) => setHomeScore(e.target.value)}
                required
                className="w-full mt-1 bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm"
              />
            </div>
            <div>
              <label className="text-sm text-gray-400">Placar visitante</label>
              <input
                type="number"
                min={0}
                value={awayScore}
                onChange={(e) => setAwayScore(e.target.value)}
                required
                className="w-full mt-1 bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm"
              />
            </div>
          </div>

          <button
            type="submit"
            className="w-full bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-medium px-4 py-2 rounded-lg"
          >
            Cadastrar partida
          </button>
        </form>
      </Card>

      <Card className="md:col-span-2">
        <h2 className="font-semibold mb-3">Partidas</h2>
        {isLoading ? (
          <p className="text-gray-400 text-sm">Carregando...</p>
        ) : (
          <div className="space-y-2">
            {matches
              ?.slice()
              .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
              .map((m) => (
                <div
                  key={m.id}
                  className="flex justify-between items-center border-t border-gray-800 pt-2 first:border-0 first:pt-0"
                >
                  <div className="text-sm">
                    <p>
                      {m.homePlayerName} ({m.homeTeamName}) vs {m.awayPlayerName} ({m.awayTeamName})
                    </p>
                    <p className="text-xs text-gray-500">{new Date(m.date).toLocaleString("pt-BR")}</p>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="font-bold">
                      {m.homeScore} - {m.awayScore}
                    </span>
                    <button
                      onClick={() => deleteMatch.mutate(m.id)}
                      className="text-red-400 hover:text-red-300 text-xs"
                    >
                      Excluir
                    </button>
                  </div>
                </div>
              ))}
          </div>
        )}
      </Card>
    </div>
  );
}