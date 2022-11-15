package ru.pryadkina.bot.todoipbot.controllers;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pryadkina.bot.todoipbot.model.ReplyMessage;
import ru.pryadkina.bot.todoipbot.util.ToDoBotInlineButton;
import ru.pryadkina.bot.todoipbot.util.ToDoBotCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class BotController extends AController {

    private final UserController userController;
    private final TaskController taskController;
    private final ListController listController;

    @Autowired
    public BotController(UserController userController, TaskController taskController, ListController listController) {
        this.userController = userController;
        this.taskController = taskController;
        this.listController = listController;
    }

    public SendMessage handle(ToDoBotCommand command, Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String payload = callbackQuery.getData();
            switch (payload.split(":",2)[0]) {
                case "task.new":
                    return menu(update);
                default:
                    return menu(update);
            }
        }
        if (command == null) {
            return menu(update);
        }
        switch (command) {
            case START:
                SendMessage sendMessage = start(update);
                return sendMessage;
            case REGISTER:
                return userController.register(update);
            case NEW_LIST:
                return listController.list(update);
            case ADD_PARTICIPANT:
                return listController.addParticipant(update);
            case DELETE_PARTICIPANT:
                return listController.deleteParticipant(update);
            case GET_LISTS:
                return listController.getLists(update);
            case NEW_TASK:
                return taskController.task(update);
            case GET_TASKS:
                return taskController.getTasks(update);
            case DONE:
                return taskController.done(update);
            case TEST:
                ReplyMessage replyMessage = new ReplyMessage(parseMessageToMap(update.getMessage().getText()).toString());
                return buildSendMessage(update.getMessage().getChatId(), replyMessage);
            default:
                return menu(update);
        }
    }

    public SendMessage start(Update update) {
        String name = update.getMessage().getChat().getFirstName();
        String message = EmojiParser.parseToUnicode("Привет, " + name + "! :blush:\n\nЭто бот для управления списками задач. \nТы можешь создавать списки, " +
                "добавлять других участников и создавать задачи для них.\n\n Для продолжения вызови /register");
        ReplyMessage replyMessage = new ReplyMessage(message);
        return buildSendMessage(update.getMessage().getChatId(), replyMessage);
    }


    public SendMessage menu(Update update) {
        long chatId = update.getMessage().getChatId();
        ReplyMessage replyMessage = new ReplyMessage();
        List<InlineKeyboardButton> menuRow1 = new ArrayList<>();
        List<InlineKeyboardButton> menuRow2 = new ArrayList<>();
        List<InlineKeyboardButton> menuRow3 = new ArrayList<>();
        menuRow1.add(ToDoBotInlineButton.NEW_TASK.convert());
        menuRow2.add(ToDoBotInlineButton.NEW_LIST.convert());
        menuRow3.add(ToDoBotInlineButton.LISTS.convert());
        replyMessage.addRow(menuRow1);
        replyMessage.addRow(menuRow2);
        replyMessage.addRow(menuRow3);
        return buildSendMessage(chatId, replyMessage);
    }

}
