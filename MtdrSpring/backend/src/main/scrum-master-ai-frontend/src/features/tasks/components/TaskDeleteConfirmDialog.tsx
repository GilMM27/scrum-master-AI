import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from "@mui/material";

interface TaskDeleteConfirmDialogProps {
  open: boolean;
  taskTitle?: string;
  loading?: boolean;
  onClose: () => void;
  onConfirm: () => Promise<void> | void;
}

const TaskDeleteConfirmDialog = ({ open, taskTitle, loading = false, onClose, onConfirm }: TaskDeleteConfirmDialogProps) => {
  return (
    <Dialog open={open} onClose={loading ? undefined : onClose} maxWidth="xs" fullWidth>
      <DialogTitle>Eliminar tarea</DialogTitle>
        <DialogContent>
          <Typography>
            ¿Estás seguro de que deseas eliminar la tarea <strong>{taskTitle || 'seleccionada'}</strong>?
          </Typography>
          <Typography color="error.main" sx={{ mt: 1 }}>
            Esta acción no se puede deshacer.
          </Typography>
        </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={loading} variant="outlined">
          Cancelar
        </Button>
        <Button onClick={onConfirm} disabled={loading} variant="contained" color="error">
          Eliminar
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export default TaskDeleteConfirmDialog