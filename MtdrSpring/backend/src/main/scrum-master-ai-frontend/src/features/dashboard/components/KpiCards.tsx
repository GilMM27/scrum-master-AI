import Grid from "@mui/material/Grid";
import KpiCard from "./KpiCard";
import { computeKpiCards } from "../utils/kpiCalculations";
import type {
  DashboardScope,
  ProjectAnalyticsData,
  SprintAnalyticsData,
} from "../types/dashboard.types";

interface KpiCardsProps {
  analytics: ProjectAnalyticsData | SprintAnalyticsData;
  scope: DashboardScope;
}

const KpiCards = ({ analytics, scope }: KpiCardsProps) => {
  const cards = computeKpiCards(analytics, scope);

  return (
    <Grid container spacing={2}>
      {cards.map((card) => (
        <Grid key={card.label} size={{ xs: 12, sm: 6, md: 4 }}>
          <KpiCard data={card} />
        </Grid>
      ))}
    </Grid>
  );
};

export default KpiCards;
