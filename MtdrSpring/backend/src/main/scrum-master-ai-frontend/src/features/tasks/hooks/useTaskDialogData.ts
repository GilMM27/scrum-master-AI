import { useCallback, useState } from "react";
import { getAvailableSprints, getProjectDevelopers, getTaskDetails } from "../services/tasks.service";
import type { SprintOption, TaskAssignee, TaskDetailItem } from "../types/tasks.types";

interface UseTaskDialogDataOptions {
  projectId: string | null;
}

interface LoadTaskDetailsOptions {
  onError?: () => void;
}

const useTaskDialogData = ({ projectId }: UseTaskDialogDataOptions) => {
  const [developers, setDevelopers] = useState<TaskAssignee[]>([]);
  const [sprintOptions, setSprintOptions] = useState<SprintOption[]>([]);
  const [selectedTask, setSelectedTask] = useState<TaskDetailItem | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);

  const loadReferenceData = useCallback(async () => {
    if (!projectId) {
      setDevelopers([]);
      setSprintOptions([]);
      return;
    }

    try {
      const [loadedDevelopers, loadedSprintOptions] = await Promise.all([
        getProjectDevelopers(projectId),
        getAvailableSprints(projectId),
      ]);

      setDevelopers(loadedDevelopers);
      setSprintOptions(loadedSprintOptions);
    } catch {
      setDevelopers([]);
      setSprintOptions([]);
    }
  }, [projectId]);

  const loadTaskDetails = useCallback(
    async (taskId: string, options: LoadTaskDetailsOptions = {}) => {
      setSelectedTask(null);
      setDetailLoading(true);

      try {
        const taskDetail = await getTaskDetails(taskId);
        setSelectedTask(taskDetail);
        return taskDetail;
      } catch {
        options.onError?.();
        return null;
      } finally {
        setDetailLoading(false);
      }
    },
    [],
  );

  const clearSelectedTask = useCallback(() => {
    setSelectedTask(null);
  }, []);

  return {
    clearSelectedTask,
    detailLoading,
    developers,
    loadReferenceData,
    loadTaskDetails,
    selectedTask,
    sprintOptions,
  };
};

export default useTaskDialogData;
