import type { TaskItem } from "../types/tasks.types";
import TaskStatusChip from "./TaskStatusChip";
import TaskPriorityChip from "./TaskPriorityChip";
import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Tooltip,
  Typography,
  IconButton,
  Stack,
  Avatar,
} from "@mui/material";
import { alpha } from "@mui/material/styles";
import { VisibilityRounded } from "@mui/icons-material";
import TaskBlockedChip from "./TaskBlockedChip";

interface TasksTableProps {
  tasks: TaskItem[];
  showAssignee?: boolean;
  showSprint?: boolean;
  onViewTask: (task: TaskItem) => void;
}

const TasksTable = ({
  tasks,
  showAssignee = true,
  showSprint = true,
  onViewTask,
}: TasksTableProps) => {
  return (
    <Paper sx={{ borderRadius: 2, overflow: "hidden" }}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Ver</TableCell>
            <TableCell>Tarea</TableCell>
            <TableCell>Estado</TableCell>
            <TableCell>Prioridad</TableCell>
            {showAssignee && <TableCell>Asignados</TableCell>}
            {showSprint && <TableCell>Sprint</TableCell>}
            <TableCell>Horas estimadas</TableCell>
            <TableCell>Horas reales</TableCell>
            <TableCell>Bloqueada</TableCell>
            {/* <TableCell>In Review</TableCell> */}
          </TableRow>
        </TableHead>

        <TableBody>
          {tasks.length === 0 ? (
            <TableRow>
              <TableCell colSpan={7 + (showAssignee ? 1 : 0) + (showSprint ? 1 : 0)} align="center">
                <Typography color="text.secondary">
                  No se encontraron tareas.
                </Typography>
              </TableCell>
            </TableRow>
          ) : (
            tasks.map((task) => (
              <TableRow key={task.taskId}>
                <TableCell>
                  <Tooltip title="Ver tarea">
                    <IconButton onClick={() => onViewTask(task)}>
                      <VisibilityRounded />
                    </IconButton>
                  </Tooltip>
                </TableCell>
                <TableCell>
                  <Typography variant="subtitle2">{task.title}</Typography>
                </TableCell>
                <TableCell>
                  <TaskStatusChip status={task.status} />
                </TableCell>
                <TableCell>
                  <TaskPriorityChip priority={task.priority} />
                </TableCell>
                {showAssignee && (
                  <TableCell>
                    <Stack
                      direction="row"
                      spacing={0.5}
                      useFlexGap
                      sx={{ flexWrap: "wrap" }}
                    >
                      {task.assignees.length > 0 ? (
                        task.assignees.map((assignee) => (
                          <Tooltip
                            key={assignee.userId}
                            title={assignee.username}
                          >
                            <Avatar
                              sx={{
                                width: 24,
                                height: 24,
                                fontSize: "0.8rem",
                                color: "info.main",
                                border: "1px solid",
                                borderColor: "divider",
                                bgcolor: alpha("#0016a6", 0.34),
                              }}
                            >
                              {assignee.username.charAt(0).toUpperCase()}
                              {assignee.username.charAt(1).toLowerCase()}
                            </Avatar>
                          </Tooltip>
                        ))
                      ) : (
                        <Typography color="text.secondary" variant="body2">
                          Sin asignar
                        </Typography>
                      )}
                    </Stack>
                  </TableCell>
                )}
                {showSprint && (
                  <TableCell>
                    {task.sprintName ?? "Backlog"}
                  </TableCell>
                )}
                <TableCell>
                  {task.storyPoints > 0 ? task.storyPoints : "-"}
                </TableCell>
                <TableCell>
                  {task.actualHours !== null ? `${task.actualHours}h` : "-"}
                </TableCell>
                <TableCell>
                  <TaskBlockedChip blocked={task.blocked} />
                </TableCell>
                {/* <TableCell>
                  {task.inReview ? 'Sí' : 'No'}
                </TableCell> */}
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
    </Paper>
  );
};

export default TasksTable;
