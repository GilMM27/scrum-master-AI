import { Box, Paper, Stack, Typography } from "@mui/material";
import TimerRounded from "@mui/icons-material/TimerRounded";
import {
  ComposedChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ReferenceLine,
  ResponsiveContainer,
} from "recharts";
import type { HistogramBucket } from "../types/dashboard.types";

interface CycleTimeChartProps {
  data: HistogramBucket[];
  mean: number | null;
}

const CustomTooltip = ({
  active,
  payload,
  label,
}: {
  active?: boolean;
  payload?: { value: number }[];
  label?: number;
}) => {
  if (!active || !payload?.length) return null;
  return (
    <Paper sx={{ p: 1.5, minWidth: 140 }}>
      <Typography variant="caption" color="text.secondary">
        {label} {label === 1 ? "día" : "días"}
      </Typography>
      <Typography variant="body2" sx={{ color: "#4ef770", fontWeight: 600 }}>
        {payload[0].value} {payload[0].value === 1 ? "tarea" : "tareas"}
      </Typography>
    </Paper>
  );
};

const CycleTimeChart = ({ data, mean }: CycleTimeChartProps) => {
  const hasData = data.length > 0;
  const minDay = hasData ? data[0].days : 0;
  const maxDay = hasData ? data[data.length - 1].days : 0;

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
            bgcolor: "rgba(78,247,112,0.12)",
          }}
        >
          <TimerRounded sx={{ fontSize: 20, color: "success.main" }} />
        </Box>
        <Box>
          <Typography variant="h6">Cycle Time</Typography>
          <Typography variant="caption" color="text.secondary">
            Número de tareas por días desde inicio hasta entrega (tareas
            completadas).
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
            No disponible — se necesitan tareas completadas con fecha de inicio
            registrada.
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
              type="number"
              dataKey="days"
              domain={[minDay - 1, maxDay + 1]}
              allowDecimals={false}
              tick={{ fill: "#b4bdc7", fontSize: 11 }}
              axisLine={false}
              tickLine={false}
              label={{
                value: "Días",
                position: "insideBottomRight",
                offset: -4,
                fill: "#b4bdc7",
                fontSize: 11,
              }}
            />
            <YAxis
              allowDecimals={false}
              tick={{ fill: "#b4bdc7", fontSize: 11 }}
              axisLine={false}
              tickLine={false}
              label={{
                value: "Tareas",
                angle: -90,
                position: "insideLeftCenter",
                fill: "#b4bdc7",
                fontSize: 11,
              }}
            />
            <Tooltip content={<CustomTooltip />} />
            <Bar
              dataKey="count"
              name="Tareas"
              fill="#4ef770"
              radius={[3, 3, 0, 0]}
              maxBarSize={40}
            />
            {mean !== null && (
              <ReferenceLine
                x={Math.round(mean)}
                stroke="#FF5C8A"
                strokeDasharray="4 2"
                label={{
                  value: `50% ${mean.toFixed(1)}d`,
                  fill: "#FF5C8A",
                  fontSize: 10,
                  position: "top",
                }}
              />
            )}
          </ComposedChart>
        </ResponsiveContainer>
      )}
    </Paper>
  );
};

export default CycleTimeChart;
