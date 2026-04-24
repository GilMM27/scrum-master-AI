package com.springboot.MyTodoList.util;


public enum BotMessages {
	
	HELLO_MYTODO_BOT(
		"👋 *Welcome to Scrum Master AI!*\n\n"
            + "I'm your Scrum Assistant bot. Here's what I can do for you:\n\n"
            + "*Available Commands:*\n\n"
            + "• `"+BotCommands.START_COMMAND.getCommand()+"` - Show this welcome message\n"
            + "• `"+BotCommands.LOGIN.getCommand()+" ` - Link your Telegram ID with your account\n"
            + "• `"+BotCommands.TASK_UPDATE.getCommand()+"` - Manage your tasks\n"
            + "• `"+BotCommands.GENERATE_TASK.getCommand()+"` - Create a new task (Project, Title, Desc, Priority, Time)\n"
            + "• `"+BotCommands.ASSIGN_TASK.getCommand()+"` - Assign tasks to Sprints or Users\n\n"
            + "_Note: Use "+BotCommands.LOGIN.getCommand()+" first to link your account and access all the different functions._"),
	BOT_REGISTERED_STARTED("Bot registered and started succesfully!"),
	ITEM_DONE("Item done! Select /todolist to return to the list of todo items, or /start to go to the main screen."), 
	ITEM_UNDONE("Item undone! Select /todolist to return to the list of todo items, or /start to go to the main screen."), 
	ITEM_DELETED("Item deleted! Select /todolist to return to the list of todo items, or /start to go to the main screen."),
	TYPE_NEW_TODO_ITEM("Type a new todo item below and press the send button (blue arrow) on the rigth-hand side."),
	NEW_ITEM_ADDED("New item added! Select /todolist to return to the list of todo items, or /start to go to the main screen."),
	BYE("Bye! Select /start to resume!"),
	UNAUTHORIZED("Sorry, it seems you haven't log in, please use the command "+BotCommands.LOGIN.getCommand()+" to link this telegram-id with your account"),
	COMMAND_NOT_FOUND("Sorry, that dosent seem like any command I know");

	private String message;

	BotMessages(String enumMessage) {
		this.message = enumMessage;
	}

	public String getMessage() {
		return message;
	}

}
