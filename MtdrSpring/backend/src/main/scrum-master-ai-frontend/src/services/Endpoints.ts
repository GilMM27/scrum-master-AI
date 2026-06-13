export const API_ENDPOINTS = {
  auth: {
    login: "/auth/login",
    register: "/auth/register",
  },
  users: {
    base: "/api/users",
    byId: (userId: string) => `/api/users/${userId}`,
    updateRole: (userId: string) => `/api/users/${userId}/role`,
    updateAuthorization: (userId: string) =>
      `/api/users/${userId}/authorization`,
    createManagedUser: "/api/users",
    developers: "/api/users/developers",
    projectsByUser: (userId: string) => `/api/users/${userId}/projects`,
  },
  projects: {
    base: "/api/projects",
    all: "/api/projects",
    mySelector: "/api/projects/my/selector",
    developers: (projectId: string) => `/api/projects/${projectId}/developers`,
    addMember: (projectId: string, userId: string) =>
      `/api/projects/${projectId}/members/${userId}`,
    removeMember: (projectId: string, userId: string) =>
      `/api/projects/${projectId}/members/${userId}`,
  },
  tasks: {
    byProject: (projectId: string) => `/api/tasks/project/${projectId}`,
    bySprint: (sprintId: string) => `/api/tasks/sprint/${sprintId}`,
    byId: (taskId: string) => `/api/tasks/${taskId}`,
    create: "/api/tasks",
    update: (taskId: string) => `/api/tasks/${taskId}`,
    updateStatus: (taskId: string) => `/api/tasks/${taskId}/status`,
    delete: (taskId: string) => `/api/tasks/${taskId}`,
  },
  sprints: {
    available: (projectId: string) =>
      `/api/sprints/project/${projectId}/available`,
    byProject: (projectId: string) => `/api/sprints/project/${projectId}`,
    create: "/api/sprints",
    update: (sprintId: string) => `/api/sprints/${sprintId}`,
    summary: (sprintId: string) => `/api/sprints/${sprintId}/summary`,
  },
  analytics: {
    byProject: (projectId: string) => `/api/analytics/project/${projectId}`,
    bySprint: (sprintId: string) => `/api/analytics/sprint/${sprintId}`,
  },
};
