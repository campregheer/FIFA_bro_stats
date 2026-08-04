import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { teamService } from "../services/teamService";
import type { TeamRequest } from "../models";

export function useTeams() {
  return useQuery({ queryKey: ["teams"], queryFn: teamService.getAll });
}

export function useCreateTeam() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: TeamRequest) => teamService.create(data),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["teams"] }),
  });
}

export function useUpdateTeam() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: TeamRequest }) => teamService.update(id, data),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["teams"] }),
  });
}

export function useDeleteTeam() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => teamService.delete(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["teams"] }),
  });
}