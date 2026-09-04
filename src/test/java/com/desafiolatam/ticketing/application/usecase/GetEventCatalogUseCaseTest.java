package com.desafiolatam.ticketing.application.usecase;

import com.desafiolatam.ticketing.application.dto.EventResponseDTO;
import com.desafiolatam.ticketing.domain.model.aggregate.Event;
import com.desafiolatam.ticketing.domain.model.enumtype.EventStatus;
import com.desafiolatam.ticketing.domain.model.valueobject.EventId;
import com.desafiolatam.ticketing.domain.model.valueobject.Money;
import com.desafiolatam.ticketing.domain.model.valueobject.SeatCapacity;
import com.desafiolatam.ticketing.domain.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetEventCatalogUseCase Tests")
class GetEventCatalogUseCaseTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private GetEventCatalogUseCase getEventCatalogUseCase;

    @Test
    @DisplayName("Should retrieve catalog and map domain events to EventResponseDTOs")
    void shouldRetrieveCatalogSuccessfully() {
        // Arrange
        Event e1 = new Event(EventId.of("EVT-01"), "Concert A", Money.of(50.0), SeatCapacity.of(100));
        Event e2 = new Event(EventId.of("EVT-02"), "Concert B", Money.of(75.0), SeatCapacity.of(50));
        when(eventRepository.findAll()).thenReturn(List.of(e1, e2));

        // Act
        List<EventResponseDTO> catalog = getEventCatalogUseCase.execute();

        // Assert
        assertNotNull(catalog);
        assertEquals(2, catalog.size());
        assertEquals("EVT-01", catalog.get(0).id());
        assertEquals("Concert A", catalog.get(0).name());
        assertEquals(50.0, catalog.get(0).basePrice(), 0.001);
        assertEquals(EventStatus.ACTIVE, catalog.get(0).status());
    }

    @Test
    @DisplayName("Should throw NullPointerException when EventRepository is null")
    void shouldThrowWhenRepositoryNull() {
        // Arrange, Act & Assert
        assertThrows(NullPointerException.class, () -> new GetEventCatalogUseCase(null));
    }
}
