import { useDashboard } from "../hooks/useStatistics";
import { StatCard } from "../components/StatCard";
import { Card } from "../components/Card";

export function DashboardPage() {
  const { data, isLoading, error } = useDashboard();

  if (isLoading) return <p className="text-gray-400">Carregando...</p>;
  if (error || !data) return <p className="text-red-400">Erro ao carregar dashboard.</p>;

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        <StatCard label="Jogadores" value={data.totalPlayers} />
        <StatCard label="Times cadastrados" value={data.totalTeams} />
        <StatCard label="Partidas" value={data.totalMatches} />
      </div>

      <Card>
        <h2 className="font-semibold mb-3">🏆 Top Ranking</h2>
        <table className="w-full text-sm">
          <thead className="text-gray-400 text-left">
            <tr>
              <th className="pb-2">Jogador</th>
              <th className="pb-2">PJ</th>
              <th className="pb-2">V</th>
              <th className="pb-2">E</th>
              <th className="pb-2">D</th>
              <th className="pb-2">SG</th>
              <th className="pb-2">Pts</th>
              <th className="pb-2">Sequência</th>
            </tr>
          </thead>
          <tbody>
            {data.topRanking.map((p) => (
              <tr key={p.playerId} className="border-t border-gray-800">
                <td className="py-2 font-medium">{p.playerName}</td>
                <td>{p.matchesPlayed}</td>
                <td>{p.wins}</td>
                <td>{p.draws}</td>
                <td>{p.losses}</td>
                <td className={p.goalDifference >= 0 ? "text-emerald-400" : "text-red-400"}>
                  {p.goalDifference > 0 ? "+" : ""}
                  {p.goalDifference}
                </td>
                <td className="font-bold">{p.points}</td>
                <td className="text-gray-400">{p.currentStreak}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </Card>

      <Card>
        <h2 className="font-semibold mb-3">🕓 Últimas partidas</h2>
        <div className="space-y-2">
          {data.recentMatches.map((m) => (
            <div
              key={m.id}
              className="flex justify-between items-center border-t border-gray-800 pt-2 first:border-0 first:pt-0"
            >
              <span className="text-sm">
                {m.homePlayerName} ({m.homeTeamName}) vs {m.awayPlayerName} ({m.awayTeamName})
              </span>
              <span className="font-bold">
                {m.homeScore} - {m.awayScore}
              </span>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
}