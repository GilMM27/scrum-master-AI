import { Box, Paper, Stack, Typography } from "@mui/material";
import type { AlertLevel, KpiCardData } from "../types/dashboard.types";

const alertBorderColor: Record<AlertLevel, string> = {
  green: "#4ef770",
  yellow: "#FFD166",
  red: "#FF5C8A",
  neutral: "#b4bdc7",
};

const alertBgColor: Record<AlertLevel, string> = {
  green: "rgba(78,247,112,0.06)",
  yellow: "rgba(255,209,102,0.06)",
  red: "rgba(255,92,138,0.06)",
  neutral: "transparent",
};

interface KpiCardProps {
  data: KpiCardData;
}

const KpiCard = ({ data }: KpiCardProps) => {
  const { label, value, sublabel, alert, Icon } = data;
  const borderColor = alertBorderColor[alert];
  const bgColor = alertBgColor[alert];
  const isUnavailable = value === "No disponible";

  return (
    <Paper
      sx={{
        p: 2.5,
        borderLeft: `4px solid ${borderColor}`,
        bgcolor: bgColor,
        height: "100%",
        display: "flex",
        flexDirection: "column",
        justifyContent: "space-between",
        transition: "border 0.2s",
        "&:hover": {
          borderColor: borderColor,
        },
      }}
    >
      <Stack direction="row" spacing={1} sx={{ alignItems: "center", mb: 1.5 }}>
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            width: 32,
            height: 32,
            borderRadius: 1.5,
            bgcolor: `${borderColor}22`,
          }}
        >
          <Icon sx={{ fontSize: 18, color: borderColor }} />
        </Box>
        <Typography
          variant="caption"
          color="text.secondary"
          sx={{
            fontWeight: 600,
            textTransform: "uppercase",
            letterSpacing: 0.5,
          }}
        >
          {label}
        </Typography>
      </Stack>

      <Typography
        variant="h4"
        sx={{
          fontWeight: 700,
          color: isUnavailable ? "text.disabled" : "text.primary",
          fontSize: isUnavailable ? "1rem !important" : undefined,
          lineHeight: 1.2,
          mb: 0.5,
        }}
      >
        {value}
      </Typography>

      {sublabel && (
        <Typography variant="caption" color="text.secondary">
          {sublabel}
        </Typography>
      )}
    </Paper>
  );
};

export default KpiCard;
