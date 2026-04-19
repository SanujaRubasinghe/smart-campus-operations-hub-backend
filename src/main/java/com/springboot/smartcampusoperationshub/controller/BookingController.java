package com.springboot.smartcampusoperationshub.controller;

import com.springboot.smartcampusoperationshub.dto.bookings.ApproveBookingDTO;
import com.springboot.smartcampusoperationshub.dto.bookings.BookingRequestDTO;
import com.springboot.smartcampusoperationshub.dto.bookings.RejectBookingDTO;
import com.springboot.smartcampusoperationshub.hateoas.bookings.BookingModel;
import com.springboot.smartcampusoperationshub.hateoas.bookings.BookingModelAssembler;
import com.springboot.smartcampusoperationshub.model.Booking;
import com.springboot.smartcampusoperationshub.model.enums.BookingStatus;
import com.springboot.smartcampusoperationshub.security.UserPrincipal;
import com.springboot.smartcampusoperationshub.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;


@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final BookingModelAssembler assembler;
    private final PagedResourcesAssembler<Booking> pagedAssembler;

    public BookingController(BookingService bookingService,
                             BookingModelAssembler assembler,
                             PagedResourcesAssembler<Booking> pagedAssembler) {
        this.bookingService = bookingService;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @PostMapping
    public ResponseEntity<BookingModel> createBooking(
            @Valid @RequestBody BookingRequestDTO dto,
            @RequestParam Long userId) {
        Booking booking = bookingService.createBooking(dto, userId);
        BookingModel model = assembler.toModel(booking);

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingModel> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(assembler.toModel(booking));
    }

    @GetMapping("/my")
    public ResponseEntity<PagedModel<BookingModel>> getMyBookings(
            @RequestParam Long userId,
            @RequestParam(required = false)BookingStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable) {
        Page<Booking> page = bookingService.getMyBookings(userId, status, pageable);
        return ResponseEntity.ok(pagedAssembler.toModel(page, assembler));
    }

    @GetMapping
    public ResponseEntity<PagedModel<BookingModel>> getAllBookings(
            @RequestParam(required = false)BookingStatus status,
            @RequestParam(required = false)UUID resourceId,
            @RequestParam(required = false)Long userId,
            @RequestParam(required = false)LocalDate date,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Booking> page = bookingService.getAllBookings(
                status, resourceId, userId, date, pageable
        );
        return ResponseEntity.ok(pagedAssembler.toModel(page, assembler));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<BookingModel> approveBooking(
            @PathVariable Long id,
            @RequestBody(required = false) ApproveBookingDTO dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal
            ) {
        if (dto == null) {
            dto = new ApproveBookingDTO();
        }
        Long adminId = userPrincipal != null ? userPrincipal.getId() : 1L;
        Booking booking = bookingService.approveBooking(id, dto, adminId);
        return ResponseEntity.ok(assembler.toModel(booking));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<BookingModel> rejectBooking(
            @PathVariable Long id,
            @RequestBody(required = false) RejectBookingDTO dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        if (dto == null) {
            dto = new RejectBookingDTO();
        }
        Long adminId = userPrincipal != null ? userPrincipal.getId() : 1L;
        Booking booking = bookingService.rejectBooking(id, dto, adminId);
        return ResponseEntity.ok(assembler.toModel(booking));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingModel> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal != null ? userPrincipal.getId() : 1L;
        Booking booking = bookingService.cancelBooking(id, userId);
        return ResponseEntity.ok(assembler.toModel(booking));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
