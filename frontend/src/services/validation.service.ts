import {
  IBookingRequestPayload,
  IEvent,
  IValidationError,
  IValidationResult,
  MembershipTier
} from '../types/index.ts';

/**
 * Service for strict validation of form payloads and business invariants.
 */
export class ValidationService {
  private static readonly EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  /**
   * Validates the customer booking form payload.
   *
   * @param payload extracted form data
   * @param selectedEvent the targeted event to check capacity
   * @returns validation result with specific error messages
   */
  public static validateBookingForm(
    payload: IBookingRequestPayload,
    selectedEvent?: IEvent
  ): IValidationResult {
    const errors: IValidationError[] = [];

    // Validate Customer Name
    if (!payload.customerName || payload.customerName.trim().length < 3) {
      errors.push({
        field: 'customerName',
        message: 'El nombre completo debe tener al menos 3 caracteres.'
      });
    }

    // Validate Email
    if (!payload.customerEmail || !this.EMAIL_REGEX.test(payload.customerEmail.trim())) {
      errors.push({
        field: 'customerEmail',
        message: 'Por favor ingresa un correo electrónico válido (ejemplo: usuario@correo.com).'
      });
    }

    // Validate Membership Tier
    if (!Object.values(MembershipTier).includes(payload.membershipTier)) {
      errors.push({
        field: 'membershipTier',
        message: 'El nivel de membresía seleccionado no es válido.'
      });
    }

    // Validate Event Selection
    if (!payload.eventId || payload.eventId.trim().length === 0) {
      errors.push({
        field: 'eventId',
        message: 'Debes seleccionar un evento válido de la cartelera.'
      });
    }

    // Validate Quantity
    if (isNaN(payload.quantity) || payload.quantity < 1) {
      errors.push({
        field: 'quantity',
        message: 'La cantidad de entradas debe ser al menos 1.'
      });
    } else if (payload.quantity > 10) {
      errors.push({
        field: 'quantity',
        message: 'El límite máximo por compra es de 10 entradas.'
      });
    } else if (selectedEvent && payload.quantity > selectedEvent.availableSeats) {
      errors.push({
        field: 'quantity',
        message: `No hay suficientes entradas disponibles. Quedan solo ${selectedEvent.availableSeats} entradas.`
      });
    }

    return {
      isValid: errors.length === 0,
      errors
    };
  }

  /**
   * Calculates discount rate according to the membership tier.
   */
  public static getDiscountRate(tier: MembershipTier): number {
    switch (tier) {
      case MembershipTier.VIP:
        return 0.20;
      case MembershipTier.PREMIUM:
        return 0.10;
      case MembershipTier.REGULAR:
      default:
        return 0.00;
    }
  }

  /**
   * Calculates price breakdown for real-time checkout preview.
   */
  public static calculatePricing(basePrice: number, quantity: number, tier: MembershipTier): {
    grossTotal: number;
    discountRate: number;
    discountAmount: number;
    netTotal: number;
  } {
    const grossTotal = basePrice * quantity;
    const discountRate = this.getDiscountRate(tier);
    const discountAmount = grossTotal * discountRate;
    const netTotal = Math.max(0, grossTotal - discountAmount);

    return {
      grossTotal,
      discountRate,
      discountAmount,
      netTotal
    };
  }
}
