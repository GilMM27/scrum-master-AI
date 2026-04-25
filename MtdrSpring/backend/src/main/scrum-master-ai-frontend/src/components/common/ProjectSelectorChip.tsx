import { useMemo, useState } from "react";
import { Box, Button, Chip, Divider, Menu, MenuItem, Stack, Typography } from "@mui/material";
import { alpha } from "@mui/material/styles";
import { AddRounded, FolderOpenRounded, KeyboardArrowDownRounded } from "@mui/icons-material";
import useProject from "../../hooks/useProject";
import type { User } from "../../types/User.types";
import CreateProjectDialog from "./CreateProjectDialog";

interface ProjectSelectorChipProps {
  userRole: User["role"];
}

const truncateName = (name: string, maxLen = 15) =>
  name.length > maxLen ? `${name.slice(0, maxLen)}...` : name;

const ProjectSelectorChip = ({ userRole }: ProjectSelectorChipProps) => {
  const { projects, selectedProjectId, setSelectedProjectId, loading } =
    useProject();

  // Derive locally from the primitive selectedProjectId so the chip always
  // reflects the latest selection without depending on a potentially-stale
  // memoised object from context.
  const selectedProject = useMemo(() => {
    const found = projects.find((p) => p.projectId === selectedProjectId) ?? null;
    return found;
  }, [projects, selectedProjectId]);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [createOpen, setCreateOpen] = useState(false);

  const open = Boolean(anchorEl);

  const handleChipClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleSelectProject = (id: string) => {
    setSelectedProjectId(id);
    handleMenuClose();
  };

  const handleOpenCreate = () => {
    handleMenuClose();
    setCreateOpen(true);
  };

  const chipLabel = loading
    ? "Cargando..."
    : selectedProject
      ? truncateName(selectedProject.name)
      : "Sin proyecto";

  return (
    <>
      <Chip
        label={chipLabel}
        onClick={handleChipClick}
        icon={
          <FolderOpenRounded
            sx={{ fontSize: "1rem !important" }}
          />
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
        onDelete={handleChipClick}
        sx={{
          display: { xs: "none", sm: "inline-flex" },
          bgcolor: alpha("#d94ab3", 0.1),
          color: "#d94ab3",
          border: "1px solid",
          borderColor: alpha("#d94ab3", 0.22),
          cursor: "pointer",
          p: 1,
          "& .MuiChip-icon": { color: "#d94ab3" },
          "& .MuiChip-deleteIcon": {
            color: "#d94ab3",
            "&:hover": { color: "#d94ab3" },
          },
          "&:hover": {
            bgcolor: alpha("#d94ab3", 0.18),
          },
        }}
      />

      <Menu
        anchorEl={anchorEl}
        open={open}
        onClose={handleMenuClose}
        transformOrigin={{ horizontal: "right", vertical: "top" }}
        anchorOrigin={{ horizontal: "right", vertical: "bottom" }}
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
              maxHeight: 300
            },
          },
        }}
      >
        <Box sx={{ px: 1.5, py: 1 }}>
          <Typography variant="caption" sx={{ color: "text.secondary", letterSpacing: 0.5, fontWeight: "bold" }}>
            MIS PROYECTOS
          </Typography>
        </Box>

        <Stack spacing={1.5} sx={{ p: 1}}>
          {projects.length === 0 ? (
            <MenuItem disabled sx={{ borderRadius: 1, mx: 0.5 }}>
              <Typography variant="body2" color="text.secondary">
                Sin proyectos disponibles
              </Typography>
            </MenuItem>
          ) : (
            projects.map((project) => (
              <MenuItem
                key={project.projectId}
                selected={project.projectId === selectedProject?.projectId}
                onClick={() => handleSelectProject(project.projectId)}
                sx={{
                  borderRadius: 1,
                  mx: 0.5,
                  "&.Mui-selected": {
                    bgcolor: alpha("#d94ab3", 0.18),
                    "&:hover": { bgcolor: alpha("#d94ab3", 0.22) },
                  },
                }}
              >
                <Typography
                  variant="body2"
                  sx={{
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    whiteSpace: "nowrap",
                    width: "100%",
                  }}
                >
                  {project.name}
                </Typography>
              </MenuItem>
            ))
          )}
        </Stack>

        {userRole === "MANAGER" && (
          <>
            <Divider sx={{ my: 0.75 }} />
            <Box sx={{ px: 1, py: 1 }}>
              <Button
                fullWidth
                variant="outlined"
                size="small"
                startIcon={<AddRounded />}
                onClick={handleOpenCreate}
                sx={{
                  display: { xs: "none", sm: "inline-flex" },
                  bgcolor: alpha("#d94ab3", 0.1),
                  color: "#d94ab3",
                  border: "1px solid",
                  borderColor: alpha("#d94ab3", 0.22),
                  cursor: "pointer",
                  p: 1,
                  "& .MuiChip-icon": { color: "#d94ab3" },
                  "& .MuiChip-deleteIcon": {
                    color: "#d94ab3",
                    "&:hover": { color: "#d94ab3" },
                  },
                  "&:hover": {
                    bgcolor: alpha("#d94ab3", 0.18),
                    borderColor: "#d94ab3",
                    borderWidth: 1.5,
                  },
                }}
              >
                Nuevo Proyecto
              </Button>
            </Box>
          </>
        )}
      </Menu>

      <CreateProjectDialog open={createOpen} onClose={() => setCreateOpen(false)} />
    </>
  );
};

export default ProjectSelectorChip;
