/**
 * Type-safe DOM element selection and helper utilities (Hito 2).
 * Prevents null reference exceptions through runtime guards and type narrowing.
 */

/**
 * Safely queries an element from the DOM with type verification and null guarding.
 *
 * @param selector CSS selector string
 * @param expectedType Optional Constructor function for runtime verification
 * @returns Strongly typed HTMLElement
 * @throws Error if element is missing or does not match expected type
 */
export function getRequiredElement<T extends HTMLElement>(
  selector: string,
  expectedType?: new () => T
): T {
  const element = document.querySelector<T>(selector);

  if (!element) {
    throw new Error(`[DOM Error] Required element matching selector '${selector}' was not found in the DOM.`);
  }

  if (expectedType && !(element instanceof expectedType)) {
    throw new Error(
      `[DOM Error] Element '${selector}' is not an instance of ${expectedType.name}. Found: ${element.constructor.name}`
    );
  }

  return element;
}

/**
 * Safely finds an element or returns null without throwing.
 */
export function getOptionalElement<T extends HTMLElement>(
  selector: string,
  expectedType?: new () => T
): T | null {
  const element = document.querySelector<T>(selector);

  if (!element) {
    return null;
  }

  if (expectedType && !(element instanceof expectedType)) {
    return null;
  }

  return element;
}

/**
 * Helper to safely format Chilean currency (CLP).
 */
export function formatCurrency(amount: number): string {
  return new Intl.NumberFormat('es-CL', {
    style: 'currency',
    currency: 'CLP',
    maximumFractionDigits: 0
  }).format(amount);
}

/**
 * Helper to format ISO date strings into readable localized format.
 */
export function formatDate(isoDateString: string): string {
  try {
    const date = new Date(isoDateString);
    return new Intl.DateTimeFormat('es-CL', {
      dateStyle: 'full',
      timeStyle: 'short'
    }).format(date);
  } catch {
    return isoDateString;
  }
}
