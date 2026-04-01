# Arquitectura recomendada para la app cliente de heladería

## Arquitectura seleccionada: MVVM (Model-View-ViewModel)

### ¿Por qué MVVM?
- **Separación de responsabilidades:** La lógica de negocio, la interfaz de usuario y la gestión de datos están claramente separadas.
- **Escalabilidad y mantenibilidad:** Facilita el crecimiento del proyecto y el trabajo en equipo.
- **Compatibilidad:** Es la arquitectura recomendada por Google para Android y se integra perfectamente con Jetpack (ViewModel, LiveData, Data Binding).
- **Facilita pruebas:** Permite testear la lógica de negocio y presentación de forma independiente.

---

## Estructura básica de carpetas

```
app/
└── src/
    └── main/
        ├── java/
        │   └── com/tuempresa/tuapp/
        │       ├── data/         # Acceso a datos (API, BD, repositorios)
        │       ├── domain/       # Lógica de negocio (casos de uso, modelos de dominio)
        │       ├── ui/           # Vistas y ViewModels
        │       │   ├── view/     # Activities, Fragments
        │       │   └── viewmodel/# ViewModels
        │       └── utils/        # Utilidades y helpers
        └── res/                 # Recursos (layouts, strings, drawables)
```

---

## Componentes principales

- **Model:** Representa los datos y la lógica de negocio. Incluye modelos, repositorios y fuentes de datos.
- **View:** Activities, Fragments y layouts XML. Solo muestran información y capturan interacción del usuario.
- **ViewModel:** Gestiona la lógica de presentación y expone datos observables a la vista (LiveData, StateFlow, etc.).

---

## Flujo de datos
1. El usuario interactúa con la View (por ejemplo, pulsa "Pedir helado").
2. La View notifica al ViewModel.
3. El ViewModel procesa la acción, consulta el Model (repositorio, API, etc.) y actualiza los datos observables.
4. La View observa los datos del ViewModel y actualiza la UI automáticamente.

---

## Buenas prácticas
- Mantener las Views lo más "tontas" posible (sin lógica de negocio).
- Usar LiveData, StateFlow o similares para la comunicación View-ViewModel.
- Inyectar dependencias (por ejemplo, con Hilt o Dagger) para facilitar pruebas y modularidad.
- Escribir pruebas unitarias para ViewModels y lógica de negocio.

---

## Próximos pasos
1. Definir los módulos principales de la app (pedidos, historial, perfil, etc.).
2. Crear la estructura de carpetas y paquetes.
3. Implementar los primeros modelos, ViewModels y vistas.
4. Configurar la inyección de dependencias si es necesario.

---

¿Listo para comenzar? Podemos ir creando juntos cada parte de la arquitectura y los módulos de la app.