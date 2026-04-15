package com.springboot.MyTodoList.util;


public enum BotMessages {
	
	HELLO_MYTODO_BOT(
	"👋 Welcome to MyTodoList Bot!\n" + //
	"            Please validate your account to continue.\n" + //
	"            Click the button below to enter your phone number."),
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
