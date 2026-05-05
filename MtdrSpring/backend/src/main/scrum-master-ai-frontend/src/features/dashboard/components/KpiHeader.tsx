import { Box, IconButton, Stack, Tooltip, Typography } from "@mui/material";
import RefreshRounded from "@mui/icons-material/RefreshRounded";

interface KpiHeaderProps {
  onRefresh: () => void;
  loading?: boolean;
}

const KpiHeader = ({ onRefresh, loading = false }: KpiHeaderProps) => {
  return (
    <Stack
      direction={{ xs: "column", md: "row" }}
      spacing={2}
      sx={{
        justifyContent: "space-between",
        alignItems: { xs: "stretch", md: "flex-start" },
      }}
    >
      <Box>
        <Typography variant="h4" gutterBottom>
          KPIs y Dashboard
        </Typography>
        <Typography color="text.secondary">
          Visualiza el estado del proyecto y los sprints en tiempo real. Descubre métricas clave para tomar decisiones informadas y mejorar continuamente.
        </Typography>
      </Box>
      <Box>
        <Tooltip title="Actualizar datos" placement="bottom">
          <span>
            <IconButton
              onClick={onRefresh}
              disabled={loading}
              sx={{
                border: "1px solid",
                borderColor: "divider",
                borderRadius: 2,
                color: "info.main",
                "&:hover": {
                  borderColor: "info.main",
                  bgcolor: "rgba(30,177,206,0.08)",
                },
              }}
            >
              <RefreshRounded />
            </IconButton>
          </span>
        </Tooltip>
      </Box>
    </Stack>
  );
};

export default KpiHeader;
