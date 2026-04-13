import type { UserRole } from "../../types/Role.types";
import { NAVIGATION_BY_ROLE } from "./navigation.config";


export const normalizePath = (path: string): string => {
    if (!path.startsWith('/')) return `/${path}`; 
    return path;
};

export const toTitleCase = (value: string): string =>
    value
        .replace(/-/g, ' ')
        .replace(/\b\w/g, (char) => char.toUpperCase());

export const getNavItemsByRole = (role: UserRole) => {
    return NAVIGATION_BY_ROLE[role].map((item) => ({
        ...item,
        path: normalizePath(item.path),
    }));
};

const ROLE_ROOT: Record<UserRole, string> = {
    ADMIN: '/admin',
    MANAGER: '/manager',
    DEVELOPER: '/developer',
};

export const getBreadcrumbsFromPath = (pathname: string, role: UserRole) => {
    const navItems = getNavItemsByRole(role);
    const cleanPath = pathname.replace(/\/+$/, '');
    const roleRoot = ROLE_ROOT[role];

    const segments = cleanPath.split('/').filter(Boolean);

    const crumbs = segments.map((segment, index) => {
        const path = `/${segments.slice(0, index + 1).join('/')}`;
        const navMatch = navItems.find((item) => item.path === path);

        return {
            label: navMatch?.label || toTitleCase(segment),
            path,
            isLast: index === segments.length - 1
        };
    });

    // Remove the role-root segment (e.g. /admin, /manager, /developer)
    // — it has no matching route and should not appear as a clickable crumb.
    return crumbs.filter((crumb) => crumb.path !== roleRoot);
};