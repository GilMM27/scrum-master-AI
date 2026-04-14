import { useEffect, useState } from "react";
import * as taskService from "../features/auth/services/tasks.service";
export const useTasks = () => {
  const [tasks, setTasks] = useState([]);

  const loadTasks = async () => {
    const data = await taskService.getTasks();
    setTasks(data);
  };

  const addTask = async (task: any) => {
    await taskService.createTask(task);
    loadTasks();
  };

  const changeStatus = async (id: number, status: string) => {
    await taskService.updateTaskStatus(id, status);
    loadTasks();
  };

  const removeTask = async (id: number) => {
    await taskService.deleteTask(id);
    loadTasks();
  };

  useEffect(() => {
    loadTasks();
  }, []);

  return {
    tasks,
    addTask,
    changeStatus,
    removeTask,
  };
};