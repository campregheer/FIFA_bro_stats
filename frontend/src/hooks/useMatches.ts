import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { matchService } from "../services/matchService";
import type { MatchRequest } from "../models";

export function useMatches() {
  return useQuery({ queryKey: ["matches"], queryFn: matchService.getAll });
}

export function useCreateMatch() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: MatchRequest) => matchService.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["matches"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
      queryClient.invalidateQueries({ queryKey: ["ranking"] });
    },
  });
}

export function useDeleteMatch() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => matchService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["matches"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
      queryClient.invalidateQueries({ queryKey: ["ranking"] });
    },
  });
}