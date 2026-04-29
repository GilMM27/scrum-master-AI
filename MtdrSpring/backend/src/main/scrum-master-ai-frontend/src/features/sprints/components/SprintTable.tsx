import type { TaskItem } from "../../tasks/types/tasks.types";
import TasksTable from "../../tasks/components/TasksTable";

interface SprintTableProps {
  tasks: TaskItem[];
  onViewTask: (task: TaskItem) => void;
}

const SprintTable = ({ tasks, onViewTask }: SprintTableProps) => (
  <TasksTable tasks={tasks} showSprint={false} onViewTask={onViewTask} />
);

export default SprintTable;
