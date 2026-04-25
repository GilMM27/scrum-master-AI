import { useState } from "react";
import { Alert, Button, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField } from "@mui/material";
import { alpha } from "@mui/material/styles";
import { AddRounded } from "@mui/icons-material";
import { createProject } from "../../features/projects/services/project.service";
import useProject from "../../hooks/useProject";
import useNotification from "../../hooks/useNotification";

interface CreateProjectDialogProps {
  open: boolean;
  onClose: () => void;
}

const CreateProjectDialog = ({ open, onClose }: CreateProjectDialogProps) => {
  const { refreshProjects, setSelectedProjectId } = useProject();
  const { showSuccess, showError } = useNotification();
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [loading, setLoading] = useState(false);
  const [inlineError, setInlineError] = useState<string | null>(null);

  const handleClose = () => {
    setName("");
    setDescription("");
    setInlineError(null);
    onClose();
  };

  const handleSubmit = async () => {
    if (!name.trim()) {
      setInlineError("El nombre del proyecto es requerido.");
      return;
    }
    setLoading(true);
    setInlineError(null);
    try {
      const created = await createProject({
        name: name.trim(),
        description: description.trim(),
      });
      await refreshProjects();
      setSelectedProjectId(created.projectId);
      handleClose();
      showSuccess("Proyecto creado exitosamente.");
    } catch {
      showError("No se pudo crear el proyecto. Inténtalo de nuevo.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="xs" fullWidth>
      <DialogTitle sx={{ p: 2, bgcolor: "primary.dark" }}>
        Nuevo Proyecto
      </DialogTitle>

      <DialogContent dividers>
        <Stack spacing={3}>
          <TextField
            label="Nombre del Proyecto"
            value={name}
            onChange={(e) => setName(e.target.value)}
            fullWidth
            size="small"
            autoFocus
          />
          <TextField
            label="Descripción del Proyecto"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            fullWidth
            size="small"
            multiline
            rows={3}
          />

          {inlineError && <Alert severity="error">{inlineError}</Alert>}

        </Stack>
      </DialogContent>

      <DialogActions sx={{ px: 3, py: 2, gap: 2 }}>
        <Button
          onClick={handleClose}
          disabled={loading}
          color="inherit"
          variant="outlined"
          size="small"
          sx={{
            borderRadius: 1.5,
            borderColor: "error.main",
            color: "error.main",
            bgcolor: (theme) => alpha(theme.palette.error.main, 0.08),
            "&:hover": {
              bgcolor: (theme) => alpha(theme.palette.error.main, 0.16),
              borderColor: "error.main",
            },
            "& .MuiButton-startIcon": {
              color: "error.main",
            },
          }}
        >
          Cancelar
        </Button>
        <Button
          onClick={handleSubmit}
          disabled={loading}
          variant="outlined"
          size="small"
          endIcon={<AddRounded />}
        >
          {loading ? "Creando..." : "Crear Proyecto"}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default CreateProjectDialog;
