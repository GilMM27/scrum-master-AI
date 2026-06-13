import { useState } from "react";
import { alpha, Autocomplete, Button, CircularProgress, Dialog, DialogActions, DialogContent,DialogTitle, IconButton, Stack, TextField, Typography } from "@mui/material";
import { CloseRounded } from "@mui/icons-material";
import type { ProjectSummary } from "../types/adminProjects.types";

interface LinkProjectDialogProps {
  open: boolean;
  allProjects: ProjectSummary[];
  linkedProjects: ProjectSummary[];
  loading?: boolean;
  onClose: () => void;
  onLink: (projectId: string) => Promise<void>;
}

const LinkProjectDialog = ({
  open,
  allProjects,
  linkedProjects,
  loading,
  onClose,
  onLink,
}: LinkProjectDialogProps) => {
  const [selectedProject, setSelectedProject] = useState<ProjectSummary | null>(
    null,
  );

  const availableProjects = allProjects.filter(
    (p) => !linkedProjects.some((lp) => lp.projectId === p.projectId),
  );

  const handleConfirm = async () => {
    if (!selectedProject) return;
    await onLink(selectedProject.projectId);
    setSelectedProject(null);
  };

  const handleClose = () => {
    setSelectedProject(null);
    onClose();
  };

  return (
    <Dialog
      open={open}
      onClose={loading ? undefined : handleClose}
      maxWidth="xs"
      fullWidth
    >
      <Stack
        direction="row"
        sx={{
          alignItems: "center",
          justifyContent: "space-between",
          bgcolor: "primary.dark",
          px: 2,
          py: 1,
        }}
      >
        <DialogTitle>Agregar Proyecto</DialogTitle>
        <IconButton
          onClick={handleClose}
          size="small"
          disabled={loading}
          sx={{ mr: 2, "&:hover": { color: alpha("#b4bdc7", 0.8) } }}
        >
          <CloseRounded />
        </IconButton>
      </Stack>

      <DialogContent dividers>
        {availableProjects.length === 0 ? (
          <Typography variant="body2" color="text.secondary">
            El desarrollador ya está vinculado a todos los proyectos
            disponibles.
          </Typography>
        ) : (
          <Autocomplete
            options={availableProjects}
            value={selectedProject}
            getOptionLabel={(option) => option.name}
            isOptionEqualToValue={(option, val) =>
              option.projectId === val.projectId
            }
            onChange={(_, newValue) => setSelectedProject(newValue)}
            renderInput={(params) => (
              <TextField
                {...params}
                label="Seleccionar proyecto"
                placeholder="Buscar proyecto..."
              />
            )}
            noOptionsText="No hay proyectos disponibles."
          />
        )}
      </DialogContent>

      <DialogActions sx={{ px: 3, py: 2 }}>
        <Button onClick={handleClose} disabled={loading} variant="outlined" color="error">
          Cancelar
        </Button>
        <Button
          onClick={handleConfirm}
          variant="outlined"
          color="info"
          disabled={!selectedProject || loading}
          startIcon={loading ? <CircularProgress size={16} /> : undefined}
        >
          Vincular
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default LinkProjectDialog;
