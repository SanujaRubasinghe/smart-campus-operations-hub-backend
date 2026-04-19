package com.springboot.smartcampusoperationshub.hateoas.bookings;

import com.springboot.smartcampusoperationshub.controller.BookingController;
import com.springboot.smartcampusoperationshub.model.Booking;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class BookingModelAssembler
    extends RepresentationModelAssemblerSupport<Booking, BookingModel> {

    public BookingModelAssembler() {
        super(BookingController.class, BookingModel.class);
    }

    @Override
    public BookingModel toModel(Booking booking) {
        BookingModel model = new BookingModel();

        model.setId(booking.getId());
        model.setResourceId(booking.getResource().getId());
        model.setResourceName(booking.getResource().getName());
        model.setResourceType(booking.getResource().getType());
        model.setUserId(booking.getUser().getId());
        model.setBookedByUsername(booking.getUser().getName());
        model.setBookingDate(booking.getBookingDate());
        model.setStartTime(booking.getStartTime());
        model.setEndTime(booking.getEndTime());
        model.setPurpose(booking.getPurpose());
        model.setExpectedAttendees(booking.getExpectedAttendees());
        model.setStatus(booking.getStatus());
        model.setAdminNote(booking.getAdminNote());
        model.setCreatedAt(booking.getCreatedAt());
        model.setUpdatedAt(booking.getUpdatedAt());

        if (booking.getReviewedBy() != null) {
            model.setReviewedByUsername(booking.getReviewedBy().getName());
        }

        // Self link - always present in the response
        model.add(linkTo(methodOn(BookingController.class)
                .getBookingById(booking.getId()))
                .withSelfRel());

        // PENDING - the booking can be approved or rejected
//        if (booking.getStatus() == BookingStatus.PENDING) {
//            model.add(linkTo(methodOn(BookingController.class)
//                    .approveBooking(booking.getId(), null))
//                    .withRel("approve"));
//
//            model.add(linkTo(methodOn(BookingController.class)
//                    .rejectBooking(booking.getId(), null))
//                    .withRel("reject"));
//        }
//
//        // APPROVED - approved bookings can be cancelled
//        if (booking.getStatus() == BookingStatus.APPROVED) {
//            model.add(linkTo(methodOn(BookingController.class)
//                    .cancelBooking(booking.getId()))
//                    .withRel("cancel"));
//        }
//
//        model.add(linkTo(methodOn(BookingController.class)
//                .getAllBookings(null, null, null, null, null))
//                .withRel("all-bookings"));

        return model;
    }
}