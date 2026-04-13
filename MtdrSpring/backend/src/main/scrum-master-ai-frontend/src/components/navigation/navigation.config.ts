import SpaceDashboardRoundedIcon from '@mui/icons-material/SpaceDashboardRounded';
import { AreaChartRounded, AssignmentIndRounded, GroupRounded, SmartToyRounded, TableChartRounded, ViewKanbanRounded, type SvgIconComponent } from '@mui/icons-material';
import type { UserRole } from '../../types/Role.types';

export interface NavigationItem {
    label: string;
    path: string;
    icon: SvgIconComponent;
}

export const NAVIGATION_BY_ROLE: Record<UserRole, NavigationItem[]> = {
    DEVELOPER: [
        { label: 'Inicio', path: '/developer/home', icon: SpaceDashboardRoundedIcon },
        { label: 'Backlog', path: '/developer/backlog', icon: TableChartRounded },
        { label: 'Mis Tareas', path: '/developer/tasks', icon: ViewKanbanRounded },
        //{ label: 'Analytics', path: '/developer/analytics', icon: AreaChartRounded }
    ],
    MANAGER: [
        { label: 'Inicio', path: '/manager/home', icon: SpaceDashboardRoundedIcon },
        { label: 'Backlog', path: '/manager/backlog', icon: TableChartRounded },
        { label: 'Sprint Actual', path: '/manager/sprint', icon: ViewKanbanRounded },
        //{ label: 'KPIs y Dashboard', path: '/manager/kpis', icon: AreaChartRounded },
        //{ label: 'Reportes', path: '/manager/reports', icon: AssignmentIndRounded },
        //{ label: 'AI Scrum Master', path: '/manager/ai', icon: SmartToyRounded }
    ],
    ADMIN: [
        { label: 'Inicio', path: '/admin/home', icon: SpaceDashboardRoundedIcon },
        { label: 'Gestión de Usuarios', path: '/admin/users', icon: GroupRounded },
        //{ label: 'Gestión de Tareas', path: '/admin/tasks', icon: TableChartRounded },
        //{ label: "Gestión de Sprints", path: '/admin/projects', icon: ViewKanbanRounded },
        //{ label: "KPIs", path: '/admin/kpis', icon: AreaChartRounded },
        //{ label: "Reportes y Alertas", path: '/admin/reports', icon: AssignmentIndRounded },
        //{ label: "AI Scrum Master", path: '/admin/ai', icon: SmartToyRounded }
    ]
}