import { useState } from "react";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts";
import { useGeneralRanking } from "../hooks/useStatistics";
import { usePlayers } from "../hooks/usePlayers";
import { statisticsService } from "../services/statisticsService";
import { useQuery } from "@tanstack/react-query";
import { Card } from "../components/Card";

export function StatisticsPage() {
  const { data: ranking, isLoading } = useGeneralRanking();
  const { data: players } = usePlayers();

  const [player1Id, setPlayer1Id] = useState("");
  const [player2Id, setPlayer2Id] = useState("");

  const { data: h2h } = useQuery({
    queryKey: ["h2h", player1Id, player2Id],
    queryFn: () => statisticsService.getHeadToHead(Number(player1Id), Number(player2Id)),
    enabled: !!player1Id && !!player2Id && player1Id !== player2Id,
  });

  const chartData = ranking?.map((p) => ({ name: p.playerName, Pontos: p.points, Saldo: p.goalDifference })) ?? [];

  return (
    <div className="space-y-6">
      <Card>
        <h2 className="font-semibold mb-3">Ranking geral</h2>
        {isLoading ? (
          <p className="text-gray-400 text-sm">Carregando...</p>
        ) : (
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
              <XAxis dataKey="name" stroke="#9ca3af" />
              <YAxis stroke="#9ca3af" />
              <Tooltip contentStyle={{ backgroundColor: "#1f2937", border: "none" }} />
              <Bar dataKey="Pontos" fill="#10b981" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        )}
      </Card>

      <Card>
        <h2 className="font-semibold mb-3">Confronto direto</h2>
        <div className="flex gap-3 mb-4">
          <select
            value={player1Id}
            onChange={(e) => setPlayer1Id(e.target.value)}
            className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm"
          >
            <option value="">Jogador 1</option>
            {players?.map((p) => (
              <option key={p.id} value={p.id}>
                {p.name}
              </option>
            ))}
          </select>
          <select
            value={player2Id}
            onChange={(e) => setPlayer2Id(e.target.value)}
            className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm"
          >
            <option value="">Jogador 2</option>
            {players?.map((p) => (
              <option key={p.id} value={p.id}>
                {p.name}
              </option>
            ))}
          </select>
        </div>

        {h2h && (
          <div className="grid grid-cols-2 gap-4 text-sm">
            {[h2h.player1Stats, h2h.player2Stats].map((s) => (
              <div key={s.playerId} className="bg-gray-800 rounded-lg p-3">
                <p className="font-semibold mb-1">{s.playerName}</p>
                <p>
                  {s.wins}V {s.draws}E {s.losses}D
                </p>
                <p className="text-gray-400">Saldo: {s.goalDifference}</p>
              </div>
            ))}
            <p className="col-span-2 text-gray-400 text-xs">Total de confrontos: {h2h.totalMatches}</p>
          </div>
        )}
      </Card>
    </div>
  );
}