# SpaceNow

SpaceNow es una aplicación móvil desarrollada en Kotlin que permite a los usuarios gestionar y reservar espacios comunes en una comunidad o conjunto residencial.

## Características

- **Autenticación de Usuarios**: Sistema completo de registro e inicio de sesión
- **Gestión de Espacios**: Visualización de espacios disponibles como:
  - Salón Social
  - Zona BBQ
  - Gimnasio
  - Sauna
  - Cancha de Tenis
  - Cancha Sintética
  - Entre otros
- **Sistema de Reservas**: Los usuarios pueden:
  - Ver espacios disponibles
  - Realizar reservas
  - Gestionar sus reservas existentes
  - Modificar o cancelar reservas
- **Panel de Administración**: Los administradores tienen acceso a:
  - Vista de todas las reservas activas
  - Estadísticas de uso de espacios
  - Gestión general del sistema

## Requisitos Técnicos

- Android Studio Arctic Fox o superior
- Kotlin 1.5.0 o superior
- Gradle 7.0 o superior
- JDK 11 o superior
- Dispositivo/Emulador con Android API 21 (5.0) o superior

## Configuración del Proyecto

1. Clona el repositorio:

```bash
git clone https://github.com/AndresT1234/SpaceNow
```

2. Abre el proyecto en Android Studio

3. Sincroniza el proyecto con Gradle

4. Ejecuta la aplicación:
   - Conecta un dispositivo Android o inicia un emulador
   - Presiona 'Run' en Android Studio o usa el comando:
   ```bash
   ./gradlew installDebug
   ```

## Arquitectura y Tecnologías

- **Arquitectura**: MVVM (Model-View-ViewModel)
- **UI**: Jetpack Compose
- **Navegación**: Navigation Component
- **Estado**: StateFlow y Coroutines
- **Tema**: Material Design 3
