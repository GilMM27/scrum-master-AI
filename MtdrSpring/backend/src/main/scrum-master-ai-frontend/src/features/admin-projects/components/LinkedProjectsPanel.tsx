import { useState } from "react";
import { alpha, Box, Button, Chip, CircularProgress, Divider,IconButton, Paper, Stack, Tooltip, Typography } from "@mui/material";
import { AddRounded, LinkOffRounded } from "@mui/icons-material";
import type { DeveloperSummary, ProjectSummary } from "../types/adminProjects.types";
import LinkProjectDialog from "./LinkProjectDialog";

interface LinkedProjectsPanelProps {
  developer: DeveloperSummary;
  linkedProjects: ProjectSummary[];
  allProjects: ProjectSummary[];
  loadingProjects: boolean;
  busyProjectId: string | null;
  onLink: (projectId: string) => Promise<void>;
  onUnlink: (projectId: string) => Promise<void>;
}

const LinkedProjectsPanel = ({
  developer,
  linkedProjects,
  allProjects,
  loadingProjects,
  busyProjectId,
  onLink,
  onUnlink,
}: LinkedProjectsPanelProps) => {
  const [linkDialogOpen, setLinkDialogOpen] = useState(false);
  const [linkLoading, setLinkLoading] = useState(false);

  const handleLink = async (projectId: string) => {
    setLinkLoading(true);
    try {
      await onLink(projectId);
      setLinkDialogOpen(false);
    } finally {
      setLinkLoading(false);
    }
  };

  return (
    <>
      <Paper
        variant="outlined"
        sx={{ borderRadius: 2, overflow: "hidden", borderColor: "divider" }}
      >
        {/* Developer header */}
        <Box
          sx={{
            px: 3,
            py: 2,
            bgcolor: "primary.dark",
            borderBottom: "1px solid",
            borderColor: "divider",
          }}
        >
          <Stack
            direction="row"
            spacing={2}
            sx={{ alignItems: "center", justifyContent: "space-between" }}
          >
            <Box>
              <Typography variant="h6" sx={{ lineHeight: 1.2 }}>
                {developer.username}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {developer.email}
              </Typography>
            </Box>
            <Stack direction="row" spacing={1} sx={{ alignItems: "center" }}>
              <Chip
                label={`${linkedProjects.length} proyecto${linkedProjects.length !== 1 ? "s" : ""}`}
                size="small"
                sx={{
                  bgcolor: alpha("#77ffc0", 0.1),
                  color: "info.main",
                  border: "1px solid",
                  borderColor: alpha("#77ffc0", 0.18),
                }}
              />
              <Button
                variant="contained"
                size="small"
                startIcon={<AddRounded />}
                onClick={() => setLinkDialogOpen(true)}
                disabled={loadingProjects}
              >
                Agregar Proyecto
              </Button>
            </Stack>
          </Stack>
        </Box>

        {/* Projects list */}
        <Box sx={{ p: 2 }}>
          {loadingProjects ? (
            <Stack sx={{ alignItems: "center", py: 4 }}>
              <CircularProgress size={32} />
            </Stack>
          ) : linkedProjects.length === 0 ? (
            <Box sx={{ textAlign: "center", py: 4 }}>
              <Typography variant="body1" color="text.secondary">
                Este desarrollador no está vinculado a ningún proyecto.
              </Typography>
            </Box>
          ) : (
            <Stack divider={<Divider />}>
              {linkedProjects.map((project) => {
                const isBusy = busyProjectId === project.projectId;
                return (
                  <Stack
                    key={project.projectId}
                    direction="row"
                    sx={{
                      alignItems: "center",
                      justifyContent: "space-between",
                      py: 1.5,
                      px: 1,
                    }}
                  >
                    <Typography variant="body1">{project.name}</Typography>
                    <Tooltip title="Desvincular">
                      <span>
                        <IconButton
                          size="small"
                          disabled={isBusy}
                          onClick={() => onUnlink(project.projectId)}
                          sx={{ color: "error.main" }}
                        >
                          {isBusy ? (
                            <CircularProgress size={18} color="inherit" />
                          ) : (
                            <LinkOffRounded fontSize="small" />
                          )}
                        </IconButton>
                      </span>
                    </Tooltip>
                  </Stack>
                );
              })}
            </Stack>
          )}
        </Box>
      </Paper>

      <LinkProjectDialog
        open={linkDialogOpen}
        allProjects={allProjects}
        linkedProjects={linkedProjects}
        loading={linkLoading}
        onClose={() => setLinkDialogOpen(false)}
        onLink={handleLink}
      />
    </>
  );
};

export default LinkedProjectsPanel;
