import { Box, Paper, Stack, Typography } from "@mui/material";
import GroupsRounded from "@mui/icons-material/GroupsRounded";
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
} from "recharts";
import type { TasksDoneBySprintRow } from "../types/dashboard.types";

const DEV_COLORS = [
  "#1eb1ce",
  "#4ef770",
  "#FFD166",
  "#FF5C8A",
  "#a78bfa",
  "#fb923c",
  "#38bdf8",
  "#f472b6",
];

interface WipByDeveloperChartProps {
  data: TasksDoneBySprintRow[];
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
          {p.name}: {p.value}
        </Typography>
      ))}
    </Paper>
  );
};

const WipByDeveloperChart = ({ data }: WipByDeveloperChartProps) => {
  const hasData = data.length > 0;

  // Collect all developer names across all sprints
  const devNames = [
    ...new Set(data.flatMap((row) => Object.keys(row.tasksDoneByUser))),
  ];

  // Build flat recharts data: [{ sprintName: "S1", Alice: 3, Bob: 2 }, ...]
  const chartData = data.map((row) => ({
    sprintName: row.sprintName ?? row.sprintId,
    ...row.tasksDoneByUser,
  }));

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
            bgcolor: "rgba(255,193,7,0.12)",
          }}
        >
          <GroupsRounded sx={{ fontSize: 20, color: "warning.main" }} />
        </Box>
        <Box>
          <Typography variant="h6">
            Tareas Completadas por Desarrollador
          </Typography>
          <Typography variant="caption" color="text.secondary">
            Tareas marcadas como DONE por sprint y desarrollador del equipo.
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
            No disponible — no hay tareas completadas en el periodo
            seleccionado.
          </Typography>
        </Stack>
      ) : (
        <ResponsiveContainer width="100%" height={220}>
          <BarChart
            data={chartData}
            margin={{ top: 20, right: 16, left: 0, bottom: 10 }}
          >
            <CartesianGrid
              strokeDasharray="3 3"
              stroke="rgba(119,255,250,0.08)"
              vertical={false}
            />
            <XAxis
              dataKey="sprintName"
              tick={{ fill: "#b4bdc7", fontSize: 11 }}
              axisLine={false}
              tickLine={false}
              label={{
                value: "Sprints",
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
                value: "Tareas Completadas",
                position: "insideLeftCenter",
                angle: -90,
                fill: "#b4bdc7",
                fontSize: 11,
              }}
            />
            <Tooltip content={<CustomTooltip />} />
            <Legend wrapperStyle={{ fontSize: 12, color: "#b4bdc7" }} />
            {devNames.map((name, idx) => (
              <Bar
                key={name}
                dataKey={name}
                name={name}
                fill={DEV_COLORS[idx % DEV_COLORS.length]}
                radius={[4, 4, 0, 0]}
              />
            ))}
          </BarChart>
        </ResponsiveContainer>
      )}
    </Paper>
  );
};

export default WipByDeveloperChart;
