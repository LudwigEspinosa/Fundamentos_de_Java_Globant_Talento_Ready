import {
  IBookingResponseDTO,
  EventStatus,
  IEvent,
  MembershipTier,
  NotificationType
} from '../types/index.ts';
import { formatCurrency, formatDate } from './dom-helpers.ts';
import { ValidationService } from '../services/validation.service.ts';

/**
 * UI Rendering engine for dynamic DOM manipulation and visual feedback (Hito 2).
 */
export class UIRenderer {
  /**
   * Renders loading spinner inside target container.
   */
  public static renderLoadingState(container: HTMLElement, message: string = 'Cargando eventos desde el servidor...'): void {
    container.innerHTML = `
      <div class="loading-state" role="status" aria-live="polite">
        <div class="spinner"></div>
        <p class="loading-text">${message}</p>
      </div>
    `;
  }

  /**
   * Renders the event catalog cards into the container.
   */
  public static renderEventCatalog(
    container: HTMLElement,
    events: readonly IEvent[],
    onSelectEvent: (event: IEvent) => void
  ): void {
    if (events.length === 0) {
      container.innerHTML = `
        <div class="empty-state">
          <p>No hay eventos disponibles en la cartelera en este momento.</p>
        </div>
      `;
      return;
    }

    container.innerHTML = '';

    events.forEach((event) => {
      const card = document.createElement('article');
      card.className = `event-card ${event.status === EventStatus.SOLD_OUT ? 'event-card--soldout' : ''}`;
      card.setAttribute('data-event-id', event.id);

      const statusBadge =
        event.status === EventStatus.ACTIVE
          ? `<span class="badge badge--success">✓ Disponible (${event.availableSeats} cupos)</span>`
          : event.status === EventStatus.SOLD_OUT
          ? `<span class="badge badge--danger">✕ Agotado (Sold Out)</span>`
          : `<span class="badge badge--neutral">Cancelado</span>`;

      const canBook = event.status === EventStatus.ACTIVE && event.availableSeats > 0;

      card.innerHTML = `
        <div class="event-card__image-wrapper">
          <img
            src="${event.imageUrl || 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600'}"
            alt="${event.name}"
            class="event-card__image"
            loading="lazy"
          />
          <div class="event-card__category">${event.category}</div>
        </div>
        <div class="event-card__body">
          <div class="event-card__header">
            ${statusBadge}
            <h3 class="event-card__title">${event.name}</h3>
          </div>
          <div class="event-card__details">
            <p class="event-detail">
              <span class="event-detail__icon">📅</span>
              <span>${formatDate(event.date)}</span>
            </p>
            <p class="event-detail">
              <span class="event-detail__icon">📍</span>
              <span>${event.venue}</span>
            </p>
            <p class="event-detail">
              <span class="event-detail__icon">🎟️</span>
              <span class="event-detail__price">${formatCurrency(event.basePrice)} <small>/ entrada</small></span>
            </p>
          </div>
          <div class="event-card__actions">
            <button
              type="button"
              class="btn btn--primary ${!canBook ? 'btn--disabled' : ''}"
              ${!canBook ? 'disabled' : ''}
            >
              ${canBook ? '⚡ Reservar Entradas' : 'Agotado'}
            </button>
          </div>
        </div>
      `;

      if (canBook) {
        const bookBtn = card.querySelector<HTMLButtonElement>('button');
        if (bookBtn) {
          bookBtn.addEventListener('click', () => onSelectEvent(event));
        }
      }

      container.appendChild(card);
    });
  }

  /**
   * Renders dismissible alert notification.
   */
  public static renderAlert(
    container: HTMLElement,
    message: string,
    type: NotificationType = NotificationType.INFO
  ): void {
    const alertClass = {
      [NotificationType.SUCCESS]: 'alert--success',
      [NotificationType.ERROR]: 'alert--danger',
      [NotificationType.WARNING]: 'alert--warning',
      [NotificationType.INFO]: 'alert--info'
    }[type];

    const icon = {
      [NotificationType.SUCCESS]: '✅',
      [NotificationType.ERROR]: '⚠️',
      [NotificationType.WARNING]: '⚡',
      [NotificationType.INFO]: 'ℹ️'
    }[type];

    container.innerHTML = `
      <div class="alert ${alertClass}" role="alert">
        <span class="alert__icon">${icon}</span>
        <div class="alert__message">${message}</div>
        <button type="button" class="alert__close" aria-label="Cerrar notificación">&times;</button>
      </div>
    `;

    const closeBtn = container.querySelector<HTMLButtonElement>('.alert__close');
    if (closeBtn) {
      closeBtn.addEventListener('click', () => {
        container.innerHTML = '';
      });
    }
  }

  /**
   * Renders the live order pricing summary during checkout.
   */
  public static renderPricingSummary(
    container: HTMLElement,
    basePrice: number,
    quantity: number,
    tier: MembershipTier
  ): void {
    const pricing = ValidationService.calculatePricing(basePrice, quantity, tier);
    const discountPercent = Math.round(pricing.discountRate * 100);

    container.innerHTML = `
      <div class="pricing-summary">
        <div class="pricing-row">
          <span>Subtotal bruto (${quantity} × ${formatCurrency(basePrice)}):</span>
          <span>${formatCurrency(pricing.grossTotal)}</span>
        </div>
        ${
          pricing.discountAmount > 0
            ? `
          <div class="pricing-row pricing-row--discount">
            <span>Descuento fidelidad (${tier} - ${discountPercent}%):</span>
            <span>-${formatCurrency(pricing.discountAmount)}</span>
          </div>
        `
            : ''
        }
        <div class="pricing-row pricing-row--total">
          <strong>Total a pagar:</strong>
          <strong class="total-amount">${formatCurrency(pricing.netTotal)}</strong>
        </div>
      </div>
    `;
  }

  /**
   * Renders the booking confirmation ticket voucher with QR simulation.
   */
  public static renderConfirmationVoucher(
    container: HTMLElement,
    booking: IBookingResponseDTO,
    onReset: () => void
  ): void {
    container.innerHTML = `
      <div class="voucher-card">
        <div class="voucher-card__header">
          <span class="voucher-badge">🎟️ COMPROBANTE OFICIAL CONFIRMADO</span>
          <h2 class="voucher-title">¡Reserva Exitosa en NeonPulse!</h2>
          <p class="voucher-subtitle">Código de Reserva: <strong>${booking.bookingId}</strong></p>
        </div>

        <div class="voucher-card__details">
          <div class="voucher-item">
            <span class="voucher-item__label">Evento:</span>
            <span class="voucher-item__value">${booking.eventName}</span>
          </div>
          <div class="voucher-item">
            <span class="voucher-item__label">Titular de la Entrada:</span>
            <span class="voucher-item__value">${booking.customerName}</span>
          </div>
          <div class="voucher-item">
            <span class="voucher-item__label">Cantidad de Entradas:</span>
            <span class="voucher-item__value">${booking.quantity} entrada(s)</span>
          </div>
          <div class="voucher-item">
            <span class="voucher-item__label">Monto Bruto:</span>
            <span class="voucher-item__value">${formatCurrency(booking.grossTotal)}</span>
          </div>
          ${
            booking.discountAmount > 0
              ? `
            <div class="voucher-item voucher-item--discount">
              <span class="voucher-item__label">Descuento Aplicado:</span>
              <span class="voucher-item__value">-${formatCurrency(booking.discountAmount)}</span>
            </div>
          `
              : ''
          }
          <div class="voucher-item voucher-item--total">
            <span class="voucher-item__label">Total Pagado:</span>
            <span class="voucher-item__value total-highlight">${formatCurrency(booking.netTotal)}</span>
          </div>
          <div class="voucher-item">
            <span class="voucher-item__label">Fecha de Emisión:</span>
            <span class="voucher-item__value">${formatDate(booking.timestamp)}</span>
          </div>
        </div>

        <div class="voucher-card__qr">
          <div class="qr-mockup">
            <div class="qr-pattern"></div>
            <small>ID: ${booking.bookingId}</small>
          </div>
        </div>

        <div class="voucher-card__actions">
          <button type="button" class="btn btn--secondary" id="btn-return-catalog">
            ⬅ Volver a la Cartelera de Eventos
          </button>
        </div>
      </div>
    `;

    const returnBtn = container.querySelector<HTMLButtonElement>('#btn-return-catalog');
    if (returnBtn) {
      returnBtn.addEventListener('click', onReset);
    }
  }
}
