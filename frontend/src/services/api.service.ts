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
 * Base API URL for backend microservice communication.
 * Configurable via Vite environment variables or defaults to local Spring Boot endpoint.
 */
const API_BASE_URL: string =
  (import.meta as any).env?.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

/**
 * Fallback dataset simulating server-side database records if backend is offline.
 */
const FALLBACK_EVENTS_DATA: IEvent[] = [
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
 * Event metadata map for UI presentation (venue, category, images).
 */
const EVENT_METADATA: Record<string, Partial<IEvent>> = {
  'EVT-001': {
    venue: 'Movistar Arena, Santiago',
    category: 'Música Electrónica / Synthwave',
    imageUrl: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80',
    date: '2026-10-15T21:00:00'
  },
  'EVT-002': {
    venue: 'Teatro Caupolicán, Santiago',
    category: 'Rock / Indie',
    imageUrl: 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80',
    date: '2026-11-04T19:30:00'
  },
  'EVT-003': {
    venue: 'Centro Parque, Las Condes',
    category: 'Conferencia & Networking',
    imageUrl: 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=600&auto=format&fit=crop&q=80',
    date: '2026-11-20T09:00:00'
  },
  'EVT-004': {
    venue: 'Teatro Nescafé de las Artes',
    category: 'Orquesta Clásica & Geek',
    imageUrl: 'https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=600&auto=format&fit=crop&q=80',
    date: '2026-12-05T18:00:00'
  }
};

/**
 * Real asynchronous service for fetching remote data and processing bookings via Spring Boot REST API.
 */
export class ApiService {
  private static localEvents: IEvent[] = [...FALLBACK_EVENTS_DATA];

  /**
   * Fetches all events from the Spring Boot REST endpoint (`GET /api/v1/events`).
   * Falls back gracefully if backend is unreachable.
   */
  public static async fetchEvents(): Promise<IApiResponse<IEvent[]>> {
    try {
      const response = await fetch(`${API_BASE_URL}/events`, {
        method: 'GET',
        headers: {
          'Accept': 'application/json'
        }
      });

      if (response.ok) {
        const rawData = await response.json();
        const enrichedEvents: IEvent[] = rawData.map((item: any) => {
          const meta = EVENT_METADATA[item.id] || {};
          return {
            id: item.id,
            name: item.name,
            basePrice: item.basePrice,
            totalCapacity: item.totalCapacity,
            availableSeats: item.availableSeats,
            status: item.status as EventStatus,
            venue: meta.venue || 'Santiago, Chile',
            category: meta.category || 'General',
            imageUrl: meta.imageUrl || 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80',
            date: meta.date || new Date().toISOString()
          };
        });

        this.localEvents = enrichedEvents;
        return {
          success: true,
          data: enrichedEvents,
          statusCode: response.status
        };
      } else {
        throw new Error(`HTTP Error ${response.status}`);
      }
    } catch (networkError) {
      // Graceful local fallback
      return {
        success: true,
        data: this.localEvents.map((e) => ({ ...e })),
        statusCode: 200
      };
    }
  }

  /**
   * Retrieves a single event by ID (`GET /api/v1/events/{id}`).
   */
  public static async fetchEventById(eventId: string): Promise<IApiResponse<IEvent>> {
    try {
      const response = await fetch(`${API_BASE_URL}/events/${eventId}`, {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
      });

      if (response.ok) {
        const item = await response.json();
        const meta = EVENT_METADATA[item.id] || {};
        const event: IEvent = {
          id: item.id,
          name: item.name,
          basePrice: item.basePrice,
          totalCapacity: item.totalCapacity,
          availableSeats: item.availableSeats,
          status: item.status as EventStatus,
          venue: meta.venue || 'Santiago, Chile',
          category: meta.category || 'General',
          imageUrl: meta.imageUrl || 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80',
          date: meta.date || new Date().toISOString()
        };
        return { success: true, data: event, statusCode: 200 };
      }
    } catch {
      // Fallback
    }

    const localFound = this.localEvents.find((e) => e.id === eventId);
    if (localFound) {
      return { success: true, data: { ...localFound }, statusCode: 200 };
    }
    return { success: false, error: 'Evento no encontrado', statusCode: 404 };
  }

  /**
   * Submits a ticket booking transaction to the backend (`POST /api/v1/bookings`).
   */
  public static async submitBooking(
    payload: IBookingRequestPayload
  ): Promise<IApiResponse<IBookingResponseDTO>> {
    try {
      const response = await fetch(`${API_BASE_URL}/bookings`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify({
          customerId: 'CUST-001',
          eventId: payload.eventId,
          quantity: payload.quantity
        })
      });

      const body = await response.json();

      if (response.ok || response.status === 201) {
        const responseDTO: IBookingResponseDTO = {
          bookingId: body.bookingId || `BKG-${Date.now().toString(36).toUpperCase()}`,
          customerName: payload.customerName,
          eventName: body.eventName || 'Evento NeonPulse',
          quantity: body.quantity || payload.quantity,
          grossTotal: body.grossTotal,
          discountAmount: body.discountAmount,
          netTotal: body.netTotal,
          status: BookingStatus.CONFIRMED,
          message: body.message || '¡Tu reserva ha sido confirmada y emitida exitosamente en NeonPulse!',
          timestamp: new Date().toISOString()
        };

        // Update local stock
        const target = this.localEvents.find((e) => e.id === payload.eventId);
        if (target) {
          target.availableSeats -= payload.quantity;
          if (target.availableSeats <= 0) target.status = EventStatus.SOLD_OUT;
        }

        return {
          success: true,
          data: responseDTO,
          statusCode: 201
        };
      } else {
        // Backend returned a handled domain error (e.g. 400, 404, 409)
        return {
          success: false,
          error: body.message || 'Error al procesar la reserva en el servidor.',
          statusCode: response.status
        };
      }
    } catch (networkError) {
      // Offline fallback processing
      const targetEvent = this.localEvents.find((e) => e.id === payload.eventId);
      if (!targetEvent) {
        return { success: false, error: 'Evento no encontrado', statusCode: 404 };
      }
      if (targetEvent.status !== EventStatus.ACTIVE) {
        return { success: false, error: 'Evento no disponible para venta', statusCode: 400 };
      }
      if (payload.quantity > targetEvent.availableSeats) {
        return {
          success: false,
          error: `Stock insuficiente: Solicitaste ${payload.quantity} entradas pero solo quedan ${targetEvent.availableSeats} disponibles.`,
          statusCode: 409
        };
      }

      const pricing = ValidationService.calculatePricing(
        targetEvent.basePrice,
        payload.quantity,
        payload.membershipTier
      );

      targetEvent.availableSeats -= payload.quantity;
      if (targetEvent.availableSeats === 0) targetEvent.status = EventStatus.SOLD_OUT;

      const responseDTO: IBookingResponseDTO = {
        bookingId: `NP-${Date.now().toString(36).toUpperCase()}-${Math.floor(1000 + Math.random() * 9000)}`,
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

      return { success: true, data: responseDTO, statusCode: 201 };
    }
  }
}
