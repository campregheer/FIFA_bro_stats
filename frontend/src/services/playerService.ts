import { api } from "./api";
import type { Player, PlayerRequest } from "../models";

export const playerService = {
  getAll: () => api.get<Player[]>("/players").then((r) => r.data),
  getById: (id: number) => api.get<Player>(`/players/${id}`).then((r) => r.data),
  create: (data: PlayerRequest) => api.post<Player>("/players", data).then((r) => r.data),
  update: (id: number, data: PlayerRequest) =>
    api.put<Player>(`/players/${id}`, data).then((r) => r.data),
  delete: (id: number) => api.delete(`/players/${id}`),
};