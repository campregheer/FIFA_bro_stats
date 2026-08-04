import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { playerService } from "../services/playerService";
import type { PlayerRequest } from "../models";

export function usePlayers() {
  return useQuery({ queryKey: ["players"], queryFn: playerService.getAll });
}

export function useCreatePlayer() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: PlayerRequest) => playerService.create(data),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["players"] }),
  });
}

export function useUpdatePlayer() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: PlayerRequest }) =>
      playerService.update(id, data),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["players"] }),
  });
}

export function useDeletePlayer() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => playerService.delete(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["players"] }),
  });
}