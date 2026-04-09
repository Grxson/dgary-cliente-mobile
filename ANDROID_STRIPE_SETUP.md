# Stripe Payment Integration - Android Setup

## ✅ Implementación completada en Android

Se han actualizado los siguientes componentes:

### 1. **Data DTOs** (`PaymentDtos.kt`)
- ✅ PaymentIntentResponseDto
- ✅ ConfirmPaymentResponseDto
- ✅ PaymentStatusResponseDto

### 2. **API Service** (ApiService.kt)
- ✅ `createPaymentIntent()` - POST /api/v1/orders/{orderId}/payment/intent
- ✅ `confirmPayment()` - POST /api/v1/orders/{orderId}/payment/confirm
- ✅ `getPaymentStatus()` - GET /api/v1/orders/{orderId}/payment/status

### 3. **Payment UI** (PaymentMethodActivity.kt)
- ✅ Integra Stripe PaymentSheet completo
- ✅ Obtiene token de SessionManager
- ✅ Llama API para crear payment intent
- ✅ Presenta PaymentSheet al usuario
- ✅ Confirma pago en API
- ✅ Maneja errores y estados

### 4. **Order Integration** (OrderDetailActivity.kt)
- ✅ Botones de pago pasan order_id a PaymentMethodActivity

---

## 🔧 Configuración Requerida

### Paso 1: Obtener tu clave pública de Stripe

1. Ir a https://dashboard.stripe.com/apikeys
2. Copiar tu **Publishable key** (pk_test_...)
3. Reemplazar en `PaymentMethodActivity.kt` línea 43:

```kotlin
// ANTES:
PaymentConfiguration.init(applicationContext, "pk_test_12345")

// DESPUÉS:
PaymentConfiguration.init(applicationContext, "pk_test_TU_CLAVE_PUBLICA")
```

### Paso 2: Verificar API Backend

Asegúrate de que en tu Laravel .env están configuradas:

```env
STRIPE_PUBLIC_KEY=pk_test_...
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...
```

Y que el servidor está corriendo en `http://192.168.1.10:8000` (o actualiza ApiClient.kt)

### Paso 3: Build & Test

```bash
# Desde Android Studio o terminal
./gradlew build

# O presiona Build > Build Bundle(s) / APK(s)
```

---

## 🧪 Testing con Tarjetas de Prueba

Usa estas tarjetas en PaymentSheet (dirección cualquiera, fecha futura, CVC cualquiera):

| Número | Resultado | Caso de Uso |
|--------|-----------|------------|
| `4242 4242 4242 4242` | ✅ Éxito | Pago normal |
| `4000 0000 0000 0002` | ❌ Rechazado | Simular rechazo |
| `4000 0025 0000 3155` | 3D Secure | Autenticación adicional |
| `5555 5555 5555 4444` | ✅ Éxito (Mastercard) | Test diferente tarjeta |

**Expiry:** Cualquier fecha futura (12/25)  
**CVC:** Cualquier 3 dígitos (123)

---

## 📱 Flow de Pago Completo

```
1. Usuario navega a OrderDetailActivity
       ↓
2. Click en "Guardar Tarjeta" / "Metodo Pago"
       ↓
3. Se abre PaymentMethodActivity
       ↓
4. Click en "Guardar Tarjeta"
       ↓
5. App llama: POST /api/v1/orders/{orderId}/payment/intent
       ↓
6. API retorna: client_secret + payment_id
       ↓
7. PaymentSheet se presenta al usuario
       ↓
8. Usuario entra tarjeta de prueba y confirma
       ↓
9. Stripe procesa el pago
       ↓
10. App llama: POST /api/v1/orders/{orderId}/payment/confirm
       ↓
11. API actualiza Payment.status = "succeeded"
       ↓
12. App cierra y vuelve a OrderDetail
```

---

## 🐛 Troubleshooting

### "Token de autenticación no disponible"
- **Causa:** Usuario no está logueado
- **Solución:** Asegurar que SessionManager tiene token válido (revisa login flow)

### "API Error 404"
- **Causa:** Ruta no existe en Laravel
- **Solución:** Verifica que las rutas están registradas en `routes/api.php`

### "Payment intent creation failed"
- **Causa:** STRIPE_SECRET_KEY no está configurado
- **Solución:** Agrega claves Stripe a `.env` del Laravel

### "PaymentSheet no aparece"
- **Causa:** client_secret inválido o vacío
- **Solución:** Revisa logs de API (Laravel), verifica respuesta JSON

### "Pago confirmado pero Order no actualiza"
- **Causa:** Webhook no configurado
- **Solución:** Configura webhook en Stripe Dashboard (ver STRIPE_INTEGRATION_GUIDE.md en API)

---

## 📊 Logs para Debugging

Si algo no funciona, revisa logcat en Android Studio:

```bash
# En Android Studio:
Logcat → buscar "PaymentMethodActivity"

# Verás mensajes como:
📱 Order ID: 123
🔐 Llamando a API para crear payment intent...
📡 Respuesta API: 201
✅ Payment intent creado: pi_1ABC...
🎯 PaymentSheet result: Completed
✅ Usuario confirmó pago
🔄 Confirmando pago en API...
✅ Pago confirmado: Pago confirmado exitosamente
```

---

## 📁 Archivos Actualizados

- ✅ `app/src/.../PaymentDtos.kt` (NUEVO)
- ✅ `app/src/.../ApiService.kt` (UPDATED - 3 métodos de pago)
- ✅ `app/src/.../PaymentMethodActivity.kt` (COMPLETELY REWRITTEN)
- ✅ `app/src/.../OrderDetailActivity.kt` (UPDATED - pass order_id)

---

## 🎯 Próximos Pasos Opcionales

1. **Agregar status visual del pago** en OrderDetailActivity
2. **Retry logic** para pagos fallidos
3. **Refund UI** si lo necesitas
4. **Saved payment methods** (guardar tarjetas)
5. **Google Pay / Apple Pay** (futura expansión)

---

## ❓ Preguntas?

Revisión de arquitectura:
- **Tarjetas:** Procesadas completamente por Stripe (PCI DSS compliant)
- **Tokens:** Nunca se guardan en app (solo en Stripe)
- **Secret key:** Solo en backend (nunca en app)
- **Public key:** Safe en app (solo crea UI)

¡Listo para pagar! 🎉
