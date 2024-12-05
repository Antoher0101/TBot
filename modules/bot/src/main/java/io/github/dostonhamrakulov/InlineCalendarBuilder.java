package io.github.dostonhamrakulov;

import io.github.dostonhamrakulov.localization.LanguageEnum;
import io.github.dostonhamrakulov.localization.TranslationEN;
import io.github.dostonhamrakulov.localization.TranslationRU;
import io.github.dostonhamrakulov.utils.DateTimeUtil;
import io.github.dostonhamrakulov.utils.InlineCalendarCommandUtil;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Inline calendar builder
 *
 * @author Doston Hamrakulov
 */
public class InlineCalendarBuilder {

    private final LanguageEnum languageCode;
    /**
     * Week days: D, T, W, etc.
     */
    private String[] weekDays;
    /**
     * Translated month names
     */
    private Map<Month, String> months;
    /**
     * Flag to show the full month name
     */
    private boolean showFullMonthName;
    private String customPrefix = "";

    private LocalDate minDate;
    private LocalDate maxDate;

    public InlineCalendarBuilder() {
        this.languageCode = LanguageEnum.RU;
    }

    public InlineCalendarBuilder(LanguageEnum languageCode) {
        this.languageCode = languageCode;
        this.showFullMonthName = false;
    }

    public synchronized InlineKeyboardMarkup build(final Update update) {

        LocalDate dateForCalendar = InlineCalendarCommandUtil.extractNavigationDate(update);

        if (dateForCalendar == null) {
            dateForCalendar = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);
        }

        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        final List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        // adding weeks in one row
        for (final String weekDay : getWeekDays()) {
            final InlineKeyboardButton in = new InlineKeyboardButton();
            in.setText(weekDay);
            in.setCallbackData(customPrefix + InlineCalendarCommandUtil.CALENDAR_COMMAND_PREFIX + InlineCalendarCommandUtil.CALENDAR_COMMAND_IGNORE);
            inlineKeyboardButtons.add(in);
        }

        rows.add(inlineKeyboardButtons);

        inlineKeyboardButtons = new ArrayList<>();

        int weekDaysCounter = LocalDate.of(dateForCalendar.getYear(), dateForCalendar.getMonth(), 1).getDayOfWeek().getValue() - 1;

        // adding empty buttons
        for (int i = 0; i < weekDaysCounter; i++) {
            final InlineKeyboardButton in = new InlineKeyboardButton();
            in.setText(" ");
            in.setCallbackData(customPrefix + InlineCalendarCommandUtil.CALENDAR_COMMAND_PREFIX + InlineCalendarCommandUtil.CALENDAR_COMMAND_IGNORE);
            inlineKeyboardButtons.add(in);
        }

        final int daysOfCurrentMonth = YearMonth.of(dateForCalendar.getYear(), dateForCalendar.getMonth()).lengthOfMonth();
        int remainingEmptyDays = 0;

        for (int i = 1; i <= daysOfCurrentMonth; i++) {
            final InlineKeyboardButton in = new InlineKeyboardButton();
            String text = String.valueOf(i);
            String command = customPrefix + InlineCalendarCommandUtil.CALENDAR_COMMAND_PREFIX + InlineCalendarCommandUtil.CALENDAR_COMMAND_DATE + DateTimeUtil.convertToString(LocalDate.of(dateForCalendar.getYear(), dateForCalendar.getMonth(), i));

            LocalDate currentDay = LocalDate.of(dateForCalendar.getYear(), dateForCalendar.getMonth(), i);
            if ((minDate != null && currentDay.isBefore(minDate)) || (maxDate != null && currentDay.isAfter(maxDate))) {
                text = " ";
                command = customPrefix + InlineCalendarCommandUtil.CALENDAR_COMMAND_PREFIX + InlineCalendarCommandUtil.CALENDAR_COMMAND_IGNORE;
            }
            in.setText(text);
            in.setCallbackData(command);
            inlineKeyboardButtons.add(in);
            weekDaysCounter += 1;

            if (weekDaysCounter == 7) {
                rows.add(inlineKeyboardButtons);

                inlineKeyboardButtons = new ArrayList<>();
                weekDaysCounter = 0;
            } else if (i == daysOfCurrentMonth) {
                remainingEmptyDays = 7 - weekDaysCounter;
            }
        }

        // adding empty buttons
        for (int i = 0; i < remainingEmptyDays; i++) {
            final InlineKeyboardButton in = new InlineKeyboardButton();
            in.setText(" ");
            in.setCallbackData(customPrefix + InlineCalendarCommandUtil.CALENDAR_COMMAND_PREFIX + InlineCalendarCommandUtil.CALENDAR_COMMAND_IGNORE);
            inlineKeyboardButtons.add(in);
        }

        if (remainingEmptyDays > 0) {
            rows.add(inlineKeyboardButtons);
        }

        inlineKeyboardButtons = new ArrayList<>();

        InlineKeyboardButton in = new InlineKeyboardButton();
        in.setText("<<");
        in.setCallbackData(customPrefix + InlineCalendarCommandUtil.CALENDAR_COMMAND_PREFIX + InlineCalendarCommandUtil.CALENDAR_COMMAND_NAVIGATION + DateTimeUtil.convertToString(dateForCalendar.minusMonths(1)));
        inlineKeyboardButtons.add(in);

        in = new InlineKeyboardButton();

        if (showFullMonthName) {
            in.setText(this.getMonths().get(dateForCalendar.getMonth()) + ", " + dateForCalendar.getYear());
        } else {
            in.setText(this.getMonths().get(dateForCalendar.getMonth()).substring(0, 3) + ", " + dateForCalendar.getYear());
        }

        in.setCallbackData(customPrefix + InlineCalendarCommandUtil.CALENDAR_COMMAND_PREFIX + InlineCalendarCommandUtil.CALENDAR_COMMAND_IGNORE + dateForCalendar.getMonth().name());
        inlineKeyboardButtons.add(in);

        in = new InlineKeyboardButton();
        in.setText(">>");
        in.setCallbackData(customPrefix + InlineCalendarCommandUtil.CALENDAR_COMMAND_PREFIX + InlineCalendarCommandUtil.CALENDAR_COMMAND_NAVIGATION + DateTimeUtil.convertToString(dateForCalendar.plusMonths(1)));
        inlineKeyboardButtons.add(in);
        rows.add(inlineKeyboardButtons);
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    public String[] getWeekDays() {
        if (LanguageEnum.RU == this.languageCode) {
            return TranslationRU.weekDays;
        }

        if (LanguageEnum.EN == this.languageCode) {
            return TranslationEN.weekDays;
        }
        return new String[]{};
    }

    public Map<Month, String> getMonths() {
        if (LanguageEnum.RU == this.languageCode) {
            return TranslationRU.getMonths();
        }

        if (LanguageEnum.EN == this.languageCode) {
            return TranslationEN.getMonths();
        }

        return new HashMap<>();
    }

    public InlineCalendarBuilder showFullMonthName(boolean showFullMonthName) {
        this.showFullMonthName = showFullMonthName;
        return this;
    }

    public InlineCalendarBuilder customPrefix(String customPrefix) {
        this.customPrefix = customPrefix + ":";
        return this;
    }

    public InlineCalendarBuilder setMinDate(LocalDate minDate) {
        this.minDate = minDate;
        return this;
    }

    public InlineCalendarBuilder setMaxDate(LocalDate maxDate) {
        this.maxDate = maxDate;
        return this;
    }
}
