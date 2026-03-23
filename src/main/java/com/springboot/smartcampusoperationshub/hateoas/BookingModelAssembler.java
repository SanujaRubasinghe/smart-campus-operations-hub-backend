package com.springboot.smartcampusoperationshub.hateoas;

import com.springboot.smartcampusoperationshub.model.Booking;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class BookingModelAssembler implements RepresentationModelAssembler<Booking, BookingModel> {

    @Override
    public BookingModel toModel(Booking entity) {
        return null;
    }
}
