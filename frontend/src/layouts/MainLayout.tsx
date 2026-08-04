import { useState } from "react";
import type { ReactNode } from "react";

type Tab = "dashboard" | "players" | "teams" | "matches" | "statistics";

interface MainLayoutProps {
  dashboard: ReactNode;
  players: ReactNode;
  teams: ReactNode;
  matches: ReactNode;
  statistics: ReactNode;
}

const TABS: { key: Tab; label: string }[] = [
  { key: "dashboard", label: "Dashboard" },
  { key: "players", label: "Jogadores" },
  { key: "teams", label: "Times" },
  { key: "matches", label: "Partidas" },
  { key: "statistics", label: "Estatísticas" },
];

export function MainLayout({ dashboard, players, teams, matches, statistics }: MainLayoutProps) {
  const [activeTab, setActiveTab] = useState<Tab>("dashboard");

  const content: Record<Tab, ReactNode> = {
    dashboard,
    players,
    teams,
    matches,
    statistics,
  };

  return (
    <div className="min-h-screen bg-gray-950 text-gray-100">
      <header className="border-b border-gray-800 bg-gray-900">
        <div className="max-w-6xl mx-auto px-4 py-4">
          <h1 className="text-xl font-bold">⚽ FIFA Bro Stats</h1>
        </div>
        <nav className="max-w-6xl mx-auto px-4 flex gap-1">
          {TABS.map((tab) => (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              className={`px-4 py-2 text-sm font-medium rounded-t-lg transition-colors ${
                activeTab === tab.key
                  ? "bg-gray-950 text-emerald-400 border-b-2 border-emerald-400"
                  : "text-gray-400 hover:text-gray-200"
              }`}
            >
              {tab.label}
            </button>
          ))}
        </nav>
      </header>
      <main className="max-w-6xl mx-auto px-4 py-6">{content[activeTab]}</main>
    </div>
  );
}