/**
 * NeonPulse Ticketing - Frontend Main Controller (Hito 2)
 * Orchestrates DOM events, strict validation, safe rendering and asynchronous API workflows.
 */

import {
  IBookingRequestPayload,
  EventStatus,
  IEvent,
  MembershipTier,
  NotificationType,
  ViewState
} from './types/index.ts';
import { ApiService } from './services/api.service.ts';
import { ValidationService } from './services/validation.service.ts';
import {
  formatCurrency,
  formatDate,
  getRequiredElement
} from './dom/dom-helpers.ts';
import { UIRenderer } from './dom/ui-renderer.ts';

class AppController {
  // State variables
  private events: readonly IEvent[] = [];
  private selectedEvent: IEvent | null = null;
  private currentView: ViewState = ViewState.EVENT_CATALOG;

  // DOM Elements (Strongly typed)
  private readonly notificationContainer: HTMLElement;
  private readonly catalogSection: HTMLElement;
  private readonly eventsContainer: HTMLElement;
  private readonly bookingSection: HTMLElement;
  private readonly confirmationSection: HTMLElement;
  private readonly bookingForm: HTMLFormElement;
  private readonly inputEventId: HTMLInputElement;
  private readonly inputCustomerName: HTMLInputElement;
  private readonly inputCustomerEmail: HTMLInputElement;
  private readonly selectMembershipTier: HTMLSelectElement;
  private readonly inputQuantity: HTMLInputElement;
  private readonly pricingPreviewContainer: HTMLElement;
  private readonly selectedEventInfoContainer: HTMLElement;
  private readonly submitButton: HTMLButtonElement;
  private readonly closeBookingBtn: HTMLButtonElement;
  private readonly refreshCatalogBtn: HTMLButtonElement;

  constructor() {
    // 1. Safe DOM element queries with type assertions and null guards
    this.notificationContainer = getRequiredElement<HTMLElement>('#notification-container');
    this.catalogSection = getRequiredElement<HTMLElement>('#catalog-section');
    this.eventsContainer = getRequiredElement<HTMLElement>('#events-container');
    this.bookingSection = getRequiredElement<HTMLElement>('#booking-section');
    this.confirmationSection = getRequiredElement<HTMLElement>('#confirmation-section');

    this.bookingForm = getRequiredElement<HTMLFormElement>('#booking-form', HTMLFormElement);
    this.inputEventId = getRequiredElement<HTMLInputElement>('#input-event-id', HTMLInputElement);
    this.inputCustomerName = getRequiredElement<HTMLInputElement>('#input-customer-name', HTMLInputElement);
    this.inputCustomerEmail = getRequiredElement<HTMLInputElement>('#input-customer-email', HTMLInputElement);
    this.selectMembershipTier = getRequiredElement<HTMLSelectElement>('#select-membership-tier', HTMLSelectElement);
    this.inputQuantity = getRequiredElement<HTMLInputElement>('#input-quantity', HTMLInputElement);
    this.pricingPreviewContainer = getRequiredElement<HTMLElement>('#pricing-preview-container');
    this.selectedEventInfoContainer = getRequiredElement<HTMLElement>('#selected-event-info');
    this.submitButton = getRequiredElement<HTMLButtonElement>('#btn-submit-booking', HTMLButtonElement);
    this.closeBookingBtn = getRequiredElement<HTMLButtonElement>('#btn-close-booking', HTMLButtonElement);
    this.refreshCatalogBtn = getRequiredElement<HTMLButtonElement>('#btn-refresh-catalog', HTMLButtonElement);

    // 2. Initialize application listeners
    this.registerEventListeners();
  }

  /**
   * Initializes application data by loading events asynchronously.
   */
  public async init(): Promise<void> {
    await this.loadEvents();
  }

  /**
   * Registers DOM event listeners with proper event neutralization (preventDefault).
   */
  private registerEventListeners(): void {
    // Form submission listener
    this.bookingForm.addEventListener('submit', (e: SubmitEvent) => {
      e.preventDefault();
      void this.handleBookingSubmission();
    });

    // Real-time pricing recalculation on quantity change
    this.inputQuantity.addEventListener('input', () => {
      this.updatePricingPreview();
    });

    // Real-time pricing recalculation on membership tier change
    this.selectMembershipTier.addEventListener('change', () => {
      this.updatePricingPreview();
    });

    // Close checkout form button
    this.closeBookingBtn.addEventListener('click', () => {
      this.switchView(ViewState.EVENT_CATALOG);
    });

    // Refresh catalog button
    this.refreshCatalogBtn.addEventListener('click', () => {
      void this.loadEvents();
    });
  }

  /**
   * Asynchronously loads events from ApiService with visual loading state.
   */
  private async loadEvents(): Promise<void> {
    UIRenderer.renderLoadingState(this.eventsContainer, 'Cargando cartelera de eventos...');
    this.refreshCatalogBtn.disabled = true;

    try {
      const response = await ApiService.fetchEvents();

      if (response.success && response.data) {
        this.events = response.data;
        UIRenderer.renderEventCatalog(this.eventsContainer, this.events, (event) => {
          this.handleEventSelection(event);
        });
      } else {
        UIRenderer.renderAlert(
          this.notificationContainer,
          response.error || 'No fue posible cargar los eventos.',
          NotificationType.ERROR
        );
      }
    } catch (error: unknown) {
      const errorMessage = error instanceof Error ? error.message : 'Error inesperado de red.';
      UIRenderer.renderAlert(this.notificationContainer, errorMessage, NotificationType.ERROR);
    } finally {
      this.refreshCatalogBtn.disabled = false;
    }
  }

  /**
   * Handles user selection of an event card to initiate booking.
   */
  private handleEventSelection(event: IEvent): void {
    if (event.status !== EventStatus.ACTIVE || event.availableSeats <= 0) {
      UIRenderer.renderAlert(
        this.notificationContainer,
        `El evento '${event.name}' se encuentra agotado o cancelado.`,
        NotificationType.WARNING
      );
      return;
    }

    this.selectedEvent = event;
    this.inputEventId.value = event.id;
    this.inputQuantity.max = String(Math.min(10, event.availableSeats));
    this.inputQuantity.value = '1';

    // Render event summary header in checkout form
    this.selectedEventInfoContainer.innerHTML = `
      <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.5rem;">
        <div>
          <strong style="color: var(--text-primary); font-size: 1.1rem;">${event.name}</strong>
          <p style="color: var(--text-secondary); font-size: 0.85rem; margin-top: 0.2rem;">
            📅 ${formatDate(event.date)} | 📍 ${event.venue}
          </p>
        </div>
        <div style="text-align: right;">
          <span style="color: var(--accent-green); font-weight: 800; font-size: 1.2rem;">
            ${formatCurrency(event.basePrice)}
          </span>
          <small style="display: block; color: var(--accent-cyan); font-size: 0.75rem;">
            ${event.availableSeats} cupos disponibles
          </small>
        </div>
      </div>
    `;

    this.clearFormErrors();
    this.updatePricingPreview();
    this.switchView(ViewState.BOOKING_FORM);
    window.scrollTo({ top: this.bookingSection.offsetTop - 80, behavior: 'smooth' });
  }

  /**
   * Updates real-time pricing calculation preview in DOM.
   */
  private updatePricingPreview(): void {
    if (!this.selectedEvent) return;

    const quantity = parseInt(this.inputQuantity.value, 10) || 1;
    const tier = this.selectMembershipTier.value as MembershipTier;

    UIRenderer.renderPricingSummary(
      this.pricingPreviewContainer,
      this.selectedEvent.basePrice,
      quantity,
      tier
    );
  }

  /**
   * Asynchronously processes the booking form submission.
   */
  private async handleBookingSubmission(): Promise<void> {
    this.clearFormErrors();

    // 1. Clean payload extraction with strict types
    const rawQuantity = parseInt(this.inputQuantity.value, 10);
    const tierValue = this.selectMembershipTier.value;
    const membershipTier = Object.values(MembershipTier).includes(tierValue as MembershipTier)
      ? (tierValue as MembershipTier)
      : MembershipTier.REGULAR;

    const payload: IBookingRequestPayload = {
      customerName: this.inputCustomerName.value.trim(),
      customerEmail: this.inputCustomerEmail.value.trim(),
      membershipTier,
      eventId: this.inputEventId.value.trim(),
      quantity: isNaN(rawQuantity) ? 0 : rawQuantity
    };

    // 2. Validate extracted payload
    const validationResult = ValidationService.validateBookingForm(payload, this.selectedEvent || undefined);
    if (!validationResult.isValid) {
      this.displayFormErrors(validationResult.errors);
      return;
    }

    // 3. Inject visual loading state into button and form
    this.setSubmittingState(true);

    try {
      // 4. Asynchronous API call
      const response = await ApiService.submitBooking(payload);

      if (response.success && response.data) {
        // Success state
        this.bookingForm.reset();
        UIRenderer.renderAlert(
          this.notificationContainer,
          '¡Pago y reserva procesados con éxito!',
          NotificationType.SUCCESS
        );

        // Render confirmation ticket voucher
        UIRenderer.renderConfirmationVoucher(this.confirmationSection, response.data, () => {
          this.switchView(ViewState.EVENT_CATALOG);
          void this.loadEvents();
        });

        this.switchView(ViewState.CONFIRMATION);
        window.scrollTo({ top: 0, behavior: 'smooth' });
      } else {
        // Business logic or network error returned by API
        UIRenderer.renderAlert(
          this.notificationContainer,
          response.error || 'La transacción de reserva no pudo completarse.',
          NotificationType.ERROR
        );
      }
    } catch (error: unknown) {
      const errorMessage =
        error instanceof Error ? error.message : 'Error imprevisto al contactar el servidor de pagos.';
      UIRenderer.renderAlert(this.notificationContainer, errorMessage, NotificationType.ERROR);
    } finally {
      // 5. Restore submit button state
      this.setSubmittingState(false);
    }
  }

  /**
   * Sets visual loading feedback on the submit button.
   */
  private setSubmittingState(isSubmitting: boolean): void {
    this.submitButton.disabled = isSubmitting;
    this.closeBookingBtn.disabled = isSubmitting;
    this.inputCustomerName.disabled = isSubmitting;
    this.inputCustomerEmail.disabled = isSubmitting;
    this.selectMembershipTier.disabled = isSubmitting;
    this.inputQuantity.disabled = isSubmitting;

    if (isSubmitting) {
      this.submitButton.innerHTML = `
        <span class="spinner" style="width: 20px; height: 20px; border-width: 2px; display: inline-block;"></span>
        <span>Procesando pago y emitiendo tickets...</span>
      `;
    } else {
      this.submitButton.innerHTML = `🔒 Confirmar y Pagar Reserva`;
    }
  }

  /**
   * Displays inline validation errors next to corresponding form inputs.
   */
  private displayFormErrors(errors: readonly { field: string; message: string }[]): void {
    errors.forEach((err) => {
      const errorSpan = document.querySelector<HTMLElement>(`#error-${this.camelToKebab(err.field)}`);
      if (errorSpan) {
        errorSpan.textContent = err.message;
        errorSpan.classList.add('form-error--visible');
      }
    });

    UIRenderer.renderAlert(
      this.notificationContainer,
      'Por favor corrige los errores señalados en el formulario antes de continuar.',
      NotificationType.WARNING
    );
  }

  /**
   * Clears inline validation errors from the form.
   */
  private clearFormErrors(): void {
    const errorSpans = document.querySelectorAll<HTMLElement>('.form-error');
    errorSpans.forEach((span) => {
      span.textContent = '';
      span.classList.remove('form-error--visible');
    });
  }

  private camelToKebab(str: string): string {
    return str.replace(/([a-z0-9])([A-Z])/g, '$1-$2').toLowerCase();
  }

  /**
   * Handles UI view switching between Catalog, Booking Form, and Confirmation Voucher.
   */
  private switchView(targetView: ViewState): void {
    this.currentView = targetView;

    switch (this.currentView) {
      case ViewState.EVENT_CATALOG:
        this.catalogSection.style.display = 'block';
        this.bookingSection.style.display = 'none';
        this.confirmationSection.style.display = 'none';
        break;

      case ViewState.BOOKING_FORM:
        this.catalogSection.style.display = 'block';
        this.bookingSection.style.display = 'block';
        this.confirmationSection.style.display = 'none';
        break;

      case ViewState.CONFIRMATION:
        this.catalogSection.style.display = 'none';
        this.bookingSection.style.display = 'none';
        this.confirmationSection.style.display = 'block';
        break;
    }
  }
}

// Bootstrap application on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  const app = new AppController();
  void app.init();
});
