import { Box, CircularProgress, Paper, Stack, Typography } from "@mui/material";
import { ArrowDropDown, ArrowDropUp, ArrowRight } from "@mui/icons-material";
import type { AlertLevel, KpiCardData } from "../types/dashboard.types";

const alertColor: Record<AlertLevel, string> = {
  green: "#4ef770",
  yellow: "#FFD166",
  red: "#FF5C8A",
  neutral: "#b4bdc7",
};

const trendMeta: Record<
  AlertLevel,
  { Icon: React.ElementType; text: string }
> = {
  green: { Icon: ArrowDropUp, text: "En buen camino" },
  yellow: { Icon: ArrowDropDown, text: "Requiere atención" },
  red: { Icon: ArrowDropDown, text: "Necesita acción" },
  neutral: { Icon: ArrowRight, text: "Sin datos" },
};

interface KpiCardProps {
  data: KpiCardData;
}

const RING_SIZE = 100;
const RING_THICKNESS = 1;

const KpiCard = ({ data }: KpiCardProps) => {
  const { label, value, sublabel, alert, Icon, progress = 0, trend, accentColor } = data;
  const color = alertColor[alert];
  const isUnavailable = value === "No disponible";
  const { Icon: TrendIcon, text: defaultTrendText } = trendMeta[alert];
  const trendText = trend ?? defaultTrendText;

  return (
    <Paper
      sx={{
        border: `1px solid ${accentColor}55`,
        bgcolor: `${accentColor}0d`,
        borderRadius: 3,
        height: "100%",
        display: "flex",
        flexDirection: "column",
        overflow: "hidden",
        transition: "border-color 0.2s, box-shadow 0.2s",
        "&:hover": {
          borderColor: `${accentColor}aa`,
          boxShadow: `0 0 16px ${accentColor}22`,
        },
      }}
    >
      {/* ── Top section ── */}
      <Stack
        direction="row"
        sx={{
          p: 2.5,
          pb: 1.5,
          justifyContent: "space-between",
          alignItems: "flex-start",
          flexGrow: 1,
        }}
      >
        {/* Left column — icon + title + metric + sublabel */}
        <Stack sx={{ flexGrow: 1, pr: 2 }}>
          {/* Icon badge + title */}
          <Stack direction="row" spacing={1.5} sx={{ alignItems: "center", mb: 2 }}>
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                width: 32,
                height: 32,
                borderRadius: "50%",
                border: `1px solid ${accentColor}`,
                bgcolor: `${accentColor}18`,
                flexShrink: 0,
              }}
            >
              <Icon sx={{ fontSize: 16, color: accentColor }} />
            </Box>
            <Typography
              variant="caption"
              sx={{
                fontWeight: 700,
                textTransform: "uppercase",
                letterSpacing: 0.8,
                color: "text.secondary",
                lineHeight: 1.3,
              }}
            >
              {label}
            </Typography>
          </Stack>

          {/* Main metric */}
          <Typography
            variant="h4"
            sx={{
              fontWeight: 700,
              color: isUnavailable ? "text.disabled" : "text.primary",
              fontSize: isUnavailable ? "1rem !important" : { xs: "1.6rem", sm: "1.9rem" },
              lineHeight: 1.15,
              mb: 0.75,
            }}
          >
            {value}
          </Typography>

          {/* Supporting text */}
          {sublabel && (
            <Typography
              variant="caption"
              color="text.secondary"
              sx={{ lineHeight: 1.4 }}
            >
              {sublabel}
            </Typography>
          )}
        </Stack>

        {/* Right column — circular progress ring */}
        <Box
          sx={{
            position: "relative",
            width: RING_SIZE,
            height: RING_SIZE,
            flexShrink: 0,
          }}
        >
          {/* Track (background circle) */}
          <CircularProgress
            variant="determinate"
            value={100}
            size={RING_SIZE}
            thickness={RING_THICKNESS}
            sx={{
              position: "absolute",
              top: 0,
              left: 0,
              color: `${color}1a`,
            }}
          />
          {/* Filled arc */}
          <CircularProgress
            variant="determinate"
            value={progress}
            size={RING_SIZE}
            thickness={RING_THICKNESS}
            sx={{
              position: "absolute",
              top: 0,
              left: 0,
              color,
              "& .MuiCircularProgress-circle": {
                strokeLinecap: "round",
                transition: "stroke-dashoffset 0.6s ease",
              },
            }}
          />
          {/* Center label */}
          <Box
            sx={{
              position: "absolute",
              inset: 0,
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              justifyContent: "center",
              px: 0.5,
            }}
          >
            <Typography
              sx={{
                fontWeight: 700,
                fontSize: isUnavailable ? "0.6rem" : "0.82rem",
                color: isUnavailable ? "text.disabled" : "text.primary",
                textAlign: "center",
                lineHeight: 1.2,
                fontFamily: "'Space Grotesk', sans-serif",
              }}
            >
              {value}
            </Typography>
          </Box>
        </Box>
      </Stack>

      {/* ── Trend indicator strip ── */}
      <Box
        sx={{
          px: 2.5,
          py: 1.25,
          bgcolor: `${color}0d`,
          borderTop: `1px solid ${color}33`,
        }}
      >
        <Stack direction="row" spacing={1} sx={{ alignItems: "center" }}>
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              width: 26,
              height: 26,
              flexShrink: 0,
            }}
          >
            <TrendIcon sx={{ fontSize: 26, color }} />
          </Box>
          <Typography
            variant="caption"
            sx={{ fontWeight: 600, color, lineHeight: 1 }}
          >
            {trendText}
          </Typography>
        </Stack>
      </Box>
    </Paper>
  );
};

export default KpiCard;
