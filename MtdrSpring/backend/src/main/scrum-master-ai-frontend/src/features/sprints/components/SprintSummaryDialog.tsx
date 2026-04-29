import {
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import { alpha } from "@mui/material/styles";
import {
  CheckCircleRounded,
  HourglassBottomRounded,
  LockRounded,
  PlayArrowRounded,
  RadioButtonUncheckedRounded,
  WarningAmberRounded,
} from "@mui/icons-material";
import type { SprintSummaryData } from "../types/sprint.types";

interface SprintSummaryDialogProps {
  open: boolean;
  loading: boolean;
  summaryData: SprintSummaryData | null;
  onClose: () => void;
  onConfirmClose: () => Promise<void>;
  confirming?: boolean;
}

interface StatCardProps {
  label: string;
  value: number | string;
  color: string;
  icon: React.ReactNode;
}

const StatCard = ({ label, value, color, icon }: StatCardProps) => (
  <Box
    sx={{
      p: 2,
      borderRadius: 2,
      border: "1px solid",
      borderColor: color,
      bgcolor: alpha(color, 0.07),
      textAlign: "center",
    }}
  >
    <Stack
      direction="row"
      spacing={1}
      sx={{ alignItems: "center", justifyContent: "center", mb: 0.5 }}
    >
      <Box sx={{ color, display: "flex" }}>{icon}</Box>
      <Typography variant="h5" sx={{ color, fontWeight: 700 }}>
        {value}
      </Typography>
    </Stack>
    <Typography variant="caption" color="text.secondary">
      {label}
    </Typography>
  </Box>
);

const SprintSummaryDialog = ({
  open,
  loading,
  summaryData,
  onClose,
  onConfirmClose,
  confirming = false,
}: SprintSummaryDialogProps) => {
  const completionRate =
    summaryData && summaryData.totalTasks > 0
      ? Math.round((summaryData.completedTasks / summaryData.totalTasks) * 100)
      : 0;

  return (
    <Dialog
      open={open}
      onClose={loading || confirming ? undefined : onClose}
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle sx={{ p: 2, bgcolor: "primary.dark" }}>
        <Stack direction="row" spacing={1.5} sx={{ alignItems: "center" }}>
          <LockRounded />
          <Typography variant="h6">Cerrar Sprint</Typography>
        </Stack>
      </DialogTitle>

      <DialogContent dividers>
        {loading ? (
          <Stack
            sx={{
              alignItems: "center",
              justifyContent: "center",
              py: 6,
              gap: 2,
            }}
          >
            <CircularProgress />
            <Typography color="text.secondary">
              Cargando resumen del sprint…
            </Typography>
          </Stack>
        ) : summaryData ? (
          <Stack spacing={3}>
            <Box>
              <Typography variant="h6" gutterBottom>
                {summaryData.sprintName}
              </Typography>
              <Typography color="text.secondary" variant="body2">
                Revisa el resumen de resultados antes de confirmar el cierre del
                sprint.
              </Typography>
            </Box>

            <Divider />

            <Grid container spacing={1.5}>
              <Grid size={{ xs: 6, sm: 4 }}>
                <StatCard
                  label="Completadas"
                  value={summaryData.completedTasks}
                  color="#4ef770"
                  icon={<CheckCircleRounded fontSize="small" />}
                />
              </Grid>
              <Grid size={{ xs: 6, sm: 4 }}>
                <StatCard
                  label="En progreso"
                  value={summaryData.inProgressTasks}
                  color="#1eb1ce"
                  icon={<PlayArrowRounded fontSize="small" />}
                />
              </Grid>
              <Grid size={{ xs: 6, sm: 4 }}>
                <StatCard
                  label="En revisión"
                  value={summaryData.reviewTasks}
                  color="#FFD166"
                  icon={<HourglassBottomRounded fontSize="small" />}
                />
              </Grid>
              <Grid size={{ xs: 6, sm: 4 }}>
                <StatCard
                  label="Bloqueadas"
                  value={summaryData.blockedTasks}
                  color="#FF5C8A"
                  icon={<WarningAmberRounded fontSize="small" />}
                />
              </Grid>
              <Grid size={{ xs: 6, sm: 4 }}>
                <StatCard
                  label="Pendientes"
                  value={summaryData.toDoTasks}
                  color="#616161"
                  icon={<RadioButtonUncheckedRounded fontSize="small" />}
                />
              </Grid>
              <Grid size={{ xs: 6, sm: 4 }}>
                <StatCard
                  label="Completado"
                  value={`${completionRate}%`}
                  color={
                    completionRate >= 80
                      ? "#1dbb4a"
                      : completionRate >= 50
                        ? "#D6A63A"
                        : "#D93A67"
                  }
                  icon={<CheckCircleRounded fontSize="small" />}
                />
              </Grid>
            </Grid>

            <Divider />

            <Stack
              direction="row"
              spacing={4}
              sx={{ justifyContent: "center" }}
            >
              <Box sx={{ textAlign: "center" }}>
                <Typography variant="h6" sx={{ fontWeight: 700 }}>
                  {summaryData.totalEstimatedHours}h
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Horas estimadas
                </Typography>
              </Box>
              {summaryData.totalActualHours !== null && (
                <Box sx={{ textAlign: "center" }}>
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>
                    {summaryData.totalActualHours.toFixed(1)}h
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    Horas reales
                  </Typography>
                </Box>
              )}
            </Stack>

            <Box
              sx={{
                p: 1.5,
                borderRadius: 2,
                bgcolor: alpha("#D93A67", 0.07),
                border: "1px solid",
                borderColor: alpha("#FF5C8A", 0.3),
              }}
            >
              <Typography
                variant="caption"
                color="error"
              >
                <strong>Atención:</strong> Esta acción es irreversible. El
                sprint pasará a estado <strong>Cerrado</strong> y no podrá ser
                reabierto.
              </Typography>
            </Box>
          </Stack>
        ) : (
          <Typography color="text.secondary">
            No se pudo cargar el resumen del sprint.
          </Typography>
        )}
      </DialogContent>

      <DialogActions sx={{ p: 2 }}>
        <Button 
          onClick={onClose}
          disabled={loading || confirming}
          color="info"
          variant="outlined"
        >
          Cancelar
        </Button>
        <Button
          variant="contained"
          color="error"
          onClick={onConfirmClose}
          disabled={loading || confirming || !summaryData}
          startIcon={
            confirming ? <CircularProgress size={16} /> : <LockRounded />
          }
        >
          Confirmar Cierre
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default SprintSummaryDialog;
