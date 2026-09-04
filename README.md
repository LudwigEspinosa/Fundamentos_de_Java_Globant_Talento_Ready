# 🎟️ NeonPulse Ticketing Platform

[![Java CI with Maven and JaCoCo](https://github.com/your-username/Fundamentos_de_Java_Globant_Talento_Ready/actions/workflows/ci.yml/badge.svg)](https://github.com/your-username/Fundamentos_de_Java_Globant_Talento_Ready/actions)
[![TypeScript: Strict](https://img.shields.io/badge/TypeScript-Strict%20Mode-blue.svg)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-5.x-646CFF.svg)](https://vitejs.dev/)
[![Coverage: 100%](https://img.shields.io/badge/Coverage-100%25-brightgreen.svg)]()

> **Programa:** Fundamentos de Java — **Globant Talento Ready / {desafío} latam_**  
> **Proyecto Full-Stack Autónomo:** Sistema de Gestión, Emisión y Reserva de Entradas para Eventos (*NeonPulse Ticketing*).

---

## 📋 Resumen de Hitos del Proyecto

| Hito | Área | Tecnologías | Criterios Clave Cumplidos |
| :---: | :--- | :--- | :--- |
| **Hito 1** | **Backend / Core de Dominio Puro** | Java 17+, JUnit 5, Mockito 5, JaCoCo | • Modelo de dominio puro en Java sin acoplamiento a frameworks.<br>• Suite automatizada bajo el **Patrón AAA (Arrange, Act, Assert)**.<br>• Excepciones de negocio con `assertThrows` y dobles de prueba con Mockito.<br>• **100% de cobertura lógica (Line/Branch Coverage)** verificada con JaCoCo. |
| **Hito 2** | **Frontend Dinámico** | TypeScript (Strict), Vite, HTML5, CSS3 | • **Tipado hermético en TS:** Cero uso de `any`, enums e interfaces estrictas.<br>• **Renderizado seguro del DOM:** Guardias de tipo contra nulidad y captura con `preventDefault()`.<br>• **Asincronía moderna:** Funciones con `async/await`, bloques `try/catch/finally` y estados visuales de carga (spinners y feedback en pantalla). |

---

## 🏛️ Estructura Global del Repositorio

```text
.
├── .github/
│   └── workflows/
│       └── ci.yml                             # Pipeline CI para Backend Java y JaCoCo
├── .gitignore                                 # Exclusiones para Java, Maven, Node y Vite
├── pom.xml                                    # Descriptor Maven del Backend (Hito 1)
├── README.md                                  # Documentación técnica completa (Hitos 1 y 2)
├── src/                                       # Código fuente del Backend Java (Hito 1)
│   ├── main/java/com/desafiolatam/ticketing/
│   │   ├── domain/dto/                        # BookingRequest, BookingResponse
│   │   ├── domain/exception/                  # Jerarquía de excepciones de negocio
│   │   ├── domain/model/                      # Customer, Event, Booking, MembershipTier...
│   │   ├── domain/port/                       # Repositorios, PaymentGateway, NotificationService
│   │   └── domain/service/                    # TicketBookingService
│   └── test/java/com/desafiolatam/ticketing/
│       └── domain/...                         # Suite de pruebas unitarias AAA y Mockito (100% Cobertura)
└── frontend/                                  # Aplicación Frontend Dinámica (Hito 2)
    ├── index.html                             # SPA de cartelera y reserva de tickets
    ├── package.json                           # Configuración y scripts de Vite y TypeScript
    ├── tsconfig.json                          # Configuración TypeScript en modo estricto
    ├── vite.config.ts                         # Configuración del servidor de desarrollo Vite
    └── src/
        ├── types/index.ts                     # Interfaces, Enums y DTOs herméticos
        ├── services/
        │   ├── api.service.ts                 # Servicio asíncrono con async/await y simulación de API
        │   └── validation.service.ts          # Validador de formularios con guardias de tipo
        ├── dom/
        │   ├── dom-helpers.ts                 # Selectores seguros contra nulos y utilidades
        │   └── ui-renderer.ts                 # Renderizado de componentes, spinners y comprobantes
        ├── style.css                          # Estilos Cyber Neon / Glassmorphism
        └── main.ts                            # Controlador principal y listeners de eventos
```

---

## ☕ HITO 1: Backend en Java Puro, TDD y Suite de Pruebas

### Glosario Técnico de Negocio (Ubiquitous Language)
- **`Customer`:** Cliente registrado con ID, nombre, email validado y nivel de membresía (`MembershipTier`).
- **`MembershipTier`:** Niveles `REGULAR` (0% dcto), `PREMIUM` (10% dcto) y `VIP` (20% dcto).
- **`Event`:** Entidad que controla stock de asientos, precio base, capacidad y transiciones de estado (`ACTIVE`, `SOLD_OUT`, `CANCELLED`).
- **`Booking`:** Agregado raíz con desglose de totales (bruto, descuento, neto) y estados (`PENDING`, `CONFIRMED`, `FAILED`, `CANCELLED`).
- **`TicketBookingService`:** Servicio orquestador con inyección por constructor de puertos (`EventRepository`, `BookingRepository`, `PaymentGateway`, `NotificationService`).

### Comandos de Ejecución Backend:
```bash
# 1. Compilar clases Java
mvn clean compile

# 2. Ejecutar suite de pruebas con JUnit 5 y Mockito (Patrón AAA)
mvn test

# 3. Validar regla de Cobertura Matemática del 100% con JaCoCo
mvn jacoco:check
```

---

## ⚡ HITO 2: Frontend Dinámico con TypeScript y Vite

### 1. Modelado y Tipado de Estructuras (Strict TypeScript)
- Todos los modelos están definidos en [`frontend/src/types/index.ts`](file:///Users/escandalosos/Documents/GitHub/Fundamentos_de_Java_Globant_Talento_Ready/frontend/src/types/index.ts).
- **Cero uso de `any`:** Todos los datos, retornos y parámetros tienen tipos explícitos.
- **Enums estrictos:** `EventStatus`, `BookingStatus`, `MembershipTier`, `NotificationType`, `ViewState`.

### 2. Renderizado Seguro y Gestión del DOM
- Captura de elementos mediante funciones con guardias de tipo (`getRequiredElement<T>()` en [`frontend/src/dom/dom-helpers.ts`](file:///Users/escandalosos/Documents/GitHub/Fundamentos_de_Java_Globant_Talento_Ready/frontend/src/dom/dom-helpers.ts)) para evitar referencias nulas en tiempo de ejecución.
- Intercepción de eventos de formulario con `e.preventDefault()`, impidiendo la recarga nativa del navegador.
- Validación estricta y cálculo de precios en tiempo real según la membresía seleccionada.

### 3. Simulación Asíncrona con Bloques de Control (`async/await`)
- [`frontend/src/services/api.service.ts`](file:///Users/escandalosos/Documents/GitHub/Fundamentos_de_Java_Globant_Talento_Ready/frontend/src/services/api.service.ts) implementa llamadas de red simuladas con latencia y manejo de errores encapsulados en `try/catch/finally`.
- **Feedback visual dinámico:**
  - Spinners de carga mientras se consultan los eventos.
  - Bloqueo y animación en el botón de pago durante la transacción ("Procesando pago y emitiendo tickets...").
  - Renderizado de comprobante de compra confirmado (Voucher oficial con ID de reserva y desglose financiero) o alertas descriptivas de error en caso de fallo.

### Comandos de Ejecución Frontend:
```bash
# Navegar a la carpeta frontend
cd frontend

# 1. Instalar dependencias
npm install

# 2. Iniciar servidor de desarrollo con Vite
npm run dev

# 3. Compilar para producción (Typecheck + Bundling)
npm run build
```

