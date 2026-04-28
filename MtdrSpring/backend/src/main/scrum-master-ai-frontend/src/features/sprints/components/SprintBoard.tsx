import { useRef, useState } from "react";
import {
  Avatar,
  Box,
  Chip,
  CircularProgress,
  Paper,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import { alpha } from "@mui/material/styles";
import type { TaskItem, TaskStatus } from "../../tasks/types/tasks.types";
import TaskPriorityChip from "../../tasks/components/TaskPriorityChip";

interface SprintBoardProps {
  tasks: TaskItem[];
  onViewTask: (task: TaskItem) => void;
  onTaskStatusChange: (taskId: string, newStatus: TaskStatus) => Promise<void>;
  onDropError: (message: string) => void;
}

const COLUMNS: { status: TaskStatus; label: string; color: string }[] = [
  { status: "TO_DO", label: "Por hacer", color: "#b4bdc7" },
  { status: "IN_PROGRESS", label: "En progreso", color: "#1eb1ce" },
  { status: "REVIEW", label: "En revisión", color: "#FFD166" },
  { status: "BLOCKED", label: "Bloqueada", color: "#FF5C8A" },
  { status: "DONE", label: "Completada", color: "#4ef770" },
];

// Matches backend TasksService VALID_TRANSITIONS
const VALID_TRANSITIONS: Record<TaskStatus, TaskStatus[]> = {
  TO_DO: ["IN_PROGRESS", "BLOCKED"],
  IN_PROGRESS: ["REVIEW", "BLOCKED", "TO_DO"],
  REVIEW: ["DONE", "IN_PROGRESS"],
  BLOCKED: ["TO_DO", "IN_PROGRESS"],
  DONE: [],
};

const PRIORITY_ORDER: Record<string, number> = {
  CRITICAL: 0,
  HIGH: 1,
  MEDIUM: 2,
  LOW: 3,
};

interface TaskCardProps {
  task: TaskItem;
  onDragStart: (task: TaskItem) => void;
  onDragEnd: () => void;
  isDragging: boolean;
  onView: (task: TaskItem) => void;
}

const TaskCard = ({
  task,
  onDragStart,
  onDragEnd,
  isDragging,
  onView,
}: TaskCardProps) => (
  <Paper
    draggable
    onDragStart={() => onDragStart(task)}
    onDragEnd={onDragEnd}
    onClick={() => onView(task)}
    elevation={isDragging ? 6 : 1}
    sx={{
      p: 1.5,
      cursor: "grab",
      borderRadius: 2,
      opacity: isDragging ? 0.5 : 1,
      border: "1px solid",
      borderColor: "divider",
      transition: "box-shadow 0.15s, opacity 0.15s",
      "&:hover": { boxShadow: 4, borderColor: "primary.main" },
      "&:active": { cursor: "grabbing" },
      userSelect: "none",
    }}
  >
    <Stack spacing={1}>
      <Typography variant="body2" sx={{ lineHeight: 1.3, fontWeight: 500 }}>
        {task.title}
      </Typography>
      <Stack
        direction="row"
        spacing={1}
        sx={{ alignItems: "center", justifyContent: "space-between" }}
      >
        <TaskPriorityChip priority={task.priority} />
        <Stack direction="row" spacing={0.5}>
          {task.assignees.length > 0 ? (
            task.assignees.slice(0, 3).map((a) => (
              <Tooltip key={a.userId} title={a.username}>
                <Avatar
                  sx={{
                    width: 22,
                    height: 22,
                    fontSize: "0.7rem",
                    bgcolor: alpha("#0016a6", 0.34),
                    color: "info.main",
                    border: "1px solid",
                    borderColor: "divider",
                  }}
                >
                  {a.username.charAt(0).toUpperCase()}{a.username.charAt(1).toLowerCase()}
                </Avatar>
              </Tooltip>
            ))
          ) : (
            <Typography
              variant="caption"
              color="text.secondary"
              sx={{ fontWeight: 700 }}
            >
              -
            </Typography>
          )}
        </Stack>
      </Stack>
    </Stack>
  </Paper>
);

const SprintBoard = ({
  tasks,
  onViewTask,
  onTaskStatusChange,
  onDropError,
}: SprintBoardProps) => {
  const [dragging, setDragging] = useState<TaskItem | null>(null);
  const [droppingOver, setDroppingOver] = useState<TaskStatus | null>(null);
  const [updatingTaskId, setUpdatingTaskId] = useState<string | null>(null);
  const dragRef = useRef<TaskItem | null>(null);

  const tasksByStatus = COLUMNS.reduce(
    (acc, col) => {
      acc[col.status] = tasks
        .filter((t) => t.status === col.status)
        .sort(
          (a, b) => PRIORITY_ORDER[a.priority] - PRIORITY_ORDER[b.priority],
        );
      return acc;
    },
    {} as Record<TaskStatus, TaskItem[]>,
  );

  const handleDragStart = (task: TaskItem) => {
    setDragging(task);
    dragRef.current = task;
  };

  const handleDragEnd = () => {
    setDragging(null);
    setDroppingOver(null);
    dragRef.current = null;
  };

  const handleDragOver = (e: React.DragEvent, targetStatus: TaskStatus) => {
    e.preventDefault();
    setDroppingOver(targetStatus);
  };

  const handleDrop = async (e: React.DragEvent, targetStatus: TaskStatus) => {
    e.preventDefault();
    setDroppingOver(null);
    const task = dragRef.current;
    if (!task || task.status === targetStatus) return;

    const validTargets = VALID_TRANSITIONS[task.status];
    if (!validTargets.includes(targetStatus)) {
      onDropError(
        `Transición no permitida: ${task.status} → ${targetStatus}. Transiciones válidas: ${validTargets.join(", ") || "ninguna"}.`,
      );
      setDragging(null);
      dragRef.current = null;
      return;
    }

    setUpdatingTaskId(task.taskId);
    setDragging(null);
    dragRef.current = null;
    try {
      await onTaskStatusChange(task.taskId, targetStatus);
    } finally {
      setUpdatingTaskId(null);
    }
  };

  return (
    <Box
      sx={{
        display: "flex",
        gap: 2,
        overflowX: "auto",
        pb: 1,
        minHeight: 480,
      }}
    >
      {COLUMNS.map((col) => {
        const columnTasks = tasksByStatus[col.status] ?? [];
        const isDropTarget = droppingOver === col.status;

        return (
          <Box
            key={col.status}
            onDragOver={(e) => handleDragOver(e, col.status)}
            onDragLeave={() => setDroppingOver(null)}
            onDrop={(e) => handleDrop(e, col.status)}
            sx={{
              minWidth: 220,
              width: 220,
              flexShrink: 0,
              display: "flex",
              flexDirection: "column",
              gap: 1,
              p: 1.5,
              borderRadius: 2,
              border: "2px solid",
              borderColor: isDropTarget ? col.color : "transparent",
              bgcolor: isDropTarget
                ? alpha(col.color, 0.06)
                : "background.paper",
              boxShadow: isDropTarget
                ? `0 0 0 2px ${alpha(col.color, 0.25)}`
                : "none",
              transition: "border-color 0.15s, background-color 0.15s",
            }}
          >
            {/* Column header */}
            <Stack
              direction="row"
              spacing={1}
              sx={{ alignItems: "center", mb: 0.5 }}
            >
              <Typography
                variant="subtitle2"
                sx={{ color: col.color, flexGrow: 1, fontWeight: 700 }}
              >
                {col.label}
              </Typography>
              <Chip
                label={columnTasks.length}
                size="small"
                sx={{
                  height: 20,
                  fontSize: "0.7rem",
                  bgcolor: alpha(col.color, 0.12),
                  color: col.color,
                  fontWeight: 700,
                }}
              />
            </Stack>

            {/* Task cards */}
            {columnTasks.map((task) => (
              <Box key={task.taskId} sx={{ position: "relative" }}>
                <TaskCard
                  task={task}
                  onDragStart={handleDragStart}
                  onDragEnd={handleDragEnd}
                  isDragging={dragging?.taskId === task.taskId}
                  onView={onViewTask}
                />
                {updatingTaskId === task.taskId && (
                  <Box
                    sx={{
                      position: "absolute",
                      inset: 0,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      bgcolor: alpha("#000", 0.15),
                      borderRadius: 2,
                    }}
                  >
                    <CircularProgress size={20} />
                  </Box>
                )}
              </Box>
            ))}

            {columnTasks.length === 0 && (
              <Box
                sx={{
                  flexGrow: 1,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  minHeight: 80,
                  border: "1.5px dashed",
                  borderColor: isDropTarget ? col.color : "divider",
                  borderRadius: 2,
                }}
              >
                <Typography variant="caption" color="text.secondary">
                  Sin tareas
                </Typography>
              </Box>
            )}
          </Box>
        );
      })}
    </Box>
  );
};

export default SprintBoard;
