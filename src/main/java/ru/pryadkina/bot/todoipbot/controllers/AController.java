package ru.pryadkina.bot.todoipbot.controllers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pryadkina.bot.todoipbot.model.ReplyMessage;

import java.util.HashMap;
import java.util.Map;

public abstract class AController {

    protected SendMessage buildSendMessage(long chatId, ReplyMessage replyMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        if (replyMessage.getMessage() != null) {
            sendMessage.setText(replyMessage.getMessage());
        }

        if (replyMessage.getButtons() != null) {
            sendMessage.setReplyMarkup(InlineKeyboardMarkup.builder().keyboard(replyMessage.getButtons()).build());
        }

        return sendMessage;
    }

    protected Map<String,String> parseMessageToMap(String message) {
        Map<String,String> params = new HashMap<>();
        String[] commandAndMessageAndParams = message.split(" ",2);
        if (commandAndMessageAndParams.length > 1) {
            String[] messageAndParams = commandAndMessageAndParams[1].split("/", 2);
            params.put("message", messageAndParams[0]);
            if (messageAndParams.length > 1) {
                for (String s : messageAndParams[1].split(" ")) {
                    if (!s.equals(" ") && !s.equals("")) {
                        String paramName = s.split("=",2)[0];
                        String paramValue = s.split("=",2)[1];
                        params.put(paramName,paramValue);
                    }
                }
            }
        }
        return params;
    }

}
