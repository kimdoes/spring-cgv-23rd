package com.ceos23.spring_cgv_23rd.Payment.Service;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationStatus;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MoviePaymentDBService {

    private final ReservationRepository reservationRepository;

    public MoviePaymentDBService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    private Reservation getActiveReservationById(long targetId){
        return reservationRepository.findActivatedReservationById(targetId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 예약입니다.")
        );
    }

    Reservation setReservation(long targetId){
        Reservation reservation = getActiveReservationById(targetId);

        if(reservation.getStatus().equals(ReservationStatus.RESERVED)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        reservation.buyReservation();

        return reservation;
    }
}
