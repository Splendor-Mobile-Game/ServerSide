package com.github.splendor_mobile_game.game.enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Regex {

    UUID_PATTERN("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"),
    USERNAME_PATTERN("^(?=.*\\p{L})[\\p{L}\\p{N}\\s]+$"),
    PASSWORD_PATTERN("^[a-zA-Z0-9ąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\p{Punct}]+$"),
    ENTER_CODE_PATTERN("^([0-9a-zA-Z]+){6}$");

    private final String pattern;

    Regex(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public boolean matches(String string) {
        Pattern pattern = Pattern.compile(getPattern());
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }
}
