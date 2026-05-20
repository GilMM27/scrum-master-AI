import { useCallback, useEffect, useState } from "react";
import {
  getProjectAnalytics,
  getSprintAnalytics,
} from "../services/dashboard.service";
import { getProjectSprints } from "../../sprints/services/sprint.service";
import type {
  DashboardScope,
  ProjectAnalyticsData,
  SprintAnalyticsData,
} from "../types/dashboard.types";
import type { SprintItem } from "../../sprints/types/sprint.types";

interface UseDashboardDataOptions {
  projectId: string | null;
  onError: (message: string) => void;
}

const useDashboardData = ({ projectId, onError }: UseDashboardDataOptions) => {
  const [scope, setScope] = useState<DashboardScope>({ type: "project" });
  const [sprints, setSprints] = useState<SprintItem[]>([]);
  const [projectAnalytics, setProjectAnalytics] =
    useState<ProjectAnalyticsData | null>(null);
  const [sprintAnalytics, setSprintAnalytics] =
    useState<SprintAnalyticsData | null>(null);
  const [loading, setLoading] = useState(false);
  const [sprintsLoading, setSprintsLoading] = useState(false);

  const fetchSprints = useCallback(async () => {
    if (!projectId) {
      setSprints([]);
      return;
    }
    setSprintsLoading(true);
    try {
      const data = await getProjectSprints(projectId);
      setSprints(
        data.filter((s) => s.status === "ACTIVE" || s.status === "CLOSED"),
      );
    } catch {
      // non-critical
    } finally {
      setSprintsLoading(false);
    }
  }, [projectId]);

  const fetchAnalytics = useCallback(async () => {
    if (!projectId) {
      setProjectAnalytics(null);
      setSprintAnalytics(null);
      return;
    }
    setLoading(true);
    try {
      if (scope.type === "project") {
        const data = await getProjectAnalytics(projectId);
        setProjectAnalytics(data);
        setSprintAnalytics(null);
      } else if (scope.sprintId) {
        const data = await getSprintAnalytics(scope.sprintId);
        setSprintAnalytics(data);
        setProjectAnalytics(null);
      }
    } catch {
      onError("No fue posible cargar los datos del dashboard.");
    } finally {
      setLoading(false);
    }
  }, [projectId, scope, onError]);

  useEffect(() => {
    setScope({ type: "project" });
    setProjectAnalytics(null);
    setSprintAnalytics(null);
    fetchSprints();
  }, [fetchSprints]);

  useEffect(() => {
    fetchAnalytics();
  }, [fetchAnalytics]);

  const analytics: ProjectAnalyticsData | SprintAnalyticsData | null =
    scope.type === "project" ? projectAnalytics : sprintAnalytics;

  return {
    analytics,
    fetchAnalytics,
    loading,
    scope,
    setScope,
    sprints,
    sprintsLoading,
  };
};

export default useDashboardData;
