package com.example.AirBnb.App.service;

import com.example.AirBnb.App.dto.BookingDto;
import com.example.AirBnb.App.dto.BookingRequest;
import com.example.AirBnb.App.dto.GuestDto;
import com.example.AirBnb.App.entities.*;
import com.example.AirBnb.App.entities.enums.BookingStatus;
import com.example.AirBnb.App.exception.ForbiddenException;
import com.example.AirBnb.App.exception.ResourceNotFoundException;
import com.example.AirBnb.App.repository.*;
import com.example.AirBnb.App.stratergy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final GuestRepository guestRepository;

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final CheckoutService checkoutService;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {

        if (!bookingRequest.getCheckoutDate().isAfter(bookingRequest.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        log.info("initializing booking for hotel:{},room:{},date {}---{}",
                bookingRequest.getHotelId(),bookingRequest.getRoomId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckoutDate());
        //Validate hotel exists
        Hotel hotel= hotelRepository
                .findById(bookingRequest.getHotelId())
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with id "+bookingRequest.getHotelId()));

        //Validate room exists
        Room room= roomRepository
                .findById(bookingRequest.getRoomId())
                .orElseThrow(()->new ResourceNotFoundException("Room not found with id "+bookingRequest.getHotelId()));

        //Ensure availability for date range
        List<Inventory> inventoryList=inventoryRepository.findAndLockAvailableInventory(room.getId(),bookingRequest.getCheckInDate(),
                bookingRequest.getCheckoutDate(),bookingRequest.getRoomCount());

        long dayCount= ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckoutDate())+1;
        if(inventoryList.size()!=dayCount){
            throw new IllegalStateException("Room is not available anymore");
        }

        //reserve the room/update the booked count

       inventoryRepository.initBooking(room.getId(),hotel.getId(),
               bookingRequest.getCheckInDate(),bookingRequest.getCheckoutDate(),
               bookingRequest.getRoomCount());

        //calculating the total price of requested room count
        BigDecimal priceForOneRoom= pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice=priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomCount()));



        //create the booking



        Booking booking=Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckoutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomCount())
                .amount(totalPrice)
                .build();

        booking=bookingRepository.save(booking);


        return modelMapper.map(booking,BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {

        log.info("Adding guest with booking id:{}",bookingId);

        Booking booking= bookingRepository.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException("Booking id not found"+bookingId));

        User user=getCurrentUser();

        if (!user.getId().equals(booking.getUser().getId())) {
            throw new ForbiddenException("Booking does not belong to the current user");
        }


        //check  booking is expired or not
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("booking has already expired");
        }

        //check booking status is reserved or not
        if(booking.getBookingStatus()!=BookingStatus.RESERVED){
            throw  new IllegalStateException("booking is not under reserved state , cannot add guest");
        }

        //Adding guest while booking
        for(GuestDto guestDto:guestDtoList){
            Guest guest=modelMapper.map(guestDto,Guest.class);
            guest.setUser(user);
            guest=guestRepository.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking=bookingRepository.save(booking);
        return modelMapper.map(booking,BookingDto.class);
    }

    @Override
    @Transactional
    public String initiatePayment(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException("Booking not found with id:"+bookingId));

        User user=getCurrentUser();

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Booking does not belong to the current user");
        }

        //check  booking is expired or not
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("booking has already expired");
        }

        String sessionUrl= checkoutService.getCheckoutSession(booking,
                frontendUrl+"/success",frontendUrl+"/failure");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);
        return sessionUrl;
    }

    //Capturing the Event

    @Override
    @Transactional
    public void capturePayment(Event event) {

        if ("checkout.session.completed".equals(event.getType())){
            //get session
            Session session= (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if(session==null) return;

            String sessionId=session.getId();
            Booking booking=bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(()->
                    new ResourceNotFoundException("Booking not found to this session Id {}"+sessionId));

            //now conform booking
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            //update booking count in inventory
            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),
                    booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

            inventoryRepository.conformBooking(booking.getHotel().getId(),booking.getRoom().getId(),
                    booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

            log.info("Successfully conform the booking with id:{}",booking.getId());

        }else {
            log.warn("Unhandled Event type:{} ",event.getType());
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        log.info("Cancelling the booking of this booking id:{}",bookingId);
        Booking booking=bookingRepository.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException("Booking not found with id:"+bookingId));

        User user=getCurrentUser();

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Booking does not belong to the current user");
        }

        if(booking.getBookingStatus()!=BookingStatus.CONFIRMED){
            throw new IllegalStateException("only conform booking can be canceled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Lock and update inventory to prevent race conditions

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),
                booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

        inventoryRepository.cancelBooking(booking.getHotel().getId(),booking.getRoom().getId(),
                booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

        //handle refund

        try {
            //Retrieve the Stripe Session to get the PaymentIntent ID
            Session session=Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParam=RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(refundParam);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

        log.info("Booking has been successfully cancelled of this booking id:{}",bookingId);

    }

    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException("Booking not found with id:"+bookingId));

        User user=getCurrentUser();

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Booking does not belong to the current user");
        }

        return booking.getBookingStatus().name();
    }


    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}