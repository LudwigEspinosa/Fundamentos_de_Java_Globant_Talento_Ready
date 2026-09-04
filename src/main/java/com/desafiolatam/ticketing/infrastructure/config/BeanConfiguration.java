package com.desafiolatam.ticketing.infrastructure.config;

import com.desafiolatam.ticketing.application.port.NotificationService;
import com.desafiolatam.ticketing.application.port.PaymentGateway;
import com.desafiolatam.ticketing.application.usecase.CancelBookingUseCase;
import com.desafiolatam.ticketing.application.usecase.CreateBookingUseCase;
import com.desafiolatam.ticketing.application.usecase.GetEventCatalogUseCase;
import com.desafiolatam.ticketing.domain.repository.BookingRepository;
import com.desafiolatam.ticketing.domain.repository.CustomerRepository;
import com.desafiolatam.ticketing.domain.repository.EventRepository;
import com.desafiolatam.ticketing.infrastructure.adapter.ConsoleNotificationService;
import com.desafiolatam.ticketing.infrastructure.adapter.SimulatedPaymentGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration registering pure domain and application use cases as Spring Beans.
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public PaymentGateway paymentGateway() {
        return new SimulatedPaymentGateway();
    }

    @Bean
    public NotificationService notificationService() {
        return new ConsoleNotificationService();
    }

    @Bean
    public CreateBookingUseCase createBookingUseCase(
            EventRepository eventRepository,
            CustomerRepository customerRepository,
            BookingRepository bookingRepository,
            PaymentGateway paymentGateway,
            NotificationService notificationService
    ) {
        return new CreateBookingUseCase(
                eventRepository,
                customerRepository,
                bookingRepository,
                paymentGateway,
                notificationService
        );
    }

    @Bean
    public CancelBookingUseCase cancelBookingUseCase(
            BookingRepository bookingRepository,
            EventRepository eventRepository
    ) {
        return new CancelBookingUseCase(bookingRepository, eventRepository);
    }

    @Bean
    public GetEventCatalogUseCase getEventCatalogUseCase(EventRepository eventRepository) {
        return new GetEventCatalogUseCase(eventRepository);
    }
}
