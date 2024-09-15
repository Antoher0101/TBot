package com.mawus.bot.model;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Arrays;

public enum Button {
    START("/start"),
    REGISTER("Зарегистрироваться"),
    ENTER_NAME("Ввести имя"),
    ENTER_PHONE("Ввести телефон"),
    ADD_TRIP("Добавить рейс"),
    CANCEL("Отмена"),
    SKIP("Пропустить"),
    ;

    private final String alias;

    Button(String alias) {
        this.alias = alias;
    }

    public static ReplyKeyboardMarkup createGeneralMenuKeyboard() {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder keyboardBuilder = ReplyKeyboardMarkup.builder();
        keyboardBuilder.resizeKeyboard(true);
        keyboardBuilder.selective(true);
        keyboardBuilder.keyboardRow(new KeyboardRow(Arrays.asList(
                KeyboardButton.builder().text(ADD_TRIP.getAlias()).build()
        )));

        return keyboardBuilder.build();
    }

    public static ReplyKeyboardMarkup createRegisterKeyboard() {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder keyboardBuilder = ReplyKeyboardMarkup.builder();
        keyboardBuilder.resizeKeyboard(true);
        keyboardBuilder.selective(true);
        keyboardBuilder.keyboardRow(new KeyboardRow(Arrays.asList(
                KeyboardButton.builder().text(REGISTER.getAlias()).build()
        )));

        return keyboardBuilder.build();
    }

    public String getAlias() {
        return alias;
    }
}
