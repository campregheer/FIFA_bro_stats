import { api } from "./api";

export type ChampionshipStatus = "DRAFT" | "GROUP_STAGE" | "KNOCKOUT_STAGE" | "FINISHED";
export type MatchFormat = "SINGLE_MATCH" | "TWO_LEGS_AGGREGATE" | "BEST_OF_3";
export interface ChampionshipParticipant { id: number; name: string }
export interface ChampionshipMatch { id: number; stage: "GROUP_STAGE" | "KNOCKOUT"; playerA: ChampionshipParticipant; playerB: ChampionshipParticipant; scoreA: number | null; scoreB: number | null; played: boolean; date: string | null }
export interface ChampionshipGroup { id: number; name: string; participants: ChampionshipParticipant[]; matches: ChampionshipMatch[] }
export interface Championship { id: number; name: string; status: ChampionshipStatus; matchFormat: MatchFormat; participants: ChampionshipParticipant[]; groups?: ChampionshipGroup[]; rounds?: ChampionshipRound[]; createdAt: string; startedAt: string | null; finishedAt: string | null }
export interface QualifiedPlayer { playerId: number; playerName: string; group: string; groupPosition: number; points: number; goalDifference: number; goalsFor: number }
export interface ChampionshipMatchup { id: number; position: number; playerA: ChampionshipParticipant; playerB: ChampionshipParticipant; winner: ChampionshipParticipant | null; status: "PENDING" | "COMPLETED" | "BYE"; matches: ChampionshipMatch[] }
export interface ChampionshipRound { id: number; name: string; orderIndex: number; matchups: ChampionshipMatchup[] }

export const championshipService = {
  getAll: () => api.get<Championship[]>("/api/championships").then((r) => r.data),
  getById: (id: number) => api.get<Championship>(`/api/championships/${id}`).then((r) => r.data),
  getDetails: async (id: number) => {
    const [details, summary] = await Promise.all([
      api.get<Championship>(`/api/championships/${id}/details`).then((r) => r.data),
      api.get<Championship>(`/api/championships/${id}`).then((r) => r.data),
    ]);
    return { ...details, participants: summary.participants };
  },
  create: (name: string, matchFormat: MatchFormat) => api.post<Championship>("/api/championships", { name, matchFormat }).then((r) => r.data),
  update: (id: number, name: string, matchFormat: MatchFormat) => api.put<Championship>(`/api/championships/${id}`, { name, matchFormat }).then((r) => r.data),
  delete: (id: number) => api.delete(`/api/championships/${id}`),
  addParticipant: (championshipId: number, playerId: number) => api.post<Championship>(`/api/championships/${championshipId}/participants/${playerId}`).then((r) => r.data),
  removeParticipant: (championshipId: number, playerId: number) => api.delete(`/api/championships/${championshipId}/participants/${playerId}`),
  start: (id: number) => api.post<Championship>(`/api/championships/${id}/start`).then((r) => r.data),
  generateKnockout: (id: number) => api.post<ChampionshipRound>(`/api/championships/${id}/knockout`).then((r) => r.data),
  registerResult: (matchId: number, scoreA: number, scoreB: number, penaltyScoreA?: number, penaltyScoreB?: number) => api.put(`/api/championship-matches/${matchId}/result`, { scoreA, scoreB, penaltyScoreA, penaltyScoreB }),
};
