package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.repository.UsersRepository;
import com.springboot.MyTodoList.repository.TaskAssignmentsRepository;
import com.springboot.MyTodoList.repository.ProjectMembersRepository;
import com.springboot.MyTodoList.repository.ProjectsRepository;
import com.springboot.MyTodoList.repository.SprintsRepository;
import com.springboot.MyTodoList.repository.TasksRepository;
import com.springboot.MyTodoList.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class BotActionBase implements BotAction {

    protected UsersRepository usersRepository;
    protected TasksService tasksService;
    protected TaskAssignmentsRepository taskAssignmentsRepository;
    protected SprintsRepository sprintsRepository;
    protected TasksRepository tasksRepository;
    protected ProjectsRepository projectsRepository;
    protected ProjectMembersRepository projectMembersRepository;

    @Autowired
    public void setServices(
            UsersRepository usersRepository,
            TasksService tasksService,
            TaskAssignmentsRepository taskAssignmentsRepository,
            SprintsRepository sprintsRepository,
            TasksRepository tasksRepository,
            ProjectsRepository projectsRepository,
            ProjectMembersRepository projectMembersRepository
        ) {
        this.usersRepository = usersRepository;
        this.tasksService = tasksService;
        this.taskAssignmentsRepository = taskAssignmentsRepository;
        this.sprintsRepository = sprintsRepository;
        this.tasksRepository = tasksRepository;
        this.projectsRepository = projectsRepository;
        this.projectMembersRepository = projectMembersRepository;
    }
}