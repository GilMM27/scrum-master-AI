import AreaChartRounded from "@mui/icons-material/AreaChartRounded";
import { FolderSharedRounded,GroupRounded, TableChartRounded, ViewKanbanRounded, type SvgIconComponent } from "@mui/icons-material";
import type { UserRole } from "../../types/Role.types";

export interface NavigationItem {
  label: string;
  path: string;
  icon: SvgIconComponent;
}

export const NAVIGATION_BY_ROLE: Record<UserRole, NavigationItem[]> = {
  DEVELOPER: [
    { label: "Backlog", path: "/developer/backlog", icon: TableChartRounded },
    { label: "Mis Tareas", path: "/developer/tasks", icon: ViewKanbanRounded },
    //{ label: 'Analytics', path: '/developer/analytics', icon: AreaChartRounded }
  ],
  MANAGER: [
    { label: "Backlog", path: "/manager/backlog", icon: TableChartRounded },
    { label: "Sprints", path: "/manager/sprint", icon: ViewKanbanRounded },
    {
      label: "KPIs y Dashboard",
      path: "/manager/kpis",
      icon: AreaChartRounded,
    },
    //{ label: 'Reportes', path: '/manager/reports', icon: AssignmentIndRounded },
    //{ label: 'AI Scrum Master', path: '/manager/ai', icon: SmartToyRounded }
  ],
  ADMIN: [
    { label: "Gestión de Usuarios", path: "/admin/users", icon: GroupRounded },
    { label: "Gestión de Proyectos", path: "/admin/projects", icon: FolderSharedRounded },
    //{ label: 'Gestión de Tareas', path: '/admin/tasks', icon: TableChartRounded },
    //{ label: "Gestión de Sprints", path: '/admin/projects', icon: ViewKanbanRounded },
    //{ label: "KPIs", path: '/admin/kpis', icon: AreaChartRounded },
    //{ label: "Reportes y Alertas", path: '/admin/reports', icon: AssignmentIndRounded },
    //{ label: "AI Scrum Master", path: '/admin/ai', icon: SmartToyRounded }
  ],
};
