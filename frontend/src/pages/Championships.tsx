import { useEffect, useMemo, useState } from "react";
import { Card } from "../components/Card";
import Swal from "sweetalert2";
import { playerService } from "../services/playerService";
import { championshipService } from "../services/championshipService";
import type { Player } from "../models";
import type { Championship, ChampionshipGroup, ChampionshipMatch, ChampionshipRound } from "../services/championshipService";

function errorMessage(error: unknown) {
  if (typeof error === "object" && error && "response" in error) return ((error as { response?: { data?: { message?: string } } }).response?.data?.message) ?? "Não foi possível concluir a operação.";
  return "Não foi possível concluir a operação.";
}

function statusLabel(status: Championship["status"]) {
  return ({ DRAFT: "Rascunho", GROUP_STAGE: "Fase de grupos", KNOCKOUT_STAGE: "Mata-mata", FINISHED: "Finalizado" })[status];
}

const toast = Swal.mixin({
  toast: true,
  position: "top-end",
  showConfirmButton: false,
  timer: 2800,
  timerProgressBar: true,
  background: "#111827",
  color: "#f3f4f6",
});

export function ChampionshipsPage() {
  const [players, setPlayers] = useState<Player[]>([]);
  const [championships, setChampionships] = useState<Championship[]>([]);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [selected, setSelected] = useState<Championship | null>(null);
  const [showDetailsPage, setShowDetailsPage] = useState(false);
  const [showChampionPage, setShowChampionPage] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [editName, setEditName] = useState("");
  const [editPlayers, setEditPlayers] = useState<number[]>([]);
  const [name, setName] = useState("");
  const [selectedPlayers, setSelectedPlayers] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [busyLabel, setBusyLabel] = useState("");
  const [message, setMessage] = useState("");
  const [modalMatch, setModalMatch] = useState<ChampionshipMatch | null>(null);
  const [scoreA, setScoreA] = useState("");
  const [scoreB, setScoreB] = useState("");
  const [penaltyA, setPenaltyA] = useState("");
  const [penaltyB, setPenaltyB] = useState("");

  async function selectChampionship(id: number) {
    setSelectedId(id); setShowDetailsPage(true); setLoading(true);
    try { setSelected(await championshipService.getDetails(id)); }
    catch (error) { setMessage(errorMessage(error)); }
    finally { setLoading(false); }
  }
  async function refreshList(idToSelect = selectedId) {
    setChampionships(await championshipService.getAll());
    if (idToSelect) setSelected(await championshipService.getDetails(idToSelect));
  }
  useEffect(() => {
    Promise.all([playerService.getAll(), championshipService.getAll()])
      .then(([availablePlayers, availableChampionships]) => { setPlayers(availablePlayers); setChampionships(availableChampionships); })
      .catch((error: unknown) => setMessage(errorMessage(error)))
      .finally(() => setLoading(false));
  }, []);
  function togglePlayer(id: number) { setSelectedPlayers((current) => current.includes(id) ? current.filter((item) => item !== id) : [...current, id]); }

  async function createChampionship(event: React.FormEvent) {
    event.preventDefault();
    if (selectedPlayers.length < 4) { setMessage("Selecione pelo menos 4 jogadores."); return; }
    setBusyLabel("Criando campeonato e adicionando participantes…"); setMessage("");
    try {
      const created = await championshipService.create(name, "SINGLE_MATCH");
      for (const playerId of selectedPlayers) await championshipService.addParticipant(created.id, playerId);
      setName(""); setSelectedPlayers([]); await refreshList(created.id); setSelectedId(created.id); setShowDetailsPage(true); setMessage("");
      void toast.fire({ icon: "success", title: "Campeonato criado" });
    } catch (error) { setMessage(errorMessage(error)); } finally { setBusyLabel(""); }
  }
  function beginEditing() {
    if (!selected) return;
    setEditName(selected.name);
    setEditPlayers(selected.participants.map((player) => player.id));
    setEditMode(true);
  }
  async function saveChampionship(event: React.FormEvent) {
    event.preventDefault();
    if (!selected) return;
    if (editPlayers.length < 4) { setMessage("O campeonato precisa ter pelo menos 4 participantes."); return; }
    setBusyLabel("Salvando alterações…"); setMessage("");
    try {
      await championshipService.update(selected.id, editName, "SINGLE_MATCH");
      const oldIds = new Set(selected.participants.map((player) => player.id));
      const newIds = new Set(editPlayers);
      for (const id of editPlayers.filter((playerId) => !oldIds.has(playerId))) await championshipService.addParticipant(selected.id, id);
      for (const id of [...oldIds].filter((playerId) => !newIds.has(playerId))) await championshipService.removeParticipant(selected.id, id);
      await refreshList(selected.id); setEditMode(false); setMessage("");
      void toast.fire({ icon: "success", title: "Campeonato atualizado" });
    } catch (error) { setMessage(errorMessage(error)); } finally { setBusyLabel(""); }
  }
  async function deleteChampionship(championship: Championship) {
    const confirmation = await Swal.fire({
      icon: "warning",
      title: "Excluir campeonato?",
      text: `"${championship.name}" e todas as partidas dele serão removidos.`,
      background: "#111827",
      color: "#f3f4f6",
      showCancelButton: true,
      confirmButtonText: "Sim, excluir",
      cancelButtonText: "Cancelar",
      confirmButtonColor: "#dc2626",
      cancelButtonColor: "#374151",
      reverseButtons: true,
      focusCancel: true,
    });
    if (!confirmation.isConfirmed) return;
    setBusyLabel("Excluindo campeonato…"); setMessage("");
    try {
      await championshipService.delete(championship.id);
      setChampionships((current) => current.filter((item) => item.id !== championship.id));
      if (selectedId === championship.id) { setSelected(null); setSelectedId(null); setShowDetailsPage(false); setShowChampionPage(false); }
      setMessage("");
      void toast.fire({ icon: "success", title: "Campeonato excluído" });
    } catch (error) { setMessage(errorMessage(error)); } finally { setBusyLabel(""); }
  }
  async function startChampionship() {
    if (!selected) return; setBusyLabel("Sorteando grupos e preparando partidas…"); setMessage("");
    try { await championshipService.start(selected.id); await refreshList(selected.id); }
    catch (error) { setMessage(errorMessage(error)); } finally { setBusyLabel(""); }
  }
  async function generateKnockout() {
    if (!selected) return; setBusyLabel("Calculando classificados e montando o mata-mata…"); setMessage("");
    try { await championshipService.generateKnockout(selected.id); await refreshList(selected.id); setMessage(""); void toast.fire({ icon: "success", title: "Mata-mata gerado" }); }
    catch (error) { setMessage(errorMessage(error)); } finally { setBusyLabel(""); }
  }
  function openResult(match: ChampionshipMatch) {
    setModalMatch(match); setScoreA(match.played ? String(match.scoreA) : ""); setScoreB(match.played ? String(match.scoreB) : ""); setPenaltyA(""); setPenaltyB("");
  }
  async function submitResult(event: React.FormEvent) {
    event.preventDefault(); if (!modalMatch || !selected) return;
    const a = Number(scoreA); const b = Number(scoreB);
    if (!Number.isInteger(a) || !Number.isInteger(b) || a < 0 || b < 0) { setMessage("Informe gols válidos para os dois jogadores."); return; }
    const draw = modalMatch.stage === "KNOCKOUT" && a === b;
    if (draw && (!penaltyA || !penaltyB || Number(penaltyA) === Number(penaltyB))) { setMessage("No mata-mata, empate precisa ser decidido nos pênaltis."); return; }
    setBusyLabel("Salvando resultado…"); setMessage("");
    try { await championshipService.registerResult(modalMatch.id, a, b, draw ? Number(penaltyA) : undefined, draw ? Number(penaltyB) : undefined); setModalMatch(null); await refreshList(selected.id); }
    catch (error) { setMessage(errorMessage(error)); } finally { setBusyLabel(""); }
  }

  return <div className="space-y-6">
    <div><p className="text-emerald-400 text-sm font-semibold uppercase tracking-widest">Área independente</p><h2 className="text-2xl font-bold mt-1">Campeonatos</h2><p className="text-gray-400 text-sm mt-1">Organize grupos, partidas e mata-mata sem misturar com o histórico geral.</p></div>
    {message && <div className="rounded-lg border border-emerald-800 bg-emerald-950/40 px-4 py-3 text-sm text-emerald-200">{message}</div>}
    {loading && <LoadingOverlay label="Carregando campeonatos…" />}
    {showChampionPage && selected ? <ChampionPage championship={selected} onBack={() => setShowChampionPage(false)} /> : showDetailsPage && selected ? <div className="space-y-5"><button onClick={() => { setShowDetailsPage(false); setEditMode(false); setMessage(""); }} className="inline-flex items-center gap-2 text-sm text-gray-400 hover:text-white"><span aria-hidden="true">←</span> Voltar aos campeonatos</button>{editMode && selected.status === "DRAFT" ? <EditChampionshipForm players={players} name={editName} selectedPlayers={editPlayers} busy={Boolean(busyLabel)} onName={setEditName} onPlayers={setEditPlayers} onCancel={() => setEditMode(false)} onSubmit={(event) => void saveChampionship(event)} /> : <ChampionshipDetails championship={selected} disabled={Boolean(busyLabel)} onStart={() => void startChampionship()} onKnockout={() => void generateKnockout()} onResult={openResult} onEdit={beginEditing} onDelete={() => void deleteChampionship(selected)} onShowChampion={() => setShowChampionPage(true)} />}</div> : <div className="grid xl:grid-cols-[340px_1fr] gap-6">
      <Card className="h-fit"><h3 className="font-semibold mb-4">Novo campeonato</h3><form onSubmit={createChampionship} className="space-y-4"><input value={name} onChange={(event) => setName(event.target.value)} required placeholder="Nome do campeonato" className="w-full bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm" /><div><p className="text-sm text-gray-400 mb-2">Participantes ({selectedPlayers.length})</p><div className="max-h-56 overflow-auto space-y-1">{players.map((player) => <label key={player.id} className="flex items-center gap-2 text-sm py-1"><input type="checkbox" checked={selectedPlayers.includes(player.id)} onChange={() => togglePlayer(player.id)} className="accent-emerald-500" />{player.name}</label>)}</div></div><button disabled={Boolean(busyLabel) || loading} className="w-full bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 rounded-lg py-2 text-sm font-medium">Criar campeonato</button></form></Card>
      <Card><h3 className="font-semibold mb-3">Meus campeonatos</h3><div className="grid md:grid-cols-2 gap-3">{championships.map((item) => <div key={item.id} className="rounded-xl border border-gray-700 bg-gray-800/60 p-4 hover:border-emerald-500 transition-colors"><button disabled={Boolean(busyLabel)} onClick={() => void selectChampionship(item.id)} className="w-full text-left"><span className="block font-medium">{item.name}</span><span className="text-xs text-gray-400 mt-1 block">{statusLabel(item.status)} · {item.participants?.length ?? 0} participantes</span><span className="mt-3 block text-xs text-emerald-400">Abrir campeonato →</span></button><button onClick={() => void deleteChampionship(item)} disabled={Boolean(busyLabel)} className="mt-3 text-xs text-red-400 hover:text-red-300">Excluir campeonato</button></div>)}{!loading && championships.length === 0 && <p className="text-sm text-gray-500">Nenhum campeonato criado.</p>}</div></Card>
    </div>}
    {busyLabel && <LoadingOverlay label={busyLabel} />}
    {modalMatch && <ScoreModal match={modalMatch} scoreA={scoreA} scoreB={scoreB} penaltyA={penaltyA} penaltyB={penaltyB} onScoreA={setScoreA} onScoreB={setScoreB} onPenaltyA={setPenaltyA} onPenaltyB={setPenaltyB} onClose={() => setModalMatch(null)} onSubmit={(event) => void submitResult(event)} busy={Boolean(busyLabel)} />}
  </div>;
}

function ChampionshipDetails({ championship, disabled, onStart, onKnockout, onResult, onEdit, onDelete, onShowChampion }: { championship: Championship; disabled: boolean; onStart: () => void; onKnockout: () => void; onResult: (match: ChampionshipMatch) => void; onEdit: () => void; onDelete: () => void; onShowChampion: () => void }) {
  return <div className="space-y-5">
    <Card><div className="flex flex-wrap items-start justify-between gap-3"><div><p className="text-xs uppercase tracking-widest text-emerald-400">{statusLabel(championship.status)}</p><h3 className="text-2xl font-bold mt-1">{championship.name}</h3><p className="text-sm text-gray-400 mt-1">{championship.participants?.length ?? 0} participantes · {formatLabel(championship.matchFormat)}</p></div><div className="flex flex-wrap gap-2">{championship.status === "DRAFT" && <><button onClick={onEdit} disabled={disabled} className="rounded-lg border border-gray-600 px-3 py-2 text-sm hover:border-gray-400">Editar</button><button onClick={onStart} disabled={disabled} className="bg-blue-600 hover:bg-blue-700 disabled:opacity-50 rounded-lg px-3 py-2 text-sm">Sortear grupos</button></>}{championship.status === "GROUP_STAGE" && <button onClick={onKnockout} disabled={disabled} className="bg-purple-600 hover:bg-purple-700 disabled:opacity-50 rounded-lg px-3 py-2 text-sm">Gerar mata-mata</button>}<button onClick={onDelete} disabled={disabled} className="rounded-lg border border-red-900/80 px-3 py-2 text-sm text-red-300 hover:bg-red-950/50">Excluir</button></div></div>{championship.status === "DRAFT" && <p className="mt-4 text-sm text-gray-400">Você pode ajustar nome, formato e participantes enquanto o campeonato estiver em rascunho.</p>}</Card>
    {championship.rounds?.length ? <Bracket rounds={championship.rounds} onResult={onResult} onShowChampion={onShowChampion} /> : null}
    {championship.groups?.length ? <ChampionshipSummary championship={championship} /> : null}
    {championship.groups?.length ? <div className="space-y-4"><div><h4 className="text-lg font-semibold">Fase de grupos</h4><p className="text-sm text-gray-500">Os jogos foram organizados para alternar os participantes sempre que possível.</p></div>{championship.groups.map((group) => <GroupBlock key={group.id} group={group} onResult={onResult} />)}</div> : null}
    {championship.status === "KNOCKOUT_STAGE" && !championship.rounds?.length && <Card><p className="text-sm text-gray-400">A chave ainda não foi carregada.</p></Card>}
  </div>;
}

function GroupBlock({ group, onResult }: { group: ChampionshipGroup; onResult: (match: ChampionshipMatch) => void }) {
  const orderedMatches = useMemo(() => arrangeMatches(group.matches), [group.matches]);
  const playedCount = group.matches.filter((match) => match.played).length;
  return <Card><div className="flex flex-wrap items-center justify-between gap-2"><div><h4 className="font-semibold text-lg">Grupo {group.name}</h4><p className="text-xs text-gray-500">{group.participants.length} jogadores · {playedCount}/{group.matches.length} jogos concluídos</p></div><span className="rounded-full bg-gray-800 px-3 py-1 text-xs text-gray-300">{group.matches.length - playedCount} pendentes</span></div><div className="mt-4 divide-y divide-gray-800">{orderedMatches.map((match, index) => <MatchCard key={match.id} match={match} order={index + 1} onResult={onResult} />)}</div></Card>;
}

function ChampionshipSummary({ championship }: { championship: Championship }) {
  const matches = [
    ...(championship.groups ?? []).flatMap((group) => group.matches),
    ...(championship.rounds ?? []).flatMap((round) => round.matchups.flatMap((matchup) => matchup.matches)),
  ].filter((match) => match.played && match.scoreA !== null && match.scoreB !== null);
  const goals = new Map<number, { name: string; goals: number }>();
  for (const match of matches) {
    for (const [player, count] of [[match.playerA, match.scoreA!], [match.playerB, match.scoreB!]] as const) {
      const current = goals.get(player.id) ?? { name: player.name, goals: 0 };
      current.goals += count;
      goals.set(player.id, current);
    }
  }
  const topScorer = [...goals.values()].sort((a, b) => b.goals - a.goals || a.name.localeCompare(b.name))[0];
  const highestScoringMatch = [...matches].sort((a, b) => (b.scoreA! + b.scoreB!) - (a.scoreA! + a.scoreB!))[0];
  const totalGoals = matches.reduce((sum, match) => sum + match.scoreA! + match.scoreB!, 0);

  return <section className="space-y-3"><div><h4 className="text-lg font-semibold">Resumo do campeonato</h4><p className="text-sm text-gray-500">Estatísticas das partidas já disputadas.</p></div><div className="grid sm:grid-cols-2 xl:grid-cols-4 gap-3">
    <SummaryStat label="Artilheiro" value={topScorer ? topScorer.name : "—"} detail={topScorer ? `${topScorer.goals} ${topScorer.goals === 1 ? "gol" : "gols"}` : "Sem gols registrados"} accent="text-amber-300" />
    <SummaryStat label="Jogo com mais gols" value={highestScoringMatch ? `${highestScoringMatch.scoreA} × ${highestScoringMatch.scoreB}` : "—"} detail={highestScoringMatch ? `${highestScoringMatch.playerA.name} × ${highestScoringMatch.playerB.name} · ${highestScoringMatch.scoreA! + highestScoringMatch.scoreB!} gols` : "Nenhuma partida concluída"} accent="text-sky-300" />
    <SummaryStat label="Gols marcados" value={String(totalGoals)} detail={`${matches.length} ${matches.length === 1 ? "partida concluída" : "partidas concluídas"}`} accent="text-emerald-300" />
    <SummaryStat label="Média de gols" value={matches.length ? (totalGoals / matches.length).toFixed(1) : "—"} detail="gols por partida" accent="text-violet-300" />
  </div></section>;
}

function SummaryStat({ label, value, detail, accent }: { label: string; value: string; detail: string; accent: string }) {
  return <Card className="min-w-0"><p className="text-xs uppercase tracking-wider text-gray-500">{label}</p><p className={`mt-2 truncate text-xl font-bold ${accent}`} title={value}>{value}</p><p className="mt-1 truncate text-xs text-gray-400" title={detail}>{detail}</p></Card>;
}

function EditChampionshipForm({ players, name, selectedPlayers, busy, onName, onPlayers, onCancel, onSubmit }: {
  players: Player[]; name: string; selectedPlayers: number[]; busy: boolean;
  onName: (value: string) => void;
  onPlayers: (value: number[] | ((current: number[]) => number[])) => void; onCancel: () => void; onSubmit: (event: React.FormEvent) => void;
}) {
  return <Card><div className="mb-4"><p className="text-xs uppercase tracking-widest text-emerald-400">Rascunho</p><h3 className="mt-1 text-xl font-semibold">Editar campeonato</h3><p className="mt-1 text-sm text-gray-400">Essas opções ficam bloqueadas assim que o sorteio começar.</p></div><form onSubmit={onSubmit} className="space-y-5">
    <label className="block text-sm text-gray-400">Nome<input required maxLength={100} value={name} onChange={(event) => onName(event.target.value)} className="mt-1 w-full rounded-lg border border-gray-700 bg-gray-800 px-3 py-2 text-white" /></label>
    <div><p className="mb-2 text-sm text-gray-400">Participantes · {selectedPlayers.length} selecionados</p><div className="grid max-h-72 gap-x-4 overflow-auto sm:grid-cols-2">{players.map((player) => <label key={player.id} className="flex items-center gap-2 border-b border-gray-800 py-2 text-sm"><input type="checkbox" checked={selectedPlayers.includes(player.id)} onChange={() => onPlayers((current) => current.includes(player.id) ? current.filter((id) => id !== player.id) : [...current, player.id])} className="accent-emerald-500" />{player.name}</label>)}</div></div>
    <div className="flex justify-end gap-2"><button type="button" onClick={onCancel} className="rounded-lg border border-gray-700 px-4 py-2 text-sm text-gray-300">Cancelar</button><button disabled={busy} className="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-semibold disabled:opacity-50">Salvar alterações</button></div>
  </form></Card>;
}

function ChampionPage({ championship, onBack }: { championship: Championship; onBack: () => void }) {
  const finalMatchup = championship.rounds?.at(-1)?.matchups[0];
  const champion = finalMatchup?.winner;
  const allMatches = [
    ...(championship.groups ?? []).flatMap((group) => group.matches),
    ...(championship.rounds ?? []).flatMap((round) => round.matchups.flatMap((matchup) => matchup.matches)),
  ].filter((match) => match.played && match.scoreA !== null && match.scoreB !== null);
  const knockoutWinners = new Map<number, number>();
  for (const round of championship.rounds ?? []) for (const matchup of round.matchups) {
    for (const match of matchup.matches) if (matchup.winner) knockoutWinners.set(match.id, matchup.winner.id);
  }
  const championMatches = champion ? allMatches.filter((match) => match.playerA.id === champion.id || match.playerB.id === champion.id) : [];
  const wins = champion ? championMatches.filter((match) => {
    const knockoutWinner = knockoutWinners.get(match.id);
    return knockoutWinner !== undefined ? knockoutWinner === champion.id : match.playerA.id === champion.id ? match.scoreA! > match.scoreB! : match.scoreB! > match.scoreA!;
  }).length : 0;
  const goalsFor = championMatches.reduce((sum, match) => sum + (match.playerA.id === champion?.id ? match.scoreA! : match.scoreB!), 0);
  const goalsAgainst = championMatches.reduce((sum, match) => sum + (match.playerA.id === champion?.id ? match.scoreB! : match.scoreA!), 0);

  if (!champion) return <div className="space-y-5"><button onClick={onBack} className="text-sm text-gray-400 hover:text-white">← Voltar ao campeonato</button><Card><p className="text-gray-300">O campeão ainda não está disponível. Registre o resultado da final primeiro.</p></Card></div>;
  return <div className="space-y-5"><button onClick={onBack} className="inline-flex items-center gap-2 text-sm text-gray-400 hover:text-white">← Voltar ao campeonato</button>
    <div className="relative overflow-hidden rounded-3xl border border-amber-400/30 bg-gradient-to-br from-amber-950 via-slate-900 to-emerald-950 px-6 py-12 text-center shadow-2xl sm:px-12"><div aria-hidden="true" className="absolute inset-0 bg-[radial-gradient(circle_at_50%_0%,rgba(251,191,36,0.24),transparent_55%)]" /><div className="relative mx-auto max-w-2xl"><span className="inline-flex h-24 w-24 items-center justify-center rounded-full border border-amber-300/50 bg-amber-400/10 text-5xl shadow-[0_0_60px_rgba(251,191,36,0.2)]">🏆</span><p className="mt-6 text-xs font-bold uppercase tracking-[0.35em] text-amber-300">Campeão</p><h2 className="mt-2 text-4xl font-black tracking-tight text-white sm:text-6xl">{champion.name}</h2><p className="mt-3 text-gray-300">{championship.name} · título conquistado na final</p><div className="mx-auto mt-6 h-px max-w-xs bg-gradient-to-r from-transparent via-amber-300/60 to-transparent" /><p className="mt-5 text-sm text-amber-100/80">Uma campanha de decisões, gols e grandes partidas.</p></div></div>
    <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4"><SummaryStat label="Partidas disputadas" value={String(championMatches.length)} detail="fase de grupos e mata-mata" accent="text-sky-300" /><SummaryStat label="Vitórias" value={String(wins)} detail="inclui classificação nos pênaltis" accent="text-emerald-300" /><SummaryStat label="Gols marcados" value={String(goalsFor)} detail="durante o campeonato" accent="text-amber-300" /><SummaryStat label="Saldo de gols" value={`${goalsFor - goalsAgainst > 0 ? "+" : ""}${goalsFor - goalsAgainst}`} detail={`${goalsFor} feitos · ${goalsAgainst} sofridos`} accent="text-violet-300" /></div>
    <Card><div className="flex items-center justify-between gap-3"><div><p className="text-xs uppercase tracking-widest text-gray-500">A decisão</p><h3 className="mt-1 text-lg font-semibold">Final do campeonato</h3></div><span className="rounded-full bg-amber-950 px-3 py-1 text-xs font-semibold text-amber-200">CAMPEÃO</span></div><div className="mt-4 flex flex-wrap items-center justify-center gap-4 rounded-xl bg-gray-800/50 px-4 py-6 text-center"><span className={finalMatchup?.playerA.id === champion.id ? "font-bold text-amber-200" : "text-gray-300"}>{finalMatchup?.playerA.name}</span><strong className="rounded-lg bg-gray-950 px-4 py-2 text-2xl">{finalMatchup?.matches[0]?.scoreA} – {finalMatchup?.matches[0]?.scoreB}</strong><span className={finalMatchup?.playerB.id === champion.id ? "font-bold text-amber-200" : "text-gray-300"}>{finalMatchup?.playerB.name}</span></div></Card>
  </div>;
}

function arrangeMatches(matches: ChampionshipMatch[]) {
  const remaining = [...matches].sort(() => Math.random() - 0.5);
  const ordered: ChampionshipMatch[] = [];
  while (remaining.length) {
    const recentPlayers = new Set(ordered.slice(-2).flatMap((match) => [match.playerA.id, match.playerB.id]));
    const safeIndexes = remaining.map((match, index) => ({ match, index }))
      .filter(({ match }) => !recentPlayers.has(match.playerA.id) && !recentPlayers.has(match.playerB.id));
    const choices = safeIndexes.length ? safeIndexes : remaining.map((match, index) => ({ match, index }));
    const chosen = choices[Math.floor(Math.random() * choices.length)];
    ordered.push(chosen.match);
    remaining.splice(chosen.index, 1);
  }
  return ordered;
}

function formatLabel(format: Championship["matchFormat"]) {
  return ({ SINGLE_MATCH: "Partida única", TWO_LEGS_AGGREGATE: "Ida e volta", BEST_OF_3: "Melhor de 3" })[format];
}

function Bracket({ rounds, onResult, onShowChampion }: { rounds: ChampionshipRound[]; onResult: (match: ChampionshipMatch) => void; onShowChampion: () => void }) {
  const initialMatchCount = rounds[0]?.matchups.length ?? 0;
  const rowCount = Math.max(1, initialMatchCount * 2 - 1);
  return <Card className="overflow-hidden"><div className="flex items-center justify-between gap-4 mb-5"><div><p className="text-xs uppercase tracking-widest text-purple-300">Fase eliminatória</p><h4 className="text-lg font-semibold mt-1">Chaveamento</h4><p className="text-xs text-gray-500 mt-1">Os vencedores avançam pela trilha até a final.</p></div><span className="rounded-full bg-purple-950 px-3 py-1 text-xs text-purple-200">{rounds.length} {rounds.length === 1 ? "rodada" : "rodadas"}</span></div>
    <div className="overflow-x-auto pb-4"><div className="flex min-w-max gap-16 px-3">
      {rounds.map((round, roundIndex) => <div key={round.id} className="w-[260px] shrink-0">
        <h5 className="mb-2 h-7 text-sm font-semibold text-purple-200">{round.name}</h5>
        <div className="relative grid" style={{ gridTemplateRows: `repeat(${rowCount}, 112px)` }}>
        {round.matchups.map((matchup, matchupIndex) => {
          const rowStart = (2 ** roundIndex) + matchupIndex * (2 ** (roundIndex + 1));
          const match = matchup.matches[0];
          const lastMatchInRound = roundIndex === rounds.length - 1;
          return <div key={matchup.id} className="relative self-center pr-8" style={{ gridColumn: 1, gridRow: `${rowStart} / span 1` }}>
            <div className="relative z-[1] rounded-xl border border-gray-700 bg-gray-900 shadow-lg">
              <div className={`flex items-center justify-between gap-2 border-b border-gray-800 px-3 py-2 text-sm ${matchup.winner?.id === matchup.playerA?.id ? "text-emerald-300" : "text-gray-200"}`}><span className="truncate">{matchup.playerA?.name ?? "A definir"}</span><strong>{match?.played ? match.scoreA : "–"}</strong></div>
              <div className={`flex items-center justify-between gap-2 px-3 py-2 text-sm ${matchup.winner?.id === matchup.playerB?.id ? "text-emerald-300" : "text-gray-200"}`}><span className="truncate">{matchup.playerB?.name ?? "A definir"}</span><strong>{match?.played ? match.scoreB : "–"}</strong></div>
              {match && <div className="border-t border-gray-800 px-3 py-2">{match.played && lastMatchInRound && matchup.winner ? <button onClick={onShowChampion} className="text-xs font-semibold text-amber-300 hover:text-amber-200">🏆 Ver tela do campeão</button> : match.played ? <span className="text-xs text-gray-500">Confronto concluído</span> : <button onClick={() => onResult(match)} className="text-xs font-medium text-emerald-400 hover:text-emerald-300">Lançar resultado</button>}</div>}
            </div>
            {roundIndex > 0 && <span className="absolute -left-16 top-1/2 z-0 h-px w-16 bg-purple-500/70" />}
            {!lastMatchInRound && <><span className="absolute right-0 top-1/2 z-0 h-px w-8 bg-purple-500/70" />{matchupIndex % 2 === 0 ? <span className="absolute right-0 top-1/2 z-0 h-28 w-px bg-purple-500/70" /> : <span className="absolute right-0 bottom-1/2 z-0 h-28 w-px bg-purple-500/70" />}</>}
          </div>;
        })}
        </div>
      </div>)}
    </div></div>
  </Card>;
}

function MatchCard({ match, order, onResult }: { match: ChampionshipMatch; order: number; onResult: (match: ChampionshipMatch) => void }) {
  return <div className="flex items-center gap-3 py-3"><span className="w-7 shrink-0 text-xs text-gray-600">{String(order).padStart(2, "0")}</span><div className="flex min-w-0 flex-1 items-center justify-between gap-3 rounded-lg bg-gray-800/60 px-3 py-3 text-sm"><span className="min-w-0 flex-1 truncate text-right">{match.playerA.name}</span><strong className="min-w-14 rounded-md bg-gray-950 px-2 py-1 text-center text-gray-200">{match.played ? `${match.scoreA} – ${match.scoreB}` : "vs"}</strong><span className="min-w-0 flex-1 truncate">{match.playerB.name}</span>{match.played ? <span className="text-xs text-gray-500">Concluído</span> : <button onClick={() => onResult(match)} className="text-emerald-400 text-xs shrink-0 hover:text-emerald-300">Lançar placar</button>}</div></div>;
}

function ScoreModal({ match, scoreA, scoreB, penaltyA, penaltyB, onScoreA, onScoreB, onPenaltyA, onPenaltyB, onClose, onSubmit, busy }: { match: ChampionshipMatch; scoreA: string; scoreB: string; penaltyA: string; penaltyB: string; onScoreA: (value: string) => void; onScoreB: (value: string) => void; onPenaltyA: (value: string) => void; onPenaltyB: (value: string) => void; onClose: () => void; onSubmit: (event: React.FormEvent) => void; busy: boolean }) {
  const penalties = match.stage === "KNOCKOUT" && scoreA !== "" && scoreB !== "" && Number(scoreA) === Number(scoreB);
  return <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4" onMouseDown={(event) => { if (event.target === event.currentTarget) onClose(); }}><div role="dialog" aria-modal="true" aria-labelledby="score-modal-title" className="w-full max-w-lg rounded-2xl border border-gray-700 bg-gray-900 p-6 shadow-2xl"><div className="flex justify-between items-start"><div><p className="text-xs uppercase tracking-widest text-emerald-400">{match.stage === "KNOCKOUT" ? "Mata-mata" : "Fase de grupos"}</p><h3 id="score-modal-title" className="text-xl font-bold mt-1">Registrar placar</h3></div><button type="button" onClick={onClose} aria-label="Fechar" className="text-gray-400 hover:text-white text-xl">×</button></div><form onSubmit={onSubmit} className="mt-6"><div className="grid grid-cols-[1fr_auto_1fr] items-center gap-4"><label className="text-center"><span className="block truncate text-sm text-gray-300 mb-2">{match.playerA.name}</span><input autoFocus required type="number" min="0" value={scoreA} onChange={(event) => onScoreA(event.target.value)} className="w-full rounded-xl border border-gray-700 bg-gray-800 px-3 py-4 text-center text-3xl font-bold focus:border-emerald-500 focus:outline-none" /></label><span className="mt-7 text-gray-500 font-bold">–</span><label className="text-center"><span className="block truncate text-sm text-gray-300 mb-2">{match.playerB.name}</span><input required type="number" min="0" value={scoreB} onChange={(event) => onScoreB(event.target.value)} className="w-full rounded-xl border border-gray-700 bg-gray-800 px-3 py-4 text-center text-3xl font-bold focus:border-emerald-500 focus:outline-none" /></label></div>{penalties && <div className="mt-5 rounded-xl border border-amber-700/50 bg-amber-950/30 p-4"><p className="text-sm font-medium text-amber-200">Empate no mata-mata: defina os pênaltis</p><div className="grid grid-cols-2 gap-3 mt-3"><label className="text-xs text-gray-400">{match.playerA.name}<input required type="number" min="0" value={penaltyA} onChange={(event) => onPenaltyA(event.target.value)} className="mt-1 w-full rounded-lg border border-gray-700 bg-gray-800 px-3 py-2 text-center text-lg text-white" /></label><label className="text-xs text-gray-400">{match.playerB.name}<input required type="number" min="0" value={penaltyB} onChange={(event) => onPenaltyB(event.target.value)} className="mt-1 w-full rounded-lg border border-gray-700 bg-gray-800 px-3 py-2 text-center text-lg text-white" /></label></div></div>}<div className="flex justify-end gap-2 mt-6"><button type="button" onClick={onClose} className="rounded-lg border border-gray-700 px-4 py-2 text-sm text-gray-300">Cancelar</button><button disabled={busy} className="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-semibold hover:bg-emerald-500 disabled:opacity-50">Salvar resultado</button></div></form></div></div>;
}

function LoadingOverlay({ label }: { label: string }) {
  return <div className="fixed inset-0 z-[60] flex items-center justify-center bg-slate-950/75 backdrop-blur-sm"><div className="flex items-center gap-4 rounded-2xl border border-gray-700 bg-gray-900 px-6 py-5 shadow-2xl"><span className="h-8 w-8 animate-spin rounded-full border-2 border-gray-600 border-t-emerald-400" /><span className="text-sm text-gray-200">{label}</span></div></div>;
}
