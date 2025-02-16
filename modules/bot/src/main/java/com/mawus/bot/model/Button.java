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
    MY_TRIPS("Мои рейсы"),
    ENTER_CITY_DEPARTURE("Город отправления"),
    ENTER_CITY_ARRIVAL("Город прибытия"),
    ENTER_TRIP_DATE("Дата отправления"),
    CANCEL("Отмена"),
    SKIP("Пропустить"),
    CONFIRM("Подтвердить"),
    COMPANIONS("Найти попутчиков");

    private final String alias;

    Button(String alias) {
        this.alias = alias;
    }

    public static ReplyKeyboardMarkup createGeneralMenuKeyboard() {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder keyboardBuilder = ReplyKeyboardMarkup.builder();
        keyboardBuilder.resizeKeyboard(true);
        keyboardBuilder.selective(true);
        keyboardBuilder.isPersistent(true);
        keyboardBuilder.keyboardRow(new KeyboardRow(Arrays.asList(
                KeyboardButton.builder().text(MY_TRIPS.getAlias()).build()
        )));
        keyboardBuilder.keyboardRow(new KeyboardRow(Arrays.asList(
                KeyboardButton.builder().text(ADD_TRIP.getAlias()).build()
        )));
//        keyboardBuilder.keyboardRow(new KeyboardRow(Arrays.asList(
//                KeyboardButton.builder().text(COMPANIONS.getAlias()).build()
//        )));

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

    public static KeyboardButton createButton(String text) {
        return KeyboardButton.builder().text(text).build();
    }

    public String getAlias() {
        return alias;
    }
}
