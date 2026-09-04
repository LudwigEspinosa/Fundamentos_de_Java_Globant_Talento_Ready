import {
  IApiResponse,
  IBookingRequestPayload,
  IBookingResponseDTO,
  BookingStatus,
  EventStatus,
  IEvent,
  MembershipTier
} from '../types/index.ts';
import { ValidationService } from './validation.service.ts';

/**
 * Initial dataset simulating server-side database records.
 */
const INITIAL_EVENTS_DATA: IEvent[] = [
  {
    id: 'EVT-001',
    name: 'Neon Cyberpunk Electronic Festival 2026',
    date: '2026-10-15T21:00:00',
    venue: 'Movistar Arena, Santiago',
    basePrice: 45000,
    totalCapacity: 50,
    availableSeats: 32,
    status: EventStatus.ACTIVE,
    category: 'Música Electrónica / Synthwave',
    imageUrl: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80'
  },
  {
    id: 'EVT-002',
    name: 'Rock Alternativo: Chilean Indie Summit',
    date: '2026-11-04T19:30:00',
    venue: 'Teatro Caupolicán, Santiago',
    basePrice: 32000,
    totalCapacity: 40,
    availableSeats: 5,
    status: EventStatus.ACTIVE,
    category: 'Rock / Indie',
    imageUrl: 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80'
  },
  {
    id: 'EVT-003',
    name: 'Global Tech & Cloud Dev Conference',
    date: '2026-11-20T09:00:00',
    venue: 'Centro Parque, Las Condes',
    basePrice: 85000,
    totalCapacity: 20,
    availableSeats: 0,
    status: EventStatus.SOLD_OUT,
    category: 'Conferencia & Networking',
    imageUrl: 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=600&auto=format&fit=crop&q=80'
  },
  {
    id: 'EVT-004',
    name: 'Sinfonía Anime & Videojuegos en Vivo',
    date: '2026-12-05T18:00:00',
    venue: 'Teatro Nescafé de las Artes',
    basePrice: 28000,
    totalCapacity: 60,
    availableSeats: 48,
    status: EventStatus.ACTIVE,
    category: 'Orquesta Clásica & Geek',
    imageUrl: 'https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=600&auto=format&fit=crop&q=80'
  }
];

/**
 * Helper to simulate network latency with Promise.
 */
const delay = (ms: number): Promise<void> =>
  new Promise((resolve) => setTimeout(resolve, ms));

/**
 * Asynchronous service for fetching remote data and processing bookings.
 */
export class ApiService {
  private static eventsDatabase: IEvent[] = [...INITIAL_EVENTS_DATA];

  /**
   * Asynchronously fetches all available events from the catalog.
   * Encapsulated in try/catch/finally with realistic network latency.
   *
   * @returns IApiResponse with array of IEvent
   */
  public static async fetchEvents(): Promise<IApiResponse<IEvent[]>> {
    try {
      // Simulate network request latency (650ms)
      await delay(650);

      // Deep copy to prevent accidental outside mutation
      const events: IEvent[] = this.eventsDatabase.map((ev) => ({ ...ev }));

      return {
        success: true,
        data: events,
        statusCode: 200
      };
    } catch (error: unknown) {
      const errorMessage =
        error instanceof Error ? error.message : 'Error desconocido al conectar con el servidor.';
      return {
        success: false,
        error: errorMessage,
        statusCode: 500
      };
    }
  }

  /**
   * Asynchronously retrieves a single event by its ID.
   */
  public static async fetchEventById(eventId: string): Promise<IApiResponse<IEvent>> {
    try {
      await delay(300);

      const found = this.eventsDatabase.find((e) => e.id === eventId);
      if (!found) {
        return {
          success: false,
          error: `No se encontró ningún evento con el identificador '${eventId}'.`,
          statusCode: 404
        };
      }

      return {
        success: true,
        data: { ...found },
        statusCode: 200
      };
    } catch (error: unknown) {
      const errorMessage =
        error instanceof Error ? error.message : 'Error interno al consultar el evento.';
      return {
        success: false,
        error: errorMessage,
        statusCode: 500
      };
    }
  }

  /**
   * Asynchronously submits a ticket booking transaction.
   * Simulates inventory verification, seat reservation, payment processing and receipt generation.
   *
   * @param payload booking form input data
   * @returns IApiResponse containing IBookingResponseDTO
   */
  public static async submitBooking(
    payload: IBookingRequestPayload
  ): Promise<IApiResponse<IBookingResponseDTO>> {
    try {
      // 1. Simulate network flight time (900ms)
      await delay(900);

      // 2. Locate target event in database
      const targetEvent = this.eventsDatabase.find((e) => e.id === payload.eventId);
      if (!targetEvent) {
        return {
          success: false,
          error: `El evento solicitado '${payload.eventId}' no existe en el sistema.`,
          statusCode: 404
        };
      }

      // 3. Business rule check: Active status
      if (targetEvent.status !== EventStatus.ACTIVE) {
        return {
          success: false,
          error: `El evento '${targetEvent.name}' no está disponible para venta en este momento.`,
          statusCode: 400
        };
      }

      // 4. Business rule check: Capacity
      if (payload.quantity > targetEvent.availableSeats) {
        return {
          success: false,
          error: `Stock insuficiente: Solicitaste ${payload.quantity} entradas pero solo quedan ${targetEvent.availableSeats} disponibles.`,
          statusCode: 409
        };
      }

      // 5. Calculate financials
      const pricing = ValidationService.calculatePricing(
        targetEvent.basePrice,
        payload.quantity,
        payload.membershipTier
      );

      // 6. Deduct inventory & update status
      targetEvent.availableSeats -= payload.quantity;
      if (targetEvent.availableSeats === 0) {
        targetEvent.status = EventStatus.SOLD_OUT;
      }

      // 7. Generate confirmed booking response DTO
      const bookingId = `NP-${Date.now().toString(36).toUpperCase()}-${Math.floor(1000 + Math.random() * 9000)}`;

      const responseDTO: IBookingResponseDTO = {
        bookingId,
        customerName: payload.customerName.trim(),
        eventName: targetEvent.name,
        quantity: payload.quantity,
        grossTotal: pricing.grossTotal,
        discountAmount: pricing.discountAmount,
        netTotal: pricing.netTotal,
        status: BookingStatus.CONFIRMED,
        message: '¡Tu reserva ha sido confirmada y emitida exitosamente en NeonPulse!',
        timestamp: new Date().toISOString()
      };

      return {
        success: true,
        data: responseDTO,
        statusCode: 201
      };
    } catch (error: unknown) {
      const errorMessage =
        error instanceof Error ? error.message : 'Error inesperado durante la transacción.';
      return {
        success: false,
        error: errorMessage,
        statusCode: 500
      };
    }
  }
}
