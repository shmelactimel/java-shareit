package ru.practicum.shareit.booking.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingMapperJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSerializeBooking() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        // Добавьте другие поля booking, если необходимо

        String json = objectMapper.writeValueAsString(booking);
        assertThat(json).contains("\"id\":1");
        // Дополнительные проверки JSON
    }

    @Test
    public void testDeserializeBooking() throws Exception {
        String json = "{\"id\":1,\"start\":\"2023-01-01T12:00:00\",\"end\":\"2023-01-02T12:00:00\"}";
        Booking booking = objectMapper.readValue(json, Booking.class);

        assertThat(booking.getId()).isEqualTo(1L);
        assertThat(booking.getStart()).isEqualTo(LocalDateTime.of(2023, 1, 1, 12, 0));
        assertThat(booking.getEnd()).isEqualTo(LocalDateTime.of(2023, 1, 2, 12, 0));
    }

    @Test
    public void testDeserializeBookingShort() throws Exception {
        String json = "{\"bookerId\":1,\"itemId\":2}";
        BookingShort bookingShort = objectMapper.readValue(json, BookingShort.class);

        assertThat(bookingShort.getBookerId()).isEqualTo(1L);
        assertThat(bookingShort.getItemId()).isEqualTo(2L);
    }
}
