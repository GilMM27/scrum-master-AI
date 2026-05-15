package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StartActionTest {

    @InjectMocks
    private StartAction startAction;

    @Mock
    private TelegramClient telegramClient;

    @Mock
    private Update update;

    @Mock
    private Message message;

    private static final Long CHAT_ID = 12345678L;

    @BeforeEach
    void setUp() {
        BotHelper.setTelegramClient(telegramClient);
        lenient().when(update.getMessage()).thenReturn(message);
        lenient().when(message.getChatId()).thenReturn(CHAT_ID);
    }

    @Test
    void testGetState() {
        assertEquals(BotState.START, startAction.getState());
    }

    @Test
    void testCanHandle_CorrectCommand() {
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn(BotCommands.START_COMMAND.getCommand());
        
        assertTrue(startAction.canHandle(update));
    }

    @Test
    void testCanHandle_WrongCommand() {
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/wrong");
        
        assertFalse(startAction.canHandle(update));
    }

    @Test
    void testCanHandle_NoText() {
        when(message.hasText()).thenReturn(false);
        
        assertFalse(startAction.canHandle(update));
    }

    @Test
    void testHandle() throws Exception {
        BotState nextState = startAction.handle(update);
        
        assertEquals(BotState.IDLE, nextState);
        verify(telegramClient, times(1)).execute(any(SendMessage.class));
    }
}
