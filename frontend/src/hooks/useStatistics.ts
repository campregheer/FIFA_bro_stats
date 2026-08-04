import { useQuery } from "@tanstack/react-query";
import { statisticsService } from "../services/statisticsService";

export function useDashboard() {
  return useQuery({ queryKey: ["dashboard"], queryFn: statisticsService.getDashboard });
}

export function useGeneralRanking() {
  return useQuery({ queryKey: ["ranking"], queryFn: statisticsService.getGeneralRanking });
}