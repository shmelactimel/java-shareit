package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
public class BookingDto {
    @NotNull
    LocalDate start;
    @NotNull
    LocalDate end;
    @NotNull
    Long item;
    @NotNull
    Long booker;
    @NotNull
    Status status = Status.WAITING;
}