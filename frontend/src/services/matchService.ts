import { api } from "./api";
import type { Match, MatchRequest } from "../models";

export const matchService = {
  getAll: () => api.get<Match[]>("/matches").then((r) => r.data),
  getById: (id: number) => api.get<Match>(`/matches/${id}`).then((r) => r.data),
  create: (data: MatchRequest) => api.post<Match>("/matches", data).then((r) => r.data),
  update: (id: number, data: MatchRequest) =>
    api.put<Match>(`/matches/${id}`, data).then((r) => r.data),
  delete: (id: number) => api.delete(`/matches/${id}`),
};