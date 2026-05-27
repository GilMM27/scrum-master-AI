import { useCallback, useEffect, useMemo, useState } from "react";
import type { TaskItem } from "../../tasks/types/tasks.types";
import type { SprintItem } from "../types/sprint.types";
import { getProjectSprints, getSprintTasks } from "../services/sprint.service";

type SprintPageState = {
  sprints: SprintItem[];
  sprintsLoading: boolean;
  selectedSprintId: string | null;
  tasks: TaskItem[];
  tasksLoading: boolean;
};

const INITIAL_STATE: SprintPageState = {
  sprints: [],
  sprintsLoading: false,
  selectedSprintId: null,
  tasks: [],
  tasksLoading: false,
};

/**
 * Owns all server-fetched sprint/task data for the sprint page.
 * UI-only state (dialogs, filters, active tab) stays in the component.
 */
export function useSprintPageData({
  projectId,
  onError,
}: {
  projectId: string | null;
  onError: (msg: string) => void;
}) {
  const [state, setState] = useState<SprintPageState>(INITIAL_STATE);

  const fetchSprints = useCallback(async () => {
    if (!projectId) return;
    setState((prev) => ({ ...prev, sprintsLoading: true }));
    try {
      const data = await getProjectSprints(projectId);
      setState((prev) => ({
        ...prev,
        sprints: data,
        sprintsLoading: false,
        selectedSprintId:
          prev.selectedSprintId ??
          (data.find((s) => s.status === "ACTIVE") ?? data[0] ?? null)
            ?.sprintId ??
          null,
      }));
    } catch {
      onError("Error al cargar los sprints del proyecto.");
      setState((prev) => ({ ...prev, sprintsLoading: false }));
    }
  }, [projectId, onError]);

  const fetchSprintTasks = useCallback(
    async (sprintId: string) => {
      setState((prev) => ({ ...prev, tasksLoading: true }));
      try {
        const data = await getSprintTasks(sprintId);
        setState((prev) => ({ ...prev, tasks: data, tasksLoading: false }));
      } catch {
        onError("Error al cargar las tareas del sprint.");
        setState((prev) => ({ ...prev, tasksLoading: false }));
      }
    },
    [onError],
  );

  // Reset sprint/task data when the project changes, then fetch.
  // A single setState call keeps re-renders to a minimum.
  useEffect(() => {
    if (!projectId) return;
    setState({ ...INITIAL_STATE, sprintsLoading: true }); // eslint-disable-line react-hooks/set-state-in-effect
    fetchSprints();
  }, [projectId, fetchSprints]);

  // Load tasks whenever the selected sprint changes.
  useEffect(() => {
    if (!state.selectedSprintId) {
      setState((prev) => ({ ...prev, tasks: [] })); // eslint-disable-line react-hooks/set-state-in-effect
      return;
    }
    fetchSprintTasks(state.selectedSprintId);
  }, [state.selectedSprintId, fetchSprintTasks]);

  const selectedSprint = useMemo(
    () =>
      state.sprints.find((s) => s.sprintId === state.selectedSprintId) ?? null,
    [state.sprints, state.selectedSprintId],
  );

  const setSelectedSprintId = (id: string | null) =>
    setState((prev) => ({ ...prev, selectedSprintId: id }));

  /** Functional updater for tasks — used by the component for optimistic updates. */
  const setTasks = (updater: (prev: TaskItem[]) => TaskItem[]) =>
    setState((prev) => ({ ...prev, tasks: updater(prev.tasks) }));

  return {
    sprints: state.sprints,
    sprintsLoading: state.sprintsLoading,
    selectedSprintId: state.selectedSprintId,
    setSelectedSprintId,
    tasks: state.tasks,
    setTasks,
    tasksLoading: state.tasksLoading,
    selectedSprint,
    fetchSprints,
    fetchSprintTasks,
  };
}
