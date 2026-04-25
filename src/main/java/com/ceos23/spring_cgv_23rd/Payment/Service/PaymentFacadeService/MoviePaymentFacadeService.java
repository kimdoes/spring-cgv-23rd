    package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentService;

    import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentRequestDTO;
    import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentResponseDTO;
    import com.ceos23.spring_cgv_23rd.Payment.Domain.PayType;
    import com.ceos23.spring_cgv_23rd.Payment.Domain.Payment;
    import com.ceos23.spring_cgv_23rd.Payment.Repository.PaymentRepository;
    import com.ceos23.spring_cgv_23rd.Payment.Service.ConcurrencyClient;
    import com.ceos23.spring_cgv_23rd.Payment.Service.PaymentDBService.MoviePaymentDBService;
    import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;
    import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationRepository;
    import feign.FeignException;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.server.ResponseStatusException;

    @Service
    public class MoviePaymentService implements CancelablePaymentService {
        private final ConcurrencyClient concurrencyClient;
        private final PaymentRepository paymentRepository;
        private final ReservationRepository reservationRepository;
        private final PaymentIdHandler paymentIdHandler;
        private final MoviePaymentDBService moviePaymentDBService;

        public MoviePaymentService(ConcurrencyClient concurrencyClient,
                                   PaymentRepository paymentRepository,
                                   ReservationRepository reservationRepository,
                                   PaymentIdHandler paymentIdHandler, MoviePaymentDBService moviePaymentDBService) {
            this.concurrencyClient = concurrencyClient;
            this.paymentRepository = paymentRepository;
            this.reservationRepository = reservationRepository;
            this.paymentIdHandler = paymentIdHandler;
            this.moviePaymentDBService = moviePaymentDBService;
        }

        private Reservation getActiveReservationById(long targetId){
            return reservationRepository.findActivatedReservationById(targetId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 예약입니다.")
            );
        }

        @Transactional
        public Payment buy(PaymentRequestDTO req,
                           long targetId) {
            Reservation reservation = moviePaymentDBService.setReservation(targetId);

            int retry = 0;

            String paymentId = paymentIdHandler.getPaymentId();


            Payment payment = Payment.create(paymentId, req.storeId(), req.orderName(), req.totalPayAmount(), req.currency(), PayType.RESERVATION, reservation.getId());
            paymentRepository.save(payment);

            while (retry < 5) {
                try {
                    retry++;
                    System.out.println("결제 시작!\n" + "retry >>> " + retry);

                    concurrencyClient.pay(paymentId, req);
                    reservation.buyReservation();
                    payment.paymentSuccess();

                    return payment;

                } catch (FeignException fe) {
                    if (handleFeignException(fe)) {
                        if (fe.status() == 409) {
                            payment.updatePaymentId(paymentIdHandler.getPaymentId());
                            continue;
                        } else if (fe.status() == 500) {
                            try {
                                PaymentResponseDTO paymentDTO = concurrencyClient.checkPayment(paymentId);

                                if (paymentDTO.findStatus("PAID")) {
                                    //결제에 성공했으나 응답반환에 실패
                                    reservation.buyReservation();
                                    payment.paymentSuccess();
                                    return payment;
                                } else {
                                    continue;
                                }
                            } catch (FeignException e) {
                                // 결제에 성공하지도 못 함. 외부 연동 서버 에러
                                // 다시 결제 시도를 보내야함
                                continue;
                            }
                        }

                        throw fe;
                    }
                } catch (Exception e) {
                    //외부연동이 아니라 내부 서버에러
                    e.printStackTrace();
                    payment.paymentFail();
                    throw e;
                }
            }

            payment.paymentFail();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "결제에 실패했습니다.");
        }

        @Override
        @Transactional
        public void cancel(long targetId){
            Payment payment = paymentRepository.findByTargetId(targetId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "결제정보가 없습니다.")
            );

            Reservation reservation = reservationRepository.findById(payment.getTargetId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "예약정보가 없습니다.")
            );

            try {
                concurrencyClient.cancel(payment.getId());
                reservation.cancel();
                payment.cancel();
            } catch (FeignException fe){
                //외부연동에러.
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "결제연동에 실패했습니다.");
            } catch (Exception e){
                // 외부연동 후 실패
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버에 장애가 발생했습니다.");
            }
        }
    }
