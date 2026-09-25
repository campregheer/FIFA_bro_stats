import { MainLayout } from "./layouts/MainLayout";
import { DashboardPage } from "./pages/Dashboard";
import { PlayersPage } from "./pages/Players";
import { TeamsPage } from "./pages/Teams";
import { MatchesPage } from "./pages/Matches";
import { StatisticsPage } from "./pages/Statistics";
import { ChampionshipsPage } from "./pages/Championships";

export default function App() {
  return (
    <MainLayout
      dashboard={<DashboardPage />}
      players={<PlayersPage />}
      teams={<TeamsPage />}
      matches={<MatchesPage />}
      statistics={<StatisticsPage />}
      championships={<ChampionshipsPage />}
    />
  );
}
