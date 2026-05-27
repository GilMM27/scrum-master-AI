import { Box, Paper, Stack, Typography } from "@mui/material";
import ShowChartRounded from "@mui/icons-material/ShowChartRounded";
import {
  ResponsiveContainer,
  ComposedChart,
  Area,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ReferenceLine,
} from "recharts";
import type { BurndownDataPoint } from "../types/dashboard.types";

interface BurndownChartProps {
  data: BurndownDataPoint[];
  sprintName?: string | null;
}

const CustomTooltip = ({
  active,
  payload,
  label,
}: {
  active?: boolean;
  payload?: { value: number; name: string; color: string }[];
  label?: string;
}) => {
  if (!active || !payload?.length) return null;
  return (
    <Paper sx={{ p: 1.5, minWidth: 180 }}>
      <Typography
        variant="caption"
        color="text.secondary"
        sx={{ display: "block", mb: 0.5 }}
      >
        {label}
      </Typography>
      {payload.map((p) => (
        <Typography
          key={p.name}
          variant="body2"
          sx={{ color: p.color, fontWeight: 600 }}
        >
          {p.name}: {typeof p.value === "number" ? p.value.toFixed(0) : "-"} pts
        </Typography>
      ))}
    </Paper>
  );
};

const BurndownChart = ({ data, sprintName }: BurndownChartProps) => {
  const hasData = data.length > 0;

  const isOnTrack = (() => {
    if (!hasData || data.length < 2) return true;
    const last = data[data.length - 1];
    return (last.remaining ?? Infinity) <= last.ideal;
  })();

  const actualColor = isOnTrack ? "#4ef770" : "#FF5C8A";

  return (
    <Paper sx={{ p: 3, height: "100%" }}>
      <Stack direction="row" spacing={1.5} sx={{ alignItems: "start", mb: 1 }}>
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            width: 36,
            height: 36,
            borderRadius: 2,
            bgcolor: "rgba(119,255,192,0.12)",
          }}
        >
          <ShowChartRounded sx={{ fontSize: 20, color: "info.main" }} />
        </Box>
        <Box>
          <Typography variant="h6">
            Burndown Chart{sprintName ? ` — ${sprintName}` : ""}
          </Typography>
          <Typography variant="caption" color="text.secondary">
            Story points restantes vs. la curva ideal de quema. El área roja
            indica que el proyecto va por detrás del plan.
          </Typography>
        </Box>
      </Stack>

      {!hasData ? (
        <Stack
          sx={{
            height: 220,
            alignItems: "center",
            justifyContent: "center",
            textAlign: "center",
          }}
        >
          <Typography color="text.disabled">
            No disponible — se necesitan sprints con fechas y story points
            asignados.
          </Typography>
        </Stack>
      ) : (
        <ResponsiveContainer width="100%" height={220}>
          <ComposedChart
            data={data}
            margin={{ top: 20, right: 16, left: 0, bottom: 10 }}
          >
            <CartesianGrid
              strokeDasharray="3 3"
              stroke="rgba(119,255,250,0.08)"
            />
            <XAxis
              dataKey="date"
              tick={{ fill: "#b4bdc7", fontSize: 10 }}
              axisLine={false}
              tickLine={false}
              label={{
                value: "Fecha",
                position: "insideBottomRight",
                offset: -4,
                fill: "#b4bdc7",
                fontSize: 11,
              }}

            />
            <YAxis
              tick={{ fill: "#b4bdc7", fontSize: 11 }}
              axisLine={false}
              tickLine={false}
              unit=" pts"
            />
            <Tooltip content={<CustomTooltip />} />
            <Legend wrapperStyle={{ fontSize: 12, color: "#F5F7FF" }} />
            <ReferenceLine y={0} stroke="rgba(119,255,250,0.2)" />
            <Line
              type="monotone"
              dataKey="ideal"
              name="Ideal"
              stroke="#F5F7FF"
              strokeWidth={1.5}
              strokeDasharray="5 3"
              dot={false}
            />
            <Area
              type="monotone"
              dataKey="remaining"
              name="Restante"
              stroke={actualColor}
              strokeWidth={2.5}
              fill={actualColor}
              fillOpacity={0.18}
              activeDot={{ r: 5 }}
              connectNulls
            />
          </ComposedChart>
        </ResponsiveContainer>
      )}
    </Paper>
  );
};

export default BurndownChart;
