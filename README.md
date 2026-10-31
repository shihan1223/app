# MediMeal – Medicine & Food Reminder

MediMeal is a native, production-grade Android application designed to help individuals manage their daily eating rhythm and prescribed medication schedules through exact alarms, notifications, and an interactive **Racetrack Daily Timeline**.

---

## Key Features

1. **Personalized 10-Meal Daily Schedule**
   - Automatically calculates 10 daily meals starting at **05:00 AM** at **2-hour intervals** up to **11:00 PM**:
     1. 05:00 AM (Early Morning)
     2. 07:00 AM (Breakfast)
     3. 09:00 AM (Mid-Morning)
     4. 11:00 AM (Pre-Lunch)
     5. 01:00 PM (Lunch)
     6. 03:00 PM (Afternoon Snack)
     7. 05:00 PM (Late Afternoon)
     8. 07:00 PM (Dinner)
     9. 09:00 PM (Post-Dinner)
     10. 11:00 PM (Night Snack)
   - Dynamic schedule customization: change start time, interval length, meal count, rename meals, and toggle individual reminders.
   - Midnight and date transition handling.

2. **Smart Food & Medicine Coordination**
   - 3 default daily medicine slots with support for custom schedules.
   - Timing options:
     - **Before Food** (with custom lead time, e.g., 30m before Breakfast)
     - **After Food** (with custom delay, e.g., 30m after Lunch)
     - **With Food** (synchronized with meal time)
     - **Empty Stomach** (associated with selected meal)
     - **Anytime / No Food Restriction** (independent reminder times)
   - Clinical safety: Alarms clearly show prescribed instructions; doses or times are never altered automatically.

3. **Signature Feature: Racetrack Daily Timeline**
   - Continuous curved racetrack visual representation with 10 checkpoint nodes.
   - Real-time progress tracking, checkpoint status (completed / upcoming / skipped), and attached medication badges.
   - Interactive checkpoint dialogs for marking eaten or taken.

4. **Reliable Alarm & Notification Engine**
   - `AlarmManager` with `setAlarmClock()` and `setExactAndAllowWhileIdle()` for exact minute delivery even in Doze mode.
   - `BroadcastReceiver` system recovery for `BOOT_COMPLETED`, `TIMEZONE_CHANGED`, `TIME_SET`, and package updates.
   - Full-Screen `AlarmActivity` ringing screen with high-contrast actions: **Mark Taken / Eaten**, **Snooze 10m**, and **Skip**.
   - Vibration and custom notification sound support.

5. **History, Hydration & Reports**
   - Daily and weekly adherence tracking with completion percentages.
   - Hydration water glass counter.
   - Exportable summary reports.
   - Bilingual support: **English** & **Bangla (বাংলা)**.

---

## Technology Stack

- **Language:** Kotlin 2.2
- **UI Toolkit:** Jetpack Compose & Material Design 3
- **Architecture:** Clean MVVM (Model-View-ViewModel) + Repository Pattern
- **Persistence:** Room Database 2.7 (with KSP)
- **Preferences:** Jetpack DataStore Preferences
- **Scheduling:** AlarmManager, NotificationManager, BroadcastReceiver
- **Concurrency:** Kotlin Coroutines & Flow
- **Typography:** Plus Jakarta Sans

---

## Build & APK Generation Instructions

### Debug APK Build
To compile and assemble the debug APK:
```bash
gradle :app:assembleDebug
```
The output APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

### Release APK Build & Signing
To build an optimized release APK:
```bash
gradle :app:assembleRelease
```
Ensure your signing credentials are configured in your environment or CI/CD pipeline:
```bash
export KEYSTORE_PATH="/path/to/your/upload-keystore.jks"
export STORE_PASSWORD="your-keystore-password"
export KEY_PASSWORD="your-key-password"
```
The output APK will be generated at:
`app/build/outputs/apk/release/app-release.apk`

### Running Tests
To run local JVM Robolectric and unit tests:
```bash
gradle :app:testDebugUnitTest
```
