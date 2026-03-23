package com.springboot.smartcampusoperationshub.hateoas.bookings;

import com.springboot.smartcampusoperationshub.model.bookings.Booking;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class BookingModelAssembler implements RepresentationModelAssembler<Booking, BookingModel> {

    @Override
    public BookingModel toModel(Booking entity) {
        return null;
    }
}
