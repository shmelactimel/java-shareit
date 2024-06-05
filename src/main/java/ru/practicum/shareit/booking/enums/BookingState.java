package ru.practicum.shareit.booking.enums;

import java.util.Arrays;
import java.util.stream.Collectors;
import ru.practicum.shareit.exception.ErrorMessages;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static BookingState parse(String name) {
        var isCorrect = Arrays.stream(values())
                .map(BookingState::name)
                .collect(Collectors.toSet())
                .contains(name);
        if (isCorrect) {
            return BookingState.valueOf(name);
        }
        throw new IllegalArgumentException(ErrorMessages.UNKNOWN_STATE.getFormatMessage(name));
    }
}
