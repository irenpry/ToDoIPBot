package ru.pryadkina.bot.todoipbot.controllers;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pryadkina.bot.todoipbot.model.ReplyMessage;
import ru.pryadkina.bot.todoipbot.service.TaskService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TaskController extends AController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    public SendMessage task(Update update) {
        long chatId = update.getMessage().getChatId();
        taskService.save(update.getMessage().getChatId(),parseMessageToMap(update.getMessage().getText()));
        ReplyMessage replyMessage = new ReplyMessage(EmojiParser.parseToUnicode("Задача добавлена :bee:"));
        return buildSendMessage(chatId, replyMessage);
    }

    public SendMessage getTasks(Update update) {
        long chatId = update.getMessage().getChatId();
        Map<String,String> params = new HashMap<>();
        if (Arrays.stream(update.getMessage().getText().split(" ",2)).count() > 1) {
            params.put("listId",update.getMessage().getText().split(" ",2)[1]);
        }
        ReplyMessage replyMessage = new ReplyMessage(taskService.get(update.getMessage().getChatId(),params));
        return buildSendMessage(chatId, replyMessage);
    }

    public SendMessage done(Update update) {
        long chatId = update.getMessage().getChatId();
        if (Arrays.stream(update.getMessage().getText().split(" ",2)).count() > 1) {
            String taskId = update.getMessage().getText().split(" ",2)[1];
            String mes = taskService.done(update.getMessage().getChatId(),Integer.parseInt(taskId));
            ReplyMessage replyMessage = new ReplyMessage(mes);
            return buildSendMessage(chatId, replyMessage);
        }
        ReplyMessage replyMessage = new ReplyMessage(EmojiParser.parseToUnicode("Укажите id задачи"));
        return buildSendMessage(chatId, replyMessage);
    }
}
