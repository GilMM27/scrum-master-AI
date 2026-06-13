export interface DeveloperSummary {
  userId: string;
  username: string;
  email: string;
  accountStatus: 'ACTIVE' | 'INACTIVE';
}

export interface ProjectSummary {
  projectId: string;
  name: string;
}
