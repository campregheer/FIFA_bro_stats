import { api } from "./api";
import type { Team, TeamRequest } from "../models";

export const teamService = {
  getAll: () => api.get<Team[]>("/teams").then((r) => r.data),
  getById: (id: number) => api.get<Team>(`/teams/${id}`).then((r) => r.data),
  create: (data: TeamRequest) => api.post<Team>("/teams", data).then((r) => r.data),
  update: (id: number, data: TeamRequest) =>
    api.put<Team>(`/teams/${id}`, data).then((r) => r.data),
  delete: (id: number) => api.delete(`/teams/${id}`),
};