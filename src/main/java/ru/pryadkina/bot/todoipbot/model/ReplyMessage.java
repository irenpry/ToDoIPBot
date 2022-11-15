package ru.pryadkina.bot.todoipbot.model;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class ReplyMessage {

    private String message;

    private List<List<InlineKeyboardButton>> inlineButtons;

    public ReplyMessage() {
    }

    public ReplyMessage(String message) {
        this.message = message;
    }

    public ReplyMessage(String message, List<List<InlineKeyboardButton>> buttons) {
        this.message = message;
        this.inlineButtons = buttons;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<List<InlineKeyboardButton>> getButtons() {
        return inlineButtons;
    }

    public void setButtons(List<List<InlineKeyboardButton>> buttons) {
        this.inlineButtons = buttons;
    }

    public void addRow(List<InlineKeyboardButton> newRow) {
        if (this.getButtons() == null) {
            this.setButtons(new ArrayList<>());
        }
        this.inlineButtons.add(newRow);
    }
}
