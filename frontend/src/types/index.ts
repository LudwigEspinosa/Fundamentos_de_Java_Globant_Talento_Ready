/**
 * Hermetic Type Definitions for NeonPulse Ticketing Frontend (Hito 2)
 * Strictly typed interfaces, enums and generic wrappers - ZERO use of 'any'.
 */

/**
 * Lifecycle states of an Event.
 */
export enum EventStatus {
  ACTIVE = 'ACTIVE',
  SOLD_OUT = 'SOLD_OUT',
  CANCELLED = 'CANCELLED'
}

/**
 * Status representation for ticket booking transactions.
 */
export enum BookingStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED'
}

/**
 * Customer loyalty tiers and discount classifications.
 */
export enum MembershipTier {
  REGULAR = 'REGULAR',
  PREMIUM = 'PREMIUM',
  VIP = 'VIP'
}

/**
 * UI Notification alert styles.
 */
export enum NotificationType {
  SUCCESS = 'SUCCESS',
  ERROR = 'ERROR',
  INFO = 'INFO',
  WARNING = 'WARNING'
}

/**
 * Active application view states.
 */
export enum ViewState {
  EVENT_CATALOG = 'EVENT_CATALOG',
  BOOKING_FORM = 'BOOKING_FORM',
  CONFIRMATION = 'CONFIRMATION'
}

/**
 * Event entity interface.
 */
export interface IEvent {
  readonly id: string;
  readonly name: string;
  readonly date: string;
  readonly venue: string;
  readonly basePrice: number;
  readonly totalCapacity: number;
  availableSeats: number;
  status: EventStatus;
  readonly category: string;
  readonly imageUrl?: string;
}

/**
 * Customer profile interface.
 */
export interface ICustomer {
  readonly id: string;
  readonly name: string;
  readonly email: string;
  readonly tier: MembershipTier;
}

/**
 * Line item in a booking order.
 */
export interface IBookingItem {
  readonly eventId: string;
  readonly eventName: string;
  readonly unitPrice: number;
  readonly quantity: number;
  readonly subtotal: number;
}

/**
 * Complete booking entity interface.
 */
export interface IBooking {
  readonly id: string;
  readonly customer: ICustomer;
  readonly items: readonly IBookingItem[];
  readonly grossTotal: number;
  readonly discountAmount: number;
  readonly netTotal: number;
  status: BookingStatus;
  readonly createdAt: string;
}

/**
 * Booking form request payload DTO.
 */
export interface IBookingRequestPayload {
  readonly customerName: string;
  readonly customerEmail: string;
  readonly membershipTier: MembershipTier;
  readonly eventId: string;
  readonly quantity: number;
}

/**
 * Booking confirmation response DTO.
 */
export interface IBookingResponseDTO {
  readonly bookingId: string;
  readonly customerName: string;
  readonly eventName: string;
  readonly quantity: number;
  readonly grossTotal: number;
  readonly discountAmount: number;
  readonly netTotal: number;
  readonly status: BookingStatus;
  readonly message: string;
  readonly timestamp: string;
}

/**
 * Generic API response envelope for type safety.
 */
export interface IApiResponse<T> {
  readonly success: boolean;
  readonly data?: T;
  readonly error?: string;
  readonly statusCode: number;
}

/**
 * Form validation error structure.
 */
export interface IValidationError {
  readonly field: string;
  readonly message: string;
}

/**
 * Form validation result.
 */
export interface IValidationResult {
  readonly isValid: boolean;
  readonly errors: readonly IValidationError[];
}
