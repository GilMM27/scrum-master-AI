import { useCallback, useEffect, useMemo, useState } from "react";
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
import {
  getProjectAnalytics,
  getSprintAnalytics,
} from "../../features/dashboard/services/dashboard.service";
import { getProjectSprints } from "../../features/sprints/services/sprint.service";
import type {
  DashboardScope,
  ProjectAnalyticsData,
  SprintAnalyticsData,
} from "../../features/dashboard/types/dashboard.types";
import type { SprintItem } from "../../features/sprints/types/sprint.types";
import useProject from "../../hooks/useProject";
import useNotification from "../../hooks/useNotification";

const ManagerDashboardContent = () => {
  const { selectedProjectId } = useProject();
  const { showError } = useNotification();

  const [scope, setScope] = useState<DashboardScope>({ type: "project" });
  const [sprints, setSprints] = useState<SprintItem[]>([]);
  const [projectAnalytics, setProjectAnalytics] =
    useState<ProjectAnalyticsData | null>(null);
  const [sprintAnalytics, setSprintAnalytics] =
    useState<SprintAnalyticsData | null>(null);
  const [loading, setLoading] = useState(false);
  const [sprintsLoading, setSprintsLoading] = useState(false);

  const fetchSprints = useCallback(async () => {
    if (!selectedProjectId) {
      setSprints([]);
      return;
    }
    setSprintsLoading(true);
    try {
      const data = await getProjectSprints(selectedProjectId);
      setSprints(
        data.filter((s) => s.status === "ACTIVE" || s.status === "CLOSED"),
      );
    } catch {
      // non-critical
    } finally {
      setSprintsLoading(false);
    }
  }, [selectedProjectId]);

  const fetchAnalytics = useCallback(async () => {
    if (!selectedProjectId) {
      setProjectAnalytics(null);
      setSprintAnalytics(null);
      return;
    }
    setLoading(true);
    try {
      if (scope.type === "project") {
        const data = await getProjectAnalytics(selectedProjectId);
        setProjectAnalytics(data);
        setSprintAnalytics(null);
      } else if (scope.sprintId) {
        const data = await getSprintAnalytics(scope.sprintId);
        setSprintAnalytics(data);
        setProjectAnalytics(null);
      }
    } catch {
      showError("No fue posible cargar los datos del dashboard.");
    } finally {
      setLoading(false);
    }
  }, [selectedProjectId, scope, showError]);

  useEffect(() => {
    setScope({ type: "project" });
    setProjectAnalytics(null);
    setSprintAnalytics(null);
    fetchSprints();
  }, [fetchSprints]);

  useEffect(() => {
    fetchAnalytics();
  }, [fetchAnalytics]);

  const handleScopeChange = (newScope: DashboardScope) => {
    setScope(newScope);
  };

  const analytics =
    scope.type === "project" ? projectAnalytics : sprintAnalytics;

  const tasksDoneData = useMemo(() => {
    if (projectAnalytics) return projectAnalytics.tasksDoneBySprint ?? [];
    if (sprintAnalytics) return sprintAnalytics.tasksDoneBySprint ?? [];
    return [];
  }, [projectAnalytics, sprintAnalytics]);

  const burndownData = useMemo(() => {
    if (projectAnalytics) return projectAnalytics.burndownData ?? [];
    if (sprintAnalytics) return sprintAnalytics.burndownData ?? [];
    return [];
  }, [projectAnalytics, sprintAnalytics]);

  const burndownSprintName = useMemo(() => {
    if (scope.type === "sprint") return scope.sprintName ?? null;
    return null;
  }, [scope]);

  const leadTimeHistData = useMemo(() => {
    if (projectAnalytics) return projectAnalytics.leadTimeHistogram;
    if (sprintAnalytics) return sprintAnalytics.leadTimeHistogram;
    return [];
  }, [projectAnalytics, sprintAnalytics]);

  const leadTimeMean = useMemo(() => {
    if (projectAnalytics) return projectAnalytics.leadTimeMean ?? null;
    if (sprintAnalytics) return sprintAnalytics.leadTimeMean ?? null;
    return null;
  }, [projectAnalytics, sprintAnalytics]);

  const cycleTimeHistData = useMemo(() => {
    if (projectAnalytics) return projectAnalytics.cycleTimeHistogram;
    if (sprintAnalytics) return sprintAnalytics.cycleTimeHistogram;
    return [];
  }, [projectAnalytics, sprintAnalytics]);

  const cycleTimeMean = useMemo(() => {
    if (projectAnalytics) return projectAnalytics.cycleTimeMean ?? null;
    if (sprintAnalytics) return sprintAnalytics.cycleTimeMean ?? null;
    return null;
  }, [projectAnalytics, sprintAnalytics]);

  if (!selectedProjectId) {
    return (
      <Stack sx={{ alignItems: "center", justifyContent: "center", py: 10 }}>
        <Typography color="text.secondary">
          Selecciona un proyecto para ver el dashboard.
        </Typography>
      </Stack>
    );
  }

  return (
    <Stack spacing={3}>
      <KpiHeader
        onRefresh={fetchAnalytics}
        loading={loading || sprintsLoading}
      />

      <DashboardLabel
        scope={scope}
        sprints={sprints}
        onChange={handleScopeChange}
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
                  data={leadTimeHistData}
                  mean={leadTimeMean}
                />
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <CycleTimeChart data={cycleTimeHistData} mean={cycleTimeMean} />
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <WipByDeveloperChart data={tasksDoneData} />
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <BurndownChart
                  data={burndownData}
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
