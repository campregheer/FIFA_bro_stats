import { api } from "./api";
import type { Dashboard, HeadToHead, PlayerStats, TeamStats } from "../models";

export const statisticsService = {
  getDashboard: () => api.get<Dashboard>("/dashboard").then((r) => r.data),
  getGeneralRanking: () => api.get<PlayerStats[]>("/statistics/general").then((r) => r.data),
  getPlayerStats: (id: number) =>
    api.get<PlayerStats>(`/statistics/player/${id}`).then((r) => r.data),
  getTeamStats: (id: number) =>
    api.get<TeamStats>(`/statistics/team/${id}`).then((r) => r.data),
  getHeadToHead: (player1Id: number, player2Id: number) =>
    api
      .get<HeadToHead>("/statistics/head-to-head", { params: { player1Id, player2Id } })
      .then((r) => r.data),
};