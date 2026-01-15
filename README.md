# FoodLog (AI GEN README)

FoodLog is a small Android app (Kotlin) for logging meals, tracking basic nutrition, and storing meal photos.

## Project overview
- Architecture: MVVM + Repository + Room (local DB) with Retrofit for a nutrition API and Firebase Storage for images.
- Primary packages/files:
  - `app/src/main/java/com/example/foodlog/` — Activities and UI code (e.g. `MainActivity.kt`, `AddMealActivity.kt`, `FoodLogActivity.kt`).
  - `app/src/main/java/com/example/foodlog/data/` — `Meal.kt`, `MealDao.kt`, `MealRepository.kt` (Room entity/DAO/repository).
  - `app/src/main/java/com/example/foodlog/database/MealDatabase.kt` — Room DB singleton.
  - `app/src/main/java/com/example/foodlog/api/ApiService.kt` — Retrofit client + models for CalorieNinjas.
  - `app/src/main/java/com/example/foodlog/MealViewModel.kt` — exposes Flow -> LiveData and write operations.

## Key behaviors & conventions
- View Binding is used everywhere (Activity and item bindings). Prefer `ActivityXBinding` fields over `findViewById`.
- DAO methods return `Flow`; repository returns `Flow`; ViewModel converts to `LiveData` via `asLiveData()`.
- Image handling stores both a local `imageUri` (persistable content URI) and a remote `imageUrl` (Firebase download URL). UI prefers `imageUrl` when online and falls back to `imageUri`.
- Singletons: Room DB and Repository use `@Volatile` + `synchronized` singletons.
- Room is currently configured with `.fallbackToDestructiveMigration()` — schema changes will erase existing data unless migrations are added.

## Prerequisites
- Java JDK and Android SDK (use Android Studio).
- Android Studio (recommended) or Gradle CLI.
- An emulator or device with Google Play services to use Firebase features, or a debug signing configuration.

## Build & run (Windows PowerShell)
Open a PowerShell at the repo root and run:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug   # optional: install on a connected device/emulator
```

Run unit tests:

```powershell
.\gradlew.bat test
```

Run instrumentation tests (requires device/emulator):

```powershell
.\gradlew.bat connectedAndroidTest
```

Run full build / CI checks:

```powershell
.\gradlew.bat build
```

## Local development workflow
- Open the project in Android Studio (Import Gradle project).
- Gradle will sync. Build and run on an emulator/device.
- `google-services.json` is present under `app/` so Firebase features will initialize if the emulator/device supports Play services.

## Secrets & configuration
- `ApiService.kt` currently contains the CalorieNinjas API key in a `@Headers` annotation. For safety, move it to `gradle.properties` or a CI secret and read it via BuildConfig or Gradle.

Example (suggested) gradle.properties entry:

```properties
CALORIE_NINJAS_API_KEY=your_api_key_here
```

And read via Gradle buildConfigField or resValue; do not commit secrets to source.

## Tests & quality
- The repo includes minimal unit and instrumentation test stubs under `app/src/test` and `app/src/androidTest`.
- Prefer unit tests for quick feedback in CI (`.\gradlew.bat test`).

## Where to look first when contributing
- Add/update meal flows: `AddMealActivity.kt`, `MealViewModel.kt`, `MealRepository.kt`, `MealDao.kt`.
- Image upload & display: `AddMealActivity.kt`, `MealAdapter.kt` (uses Glide and network availability checks).
- API network code: `api/ApiService.kt` and the `NutritionalInfo` model.

## Common maintenance notes
- Remove the embedded API key from `ApiService.kt` and provide a secure configuration.
- If you change the Room schema, implement Room migrations rather than relying on destructive fallback if you need to preserve data.

## License
This repository does not include a license file. Add `LICENSE` if you want to make the project open-source.

---
If you'd like, I can:
- Move the API key into `gradle.properties` and wire `ApiService` to read it.
- Add a basic CI workflow file (GitHub Actions) to run `./gradlew test` and `./gradlew assembleDebug`.
- Write a short CONTRIBUTING.md with local dev steps and emulator setup.

Please tell me which of the above you'd like next.
