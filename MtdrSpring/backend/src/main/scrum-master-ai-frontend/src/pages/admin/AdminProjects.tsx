import { useCallback, useEffect, useRef, useState } from "react";
import { Box, CircularProgress, Stack, Typography } from "@mui/material";
import DashboardLayout from "../../layouts/DashboardLayout";
import DeveloperSearchAutocomplete from "../../features/admin-projects/components/DeveloperSearchAutocomplete";
import LinkedProjectsPanel from "../../features/admin-projects/components/LinkedProjectsPanel";
import { getAllDevelopers, getAllProjects, getDeveloperProjects, linkDeveloperToProject, unlinkDeveloperFromProject } from "../../features/admin-projects/services/adminProjects.service";
import type { DeveloperSummary, ProjectSummary } from "../../features/admin-projects/types/adminProjects.types";
import useNotification from "../../hooks/useNotification";

const AdminProjectsContent = () => {
  const [developers, setDevelopers] = useState<DeveloperSummary[]>([]);
  const [allProjects, setAllProjects] = useState<ProjectSummary[]>([]);
  const [loadingInitial, setLoadingInitial] = useState(true);

  const [selectedDeveloper, setSelectedDeveloper] =
    useState<DeveloperSummary | null>(null);
  const [developerProjects, setDeveloperProjects] = useState<ProjectSummary[]>(
    [],
  );
  const [loadingProjects, setLoadingProjects] = useState(false);
  const [busyProjectId, setBusyProjectId] = useState<string | null>(null);

  const { showSuccess, showError } = useNotification();

  // Track the latest selected developer to avoid stale async updates
  const selectedDevRef = useRef<DeveloperSummary | null>(null);

  useEffect(() => {
    const fetchInitial = async () => {
      try {
        setLoadingInitial(true);
        const [devs, projects] = await Promise.all([
          getAllDevelopers(),
          getAllProjects(),
        ]);
        setDevelopers(devs);
        setAllProjects(projects);
      } catch (error) {
        showError(
          error instanceof Error ? error.message : "Error al cargar datos.",
        );
      } finally {
        setLoadingInitial(false);
      }
    };
    void fetchInitial();
  }, [showError]);

  const fetchDeveloperProjects = useCallback(
    async (developer: DeveloperSummary) => {
      setLoadingProjects(true);
      setDeveloperProjects([]);
      selectedDevRef.current = developer;
      try {
        const projects = await getDeveloperProjects(developer.userId);
        // Discard if the selected developer changed while fetching
        if (selectedDevRef.current?.userId === developer.userId) {
          setDeveloperProjects(projects);
        }
      } catch (error) {
        showError(
          error instanceof Error
            ? error.message
            : "Error al cargar proyectos del desarrollador.",
        );
      } finally {
        if (selectedDevRef.current?.userId === developer.userId) {
          setLoadingProjects(false);
        }
      }
    },
    [showError],
  );

  const handleSelectDeveloper = (developer: DeveloperSummary | null) => {
    setSelectedDeveloper(developer);
    setDeveloperProjects([]);
    if (developer) {
      void fetchDeveloperProjects(developer);
    }
  };

  const handleLink = async (projectId: string) => {
    if (!selectedDeveloper) return;
    try {
      await linkDeveloperToProject(projectId, selectedDeveloper.userId);
      const linked = allProjects.find((p) => p.projectId === projectId);
      if (linked) {
        setDeveloperProjects((prev) =>
          [...prev, linked].sort((a, b) => a.name.localeCompare(b.name)),
        );
      }
      showSuccess("Desarrollador vinculado al proyecto exitosamente.");
    } catch (error) {
      showError(
        error instanceof Error
          ? error.message
          : "No fue posible vincular al desarrollador.",
      );
      throw error; // let dialog keep its loading state
    }
  };

  const handleUnlink = async (projectId: string) => {
    if (!selectedDeveloper) return;
    try {
      setBusyProjectId(projectId);
      await unlinkDeveloperFromProject(projectId, selectedDeveloper.userId);
      setDeveloperProjects((prev) =>
        prev.filter((p) => p.projectId !== projectId),
      );
      showSuccess("Desarrollador desvinculado del proyecto.");
    } catch (error) {
      showError(
        error instanceof Error
          ? error.message
          : "No fue posible desvincular al desarrollador.",
      );
    } finally {
      setBusyProjectId(null);
    }
  };

  return (
    <Stack spacing={3}>
      <Box>
        <Typography variant="h4" gutterBottom>
          Gestión de Proyectos
        </Typography>
        <Typography variant="body1" color="textSecondary">
          Busca un desarrollador y gestiona los proyectos a los que tiene
          acceso.
        </Typography>
      </Box>

      {loadingInitial ? (
        <Stack sx={{ alignItems: "center", py: 8 }}>
          <CircularProgress />
        </Stack>
      ) : (
        <>
          <DeveloperSearchAutocomplete
            developers={developers}
            loading={loadingInitial}
            value={selectedDeveloper}
            onSelect={handleSelectDeveloper}
          />

          {selectedDeveloper && (
            <LinkedProjectsPanel
              developer={selectedDeveloper}
              linkedProjects={developerProjects}
              allProjects={allProjects}
              loadingProjects={loadingProjects}
              busyProjectId={busyProjectId}
              onLink={handleLink}
              onUnlink={handleUnlink}
            />
          )}

          {!selectedDeveloper && (
            <Box
              sx={{
                textAlign: "center",
                py: 8,
                border: "1px dashed",
                borderColor: "divider",
                borderRadius: 2,
              }}
            >
              <Typography variant="body1" color="text.secondary">
                Selecciona un desarrollador para ver y gestionar sus proyectos.
              </Typography>
            </Box>
          )}
        </>
      )}
    </Stack>
  );
};

const AdminProjects = () => (
  <DashboardLayout>
    <AdminProjectsContent />
  </DashboardLayout>
);

export default AdminProjects;
