package com.springboot.MyTodoList.util;


public enum BotMessages {
	
	HELLO_MYTODO_BOT(
		"👋 *¡Bienvenido a Scrum Master AI!*\n\n"
            + "Soy tu asistente de Scrum. Esto es lo que puedo hacer por ti:\n\n"
            + "*Comandos Disponibles:*\n\n"
            + "• "+BotCommands.START_COMMAND.getCommand()+" - Mostrar este mensaje de bienvenida\n"
            + "• "+BotCommands.LOGIN.getCommand()+"  - Vincular tu ID de Telegram con tu cuenta\n"
            + "• "+BotCommands.TASK_UPDATE.getCommand()+" - Gestionar tus tareas\n"
            + "• "+BotCommands.GENERATE_TASK.getCommand()+" - Crear una nueva tarea (Proyecto, Título, Descripción, Prioridad, Tiempo)\n"
            + "• "+BotCommands.ASSIGN_TASK.getCommand()+" - Asignar tareas a Sprints o Usuarios\n"
            + "• "+BotCommands.KPI.getCommand()+" - Ver los KPIs de un proyecto\n\n"
            + "_Nota: Usa "+BotCommands.LOGIN.getCommand()+" primero para vincular tu cuenta y acceder a todas las funciones._"),
	BOT_REGISTERED_STARTED("¡Bot registrado e iniciado con éxito!"),
	ITEM_DONE("¡Tarea completada! Selecciona /todolist para volver a la lista de tareas, o /start para ir a la pantalla principal."), 
	ITEM_UNDONE("¡Tarea marcada como pendiente! Selecciona /todolist para volver a la lista de tareas, o /start para ir a la pantalla principal."), 
	ITEM_DELETED("¡Tarea eliminada! Selecciona /todolist para volver a la lista de tareas, o /start para ir a la pantalla principal."),
	TYPE_NEW_TODO_ITEM("Escribe una nueva tarea abajo y presiona el botón de enviar (flecha azul) a la derecha."),
	NEW_ITEM_ADDED("¡Nueva tarea añadida! Selecciona /todolist para volver a la lista de tareas, o /start para ir a la pantalla principal."),
	BYE("¡Adiós! ¡Selecciona /start para reanudar!"),
	UNAUTHORIZED("Lo siento, parece que no has iniciado sesión. Por favor, usa el comando "+BotCommands.LOGIN.getCommand()+" para vincular este ID de Telegram con tu cuenta."),
	COMMAND_NOT_FOUND("Lo siento, no reconozco ese comando.");

	private String message;

	BotMessages(String enumMessage) {
		this.message = enumMessage;
	}

	public String getMessage() {
		return message;
	}

}
