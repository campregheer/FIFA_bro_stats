export interface Player {
  id: number;
  name: string;
  nickname: string | null;
  createdAt: string;
}

export interface PlayerRequest {
  name: string;
  nickname?: string;
}

export interface Team {
  id: number;
  name: string;
  country: string | null;
  logoUrl: string | null;
  createdAt: string;
}

export interface TeamRequest {
  name: string;
  country?: string;
  logoUrl?: string;
}

export interface Match {
  id: number;
  date: string;
  homePlayerId: number;
  homePlayerName: string;
  awayPlayerId: number;
  awayPlayerName: string;
  homeTeamId: number | null;
  homeTeamName: string;
  awayTeamId: number | null;
  awayTeamName: string;
  homeScore: number;
  awayScore: number;
  duration: number | null;
  gameMode: string | null;
  notes: string | null;
  createdAt: string;
}

export interface MatchRequest {
  date: string;
  homePlayerId: number;
  awayPlayerId: number;
  homeTeamId?: number | null;
  awayTeamId?: number | null;
  homeTeamName?: string;
  awayTeamName?: string;
  homeScore: number;
  awayScore: number;
  duration?: number;
  gameMode?: string;
  notes?: string;
}

export interface PlayerStats {
  playerId: number;
  playerName: string;
  matchesPlayed: number;
  wins: number;
  draws: number;
  losses: number;
  goalsFor: number;
  goalsAgainst: number;
  goalDifference: number;
  winRate: number;
  points: number;
  currentStreak: string;
}

export interface TeamStats {
  teamId: number;
  teamName: string;
  matchesPlayed: number;
  wins: number;
  draws: number;
  losses: number;
  goalsFor: number;
  goalsAgainst: number;
  goalDifference: number;
  winRate: number;
}

export interface HeadToHead {
  player1Stats: PlayerStats;
  player2Stats: PlayerStats;
  totalMatches: number;
}

export interface Dashboard {
  totalPlayers: number;
  totalTeams: number;
  totalMatches: number;
  topRanking: PlayerStats[];
  recentMatches: Match[];
}
