package com.springboot.MyTodoList.util;

public enum BotCommands {

	START_COMMAND("/start"), 
	HIDE_COMMAND("/hide"), 
	TODO_LIST("/todolist"),
	ADD_ITEM("/additem"),
	LLM_REQ("/llm"),
	LOGIN("/login"),
	TASK_UPDATE("/utask"),
	GENERATE_TASK("/gtask"),
	ASSIGN_TASK("/assignTask"),
	KPI("/kpi"),
	EXIT_TRANSACTION("/exit");

	private String command;

	BotCommands(String enumCommand) {
		this.command = enumCommand;
	}

	public String getCommand() {
		return command;
	}
}
