import { createContext, useCallback, useEffect, useMemo, useRef, useState, type ReactNode } from 'react';
import type { ProjectSelectorItem } from '../types/Project.types';
import { getMyProjectsSelector } from '../features/projects/services/project.service';
import useAuth from '../hooks/useAuth';

interface ProjectContextValue {
    projects: ProjectSelectorItem[];
    selectedProjectId: string | null;
    selectedProject: ProjectSelectorItem | null;
    setSelectedProjectId: (id: string | null) => void;
    refreshProjects: () => Promise<void>;
    loading: boolean;
}

export const ProjectContext = createContext<ProjectContextValue | undefined>(undefined);

interface ProjectProviderProps {
    children: ReactNode;
}

const ProjectProvider = ({ children }: ProjectProviderProps) => {
    const { isAuthenticated } = useAuth();
    const [projects, setProjects] = useState<ProjectSelectorItem[]>([]);
    const [selectedProjectId, setSelectedProjectId] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const fetchProjects = useCallback(async () => {
        if (!isAuthenticated) return;
        setLoading(true);
        try {
            const data = await getMyProjectsSelector();
            setProjects(data);
            if (data.length > 0) {
                setSelectedProjectId((prev) => (prev === null ? data[0].projectId : prev));
            }
        } catch (error) {
            console.error('[Projects] Failed to fetch projects selector:', error);
        } finally {
            setLoading(false);
        }
    }, [isAuthenticated]);

    useEffect(() => {
        fetchProjects();
    }, [fetchProjects]);

    const renderCount = useRef(0);
    renderCount.current += 1;

    const value = useMemo(() => {
        const selectedProject = projects.find((p) => p.projectId === selectedProjectId) ?? null;
        return { projects, selectedProjectId, selectedProject, setSelectedProjectId, refreshProjects: fetchProjects, loading };
    }, [projects, selectedProjectId, fetchProjects, loading]);

    return <ProjectContext.Provider value={value}>{children}</ProjectContext.Provider>;
};

export default ProjectProvider;
