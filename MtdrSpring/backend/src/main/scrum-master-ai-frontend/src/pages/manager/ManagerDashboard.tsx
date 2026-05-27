import { Box, CircularProgress, Stack, Typography } from "@mui/material";
import Grid from "@mui/material/Grid";
import DashboardLayout from "../../layouts/DashboardLayout";
import KpiHeader from "../../features/dashboard/components/KpiHeader";
import DashboardLabel from "../../features/dashboard/components/DashboardLabel";
import KpiCards from "../../features/dashboard/components/KpiCards";
import LeadTimeTrendChart from "../../features/dashboard/components/LeadTimeTrendChart";
import CycleTimeChart from "../../features/dashboard/components/CycleTimeChart";
import WipByDeveloperChart from "../../features/dashboard/components/WipByDeveloperChart";
import BurndownChart from "../../features/dashboard/components/BurndownChart";
import useDashboardData from "../../features/dashboard/hooks/useDashboardData";
import useProject from "../../hooks/useProject";
import useNotification from "../../hooks/useNotification";

const ManagerDashboardContent = () => {
  const { selectedProjectId } = useProject();
  const { showError } = useNotification();

  const { analytics, fetchAnalytics, loading, scope, setScope, sprints, sprintsLoading } = useDashboardData({ projectId: selectedProjectId, onError: showError });

  if (!selectedProjectId) {
    return (
      <Stack sx={{ alignItems: "center", justifyContent: "center", py: 10 }}>
        <Typography color="text.secondary">
          Selecciona un proyecto para ver el dashboard.
        </Typography>
      </Stack>
    );
  }

  const burndownSprintName = scope.type === "sprint" ? (scope.sprintName ?? null) : null;

  return (
    <Stack spacing={3}>
      <KpiHeader
        onRefresh={fetchAnalytics}
        loading={loading || sprintsLoading}
      />

      <DashboardLabel
        scope={scope}
        sprints={sprints}
        onChange={setScope}
      />

      {loading ? (
        <Stack sx={{ alignItems: "center", py: 8 }}>
          <CircularProgress />
          <Typography color="text.secondary" sx={{ mt: 2 }}>
            Calculando métricas…
          </Typography>
        </Stack>
      ) : analytics ? (
        <>
          <KpiCards analytics={analytics} scope={scope} />

          <Box sx={{ mt: 1 }}>
            <Grid container spacing={3}>
              <Grid size={{ xs: 12, md: 6 }}>
                <LeadTimeTrendChart
                  data={analytics.leadTimeHistogram}
                  mean={analytics.leadTimeMean ?? null}
                />
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <CycleTimeChart
                  data={analytics.cycleTimeHistogram}
                  mean={analytics.cycleTimeMean ?? null}
                />
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <WipByDeveloperChart data={analytics.tasksDoneBySprint ?? []} />
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <BurndownChart
                  data={analytics.burndownData ?? []}
                  sprintName={burndownSprintName}
                />
              </Grid>
            </Grid>
          </Box>
        </>
      ) : (
        <Stack sx={{ alignItems: "center", py: 8 }}>
          <Typography color="text.secondary">
            No hay datos disponibles para mostrar.
          </Typography>
        </Stack>
      )}
    </Stack>
  );
};

const ManagerDashboard = () => (
  <DashboardLayout>
    <ManagerDashboardContent />
  </DashboardLayout>
);

export default ManagerDashboard;
