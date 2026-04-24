package com.springboot.MyTodoList.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class BotHelper {

	private static final Logger logger = LoggerFactory.getLogger(BotHelper.class);
	
	private static TelegramClient telegramClient;
	
	public static void setTelegramClient(TelegramClient client) {
		telegramClient = client;
	}
	
	public static TelegramClient getTelegramClient() {
		return telegramClient;
	}

	public static void sendMessageToTelegram(Long chatId, String text, TelegramClient bot) {

		try {
			SendMessage messageToTelegram = 
					SendMessage
					.builder()
					.chatId(chatId)
					.text(text)
					.replyMarkup(new ReplyKeyboardRemove(true))
					.build()
				;

			bot.execute(messageToTelegram);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	public static void sendMessageToTelegram(Long chatId, String text, TelegramClient bot, ReplyKeyboardMarkup rk ) {

		try {
			SendMessage messageToTelegram = 
					SendMessage
					.builder()
					.chatId(chatId)
					.text(text)
					.replyMarkup(rk)
					.build()
				;

			bot.execute(messageToTelegram);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	public static void sendMessageWithInlineKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard, TelegramClient bot) {
		try {
			SendMessage messageToTelegram = SendMessage.builder()
					.chatId(chatId)
					.text(text)
					.replyMarkup(keyboard)
					.build();
			bot.execute(messageToTelegram);
			System.out.println("Intento de crear InlineKeyboard");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	public static void editMessageText(Long chatId, Integer messageId, String text, TelegramClient bot) {
		try {
			EditMessageText editMessage = EditMessageText.builder()
					.chatId(chatId)
					.messageId(messageId)
					.text(text)
					.build();
			bot.execute(editMessage);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	public static void editMessageTextWithKeyboard(Long chatId, Integer messageId, String text, 
			InlineKeyboardMarkup keyboard, TelegramClient bot) {
		try {
			EditMessageText editMessage = EditMessageText.builder()
					.chatId(chatId)
					.messageId(messageId)
					.text(text)
					.replyMarkup(keyboard)
					.build();
			bot.execute(editMessage);
		} catch (Exception e) {
			System.out.println("Cannot Send new Keyboard: "+ text);
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	public static void editMessageReplyMarkup(Long chatId, Integer messageId, InlineKeyboardMarkup keyboard, TelegramClient bot) {
		try {
			EditMessageReplyMarkup editMarkup = EditMessageReplyMarkup.builder()
					.chatId(chatId)
					.messageId(messageId)
					.replyMarkup(keyboard)
					.build();
			bot.execute(editMarkup);
			System.out.println("Cannot| Updated Send new Keyboard options ");
		} catch (Exception e) {
			System.out.println("Cannot Send new Keyboard options ");
			logger.error(e.getLocalizedMessage(), e);
		}
	}

}