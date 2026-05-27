import { Box, Button, Chip, Divider, Stack, Typography } from "@mui/material";
import DashboardRounded from "@mui/icons-material/DashboardRounded";
import SpeedRounded from "@mui/icons-material/SpeedRounded";
import type { DashboardScope } from "../types/dashboard.types";
import type { SprintItem } from "../../sprints/types/sprint.types";

interface DashboardLabelProps {
  scope: DashboardScope;
  sprints: SprintItem[];
  onChange: (scope: DashboardScope) => void;
}

const statusColor = (status: SprintItem["status"]): string => {
  if (status === "ACTIVE") return "#4ef770";
  return "#b4bdc7";
};

const DashboardLabel = ({ scope, sprints, onChange }: DashboardLabelProps) => {
  const isProject = scope.type === "project";

  return (
    <Box
      sx={{
        p: 2,
        border: "1px solid",
        borderColor: "divider",
        borderRadius: 2,
        bgcolor: "background.paper",
      }}
    >
      <Stack direction="row" spacing={1} sx={{ alignItems: "center", mb: 1.5 }}>
        <SpeedRounded sx={{ fontSize: 18, color: "text.secondary" }} />
        <Typography variant="subtitle2" color="text.secondary">
          Visualizando datos de:
        </Typography>
      </Stack>
      <Stack direction="row" sx={{ flexWrap: "wrap", gap: 1 }}>
        <Button
          variant={isProject ? "contained" : "outlined"}
          size="small"
          startIcon={<DashboardRounded />}
          onClick={() => onChange({ type: "project" })}
          sx={{
            borderRadius: 2,
            ...(isProject
              ? {}
              : {
                  borderColor: "divider",
                  color: "text.secondary",
                  "&:hover": {
                    borderColor: "info.main",
                    color: "info.main",
                  },
                }),
          }}
        >
          Proyecto
        </Button>

        {sprints.length > 0 && (
          <>
            <Divider orientation="vertical" flexItem sx={{ mx: 0.5 }} />
            {sprints.map((sprint) => {
              const isSelected =
                scope.type === "sprint" && scope.sprintId === sprint.sprintId;
              return (
                <Button
                  key={sprint.sprintId}
                  variant={isSelected ? "contained" : "outlined"}
                  color={isSelected ? "primary" : "info"}
                  size="small"
                  onClick={() =>
                    onChange({
                      type: "sprint",
                      sprintId: sprint.sprintId,
                      sprintName: sprint.name ?? sprint.sprintId,
                    })
                  }
                  endIcon={
                    <Chip
                      label={sprint.status === "ACTIVE" ? "Activo" : "Cerrado"}
                      size="small"
                      variant="outlined"
                      sx={{
                        color: statusColor(sprint.status),
                        borderColor: statusColor(sprint.status),
                      }}
                    />
                  }
                  sx={{
                    borderRadius: 2,
                    ...(isSelected
                      ? {}
                      : {
                          borderColor: "divider",
                          color: "text.secondary",
                          "&:hover": {
                            borderColor: "info.main",
                            color: "info.main",
                          },
                        }),
                  }}
                >
                  {sprint.name ?? "Sprint"}
                </Button>
              );
            })}
          </>
        )}

        {sprints.length === 0 && (
          <Typography
            variant="body2"
            color="text.disabled"
            sx={{ alignSelf: "center", ml: 1 }}
          >
            No hay sprints disponibles para este proyecto.
          </Typography>
        )}
      </Stack>
    </Box>
  );
};

export default DashboardLabel;
