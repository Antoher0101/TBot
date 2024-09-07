package com.mawus.core.domain;

public class MessagePlaceholder {

    private final String placeholder;

    private final Object replacement;

    public MessagePlaceholder(String placeholder, Object replacement) {
        this.placeholder = placeholder;
        this.replacement = replacement;
    }

    public static MessagePlaceholder of(String placeholder, Object replacement) {
        return new MessagePlaceholder(placeholder, replacement);
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public Object getReplacement() {
        return replacement;
    }
}
