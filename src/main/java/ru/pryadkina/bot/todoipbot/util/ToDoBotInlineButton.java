package ru.pryadkina.bot.todoipbot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public enum ToDoBotInlineButton {
    NEW_TASK("Создать задачу","task.new"),
    NEW_LIST("Создать список","list.new"),
    LISTS("Получить список задач","tasks.get");

    private String textButton;
    private String dataButton;

    ToDoBotInlineButton(String textButton, String dataButton) {
        this.textButton = textButton;
        this.dataButton = dataButton;
    }

    public InlineKeyboardButton convert() {
        return this.convert(null);
    }

    public InlineKeyboardButton convert(String payload) {
        InlineKeyboardButton convertedButton = new InlineKeyboardButton();
        convertedButton.setText(this.textButton);
        if (payload == null) {
            convertedButton.setCallbackData(this.dataButton);
        } else {
            convertedButton.setCallbackData(this.dataButton + ":" + payload);
        }
        return convertedButton;
    }

}
