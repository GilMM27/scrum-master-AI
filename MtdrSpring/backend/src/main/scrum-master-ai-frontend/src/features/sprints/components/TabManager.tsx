import { Box, Tab, Tabs } from "@mui/material";
import { TableRowsRounded, ViewKanbanRounded } from "@mui/icons-material";

export type SprintTab = "board" | "table";

interface TabManagerProps {
  activeTab: SprintTab;
  onChange: (tab: SprintTab) => void;
}

const TabManager = ({ activeTab, onChange }: TabManagerProps) => (
  <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
    <Tabs
      value={activeTab}
      onChange={(_, v: SprintTab) => onChange(v)}
      aria-label="Sprint view tabs"
      textColor="secondary"
      indicatorColor="secondary"
    >
      <Tab
        value="board"
        label="Tablero"
        icon={<ViewKanbanRounded fontSize="small" />}
        iconPosition="start"
      />
      <Tab
        value="table"
        label="Lista"
        icon={<TableRowsRounded fontSize="small" />}
        iconPosition="start"
      />
    </Tabs>
  </Box>
);

export default TabManager;
