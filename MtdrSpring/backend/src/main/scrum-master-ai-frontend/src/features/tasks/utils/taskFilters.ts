import type { TaskFiltersState, TaskItem } from "../types/tasks.types";

export const TASK_FILTERS_INITIAL_STATE: TaskFiltersState = {
  search: "",
  status: "",
  priority: "",
  inReview: false,
  blocked: false,
};

export const applyTaskFilters = (
  tasks: TaskItem[],
  filters: TaskFiltersState,
): TaskItem[] => {
  const searchValue = filters.search.toLowerCase();

  return tasks.filter((task) => {
    const matchesSearch =
      !searchValue || task.title.toLowerCase().includes(searchValue);

    const matchesStatus = !filters.status || task.status === filters.status;
    const matchesPriority =
      !filters.priority || task.priority === filters.priority;
    const matchesInReview = !filters.inReview || task.inReview;
    const matchesBlocked = !filters.blocked || task.blocked;

    return (
      matchesSearch &&
      matchesStatus &&
      matchesPriority &&
      matchesInReview &&
      matchesBlocked
    );
  });
};
