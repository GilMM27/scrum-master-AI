import { useState } from "react";
import {
  Box,
  Chip,
  Divider,
  Menu,
  MenuItem,
  Stack,
  Typography,
} from "@mui/material";
import { alpha } from "@mui/material/styles";
import {
  KeyboardArrowDownRounded,
  LockRounded,
  PlayArrowRounded,
  SpeedRounded,
} from "@mui/icons-material";
import type { SprintItem } from "../types/sprint.types";

interface SprintLabelProps {
  sprints: SprintItem[];
  selectedSprintId: string | null;
  onSelectSprint: (sprintId: string) => void;
  loading?: boolean;
}

const STATUS_COLORS: Record<string, "info" | "success" | "error"> = {
    PLANNED: "info",
    ACTIVE: "success",
    CLOSED: "error",
}

const STATUS_ICONS: Record<string, React.ReactNode> = {
  PLANNED: <SpeedRounded sx={{ fontSize: "0.9rem !important" }} />,
  ACTIVE: <PlayArrowRounded sx={{ fontSize: "0.9rem !important" }} />,
  CLOSED: <LockRounded sx={{ fontSize: "0.9rem !important" }} />,
};

const STATUS_LABELS: Record<string, string> = {
  PLANNED: "Planificado",
  ACTIVE: "Activo",
  CLOSED: "Cerrado",
};

const truncate = (s: string | null, max = 20) =>
  !s ? "Sin nombre" : s.length > max ? `${s.slice(0, max)}…` : s;

const SprintLabel = ({
  sprints,
  selectedSprintId,
  onSelectSprint,
  loading = false,
}: SprintLabelProps) => {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);

  const selectedSprint =
    sprints.find((s) => s.sprintId === selectedSprintId) ?? null;
  const colors = STATUS_COLORS[selectedSprint?.status ?? "PLANNED"];

  const handleOpen = (e: React.MouseEvent<HTMLElement>) =>
    setAnchorEl(e.currentTarget);
  const handleClose = () => setAnchorEl(null);
  const handleSelect = (id: string) => {
    onSelectSprint(id);
    handleClose();
  };

  const chipLabel = loading
    ? "Cargando..."
    : selectedSprint
      ? truncate(selectedSprint?.name)
      : "Sin sprint";

  return (
    <Stack
      direction="row"
      spacing={1.5}
      sx={{ alignItems: "center", flexWrap: "wrap" }}
      useFlexGap
    >
      <Chip
        label={chipLabel}
        icon={
          STATUS_ICONS[
            selectedSprint?.status ?? "PLANNED"
          ] as React.ReactElement
        }
        deleteIcon={
          <KeyboardArrowDownRounded
            sx={{
              fontSize: "1.1rem !important",
              transform: open ? "rotate(180deg)" : "rotate(0deg)",
              transition: "transform 0.2s ease",
            }}
          />
        }
        onDelete={sprints.length > 0 ? handleOpen : undefined}
        onClick={sprints.length > 0 ? handleOpen : undefined}
        variant="outlined"
        color={colors}
        sx={{ p: 1 }}
      />

      <Menu
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        slotProps={{
          paper: {
            sx: {
              bgcolor: "background.paper",
              border: "1px solid",
              borderColor: "divider",
              borderRadius: 1,
              minWidth: 220,
              maxWidth: 300,
              marginTop: 2,
              boxShadow: "0 8px 32px rgba(0,0,0,0.5)",
              maxHeight: 300,
            },
          },
        }}
      >
        <Box sx={{ px: 2, py: 1 }}>
          <Typography
            variant="caption"
            color="text.secondary"
            sx={{ fontWeight: "bold", letterSpacing: 0.5 }}
          >
            SPRINTS DEL PROYECTO
          </Typography>
        </Box>
        <Divider />
        {sprints.length === 0 ? (
          <MenuItem disabled>
            <Typography variant="body2" color="text.secondary">
              No hay sprints creados
            </Typography>
          </MenuItem>
        ) : (
          sprints.map((sprint) => {
            const sc = STATUS_COLORS[sprint.status];
            return (
              <MenuItem
                key={sprint.sprintId}
                selected={sprint.sprintId === selectedSprintId}
                onClick={() => handleSelect(sprint.sprintId)}
                sx={{ gap: 1.5, borderRadius: 1, mx: 0.5,
                    "&.Mui-selected": {
                    bgcolor: alpha("#77fffa", 0.18),
                    "&:hover": { bgcolor: alpha("#77fffa", 0.22) },
                    }
                 }}
              >
                <Box
                  sx={{
                    width: 8,
                    height: 8,
                    borderRadius: "50%",
                    bgcolor: `${sc}.main`,
                    flexShrink: 0,
                  }}
                />
                <Box sx={{ flexGrow: 1, minWidth: 0 }}>
                  <Typography variant="body2" noWrap>
                    {sprint.name ?? "Sin nombre"}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    {STATUS_LABELS[sprint.status]}
                    {sprint.startDate ? ` · ${sprint.startDate}` : ""}
                  </Typography>
                </Box>
              </MenuItem>
            );
          })
        )}
      </Menu>
    </Stack>
  );
};

export default SprintLabel;
