# Health Monitoring — 4 · Presentation: the ViewModel

Guide 3 done. Use cases work. Now ViewModel.
No screen yet. Screen next session. Today: brain only.

> Rule same as before: **Try it** first. Answer key folded. Peek only after trying.
> Copy shape from `emergencyalerting/presentation/activealerts/ActiveAlertsViewModel.kt`. Same pattern.

---

## 0. Big picture (read once)

```
Screen (Compose) ──collects──► uiState: StateFlow<LiveVitalsUiState>
       │                                  ▲
       └──calls──► ViewModel.retry() ─────┤ _uiState.update { … }
                        │                 │
                        ▼                 │
        GetLiveVitalSignsUseCase ── Result ┘
        GetWearableDevicesUseCase
```

ViewModel job:
- Call use cases. Never repository.
- Hold **one** state object. Screen read it. Screen draw it.
- Survive rotation. Screen die, ViewModel live.
- Screen never call backend. Screen only call ViewModel functions.

Three pieces today:

| File | What |
|---|---|
| `LiveVitalsUiState.kt` | Data class. Everything screen draw. |
| `VitalSignLabels.kt` | `VitalSignType` → Spanish label. |
| `LiveVitalsViewModel.kt` | Load. Refresh. Push state. |

Folder: `features/healthmonitoring/presentation/livevitals/`

---

## Step 1 — `LiveVitalsUiState`

State = snapshot of screen. Immutable. `data class`. Every field has default.
New state? `copy(...)`. Never mutate.

**What screen need?** Look Figma Home:
- Loading spinner → `isLoading`
- Error box → `errorMessage`
- "Elena está bien" → `careRecipientFirstName`
- Vital cards → `vitals: LiveVitalSigns?` (null = nothing loaded yet)
- "Pulsera conectada" → `hasWristband`

Then **derived** values. Not stored. Computed with `get()`. Like `canRemove` in `EmergencyContactsUiState`.
- `allWithinRange` → "Elena está bien" vs warning
- `hasLiveSignal` → "En vivo" vs "Última lectura"

Domain already know both (`LiveVitalSigns.allWithinRange`, `hasLiveSignal`). Reuse. No copy logic.
But `vitals` can be null. Handle null.

**Try it:**
- `data class LiveVitalsUiState(...)` with 5 fields above.
- Two `val ... : Boolean get() = ...` using `vitals?.` and `?: false`.

> 🤔 Why `allWithinRange` false when `vitals == null`? Nothing loaded. Caveman no say "Elena está bien" without proof.

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns

data class LiveVitalsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val careRecipientFirstName: String = "",
    // Null until the first successful load; a later failure keeps the last values
    val vitals: LiveVitalSigns? = null,
    val hasWristband: Boolean = false
) {
    /** No readings yet means nothing to vouch for, so it is not "all good". */
    val allWithinRange: Boolean get() = vitals?.allWithinRange ?: false

    val hasLiveSignal: Boolean get() = vitals?.hasLiveSignal ?: false
}
```
</details>

---

## Step 2 — Spanish labels

Backend `typeName` = English ("Heart rate"). Figma = Spanish ("Ritmo cardíaco").
Label is **presentation** job. Not domain. Not backend.
So extension function in presentation.

Figma labels:

| `VitalSignType` | Label | Unit on screen |
|---|---|---|
| `HR` | Ritmo cardíaco | lpm |
| `BP_SYS` / `BP_DIA` | Presión arterial | mmHg |
| `SPO2` | Saturación | % |
| `TEMP` | Temperatura | °C |
| `RESP_RATE` | Respiración | rpm |

**Try it:** file `VitalSignLabels.kt`. Two extension vals on `VitalSignType`: `label` and `displayUnit`.
Use `when (this)`. No `else` branch.

> 🤔 Why no `else`? Enum `when` without `else` must cover all. Somebody add new type → compile break → you notice. `else` hide it.

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType

// The backend sends English names; the app speaks Spanish
val VitalSignType.label: String
    get() = when (this) {
        VitalSignType.HR -> "Ritmo cardíaco"
        VitalSignType.BP_SYS, VitalSignType.BP_DIA -> "Presión arterial"
        VitalSignType.SPO2 -> "Saturación"
        VitalSignType.TEMP -> "Temperatura"
        VitalSignType.RESP_RATE -> "Respiración"
    }

val VitalSignType.displayUnit: String
    get() = when (this) {
        VitalSignType.HR -> "lpm"
        VitalSignType.BP_SYS, VitalSignType.BP_DIA -> "mmHg"
        VitalSignType.SPO2 -> "%"
        VitalSignType.TEMP -> "°C"
        VitalSignType.RESP_RATE -> "rpm"
    }
```
</details>

---

## Step 3 — ViewModel skeleton

Four parts. Always same four:

1. `@HiltViewModel` + `@Inject constructor(...)` → Hilt give use cases.
2. `private val _uiState = MutableStateFlow(...)` → ViewModel write here.
3. `val uiState: StateFlow<...> = _uiState.asStateFlow()` → screen read here. Read only.
4. `init { ... }` → start loading when ViewModel born.

> 🤔 Why two flows, `_uiState` and `uiState`? Screen get read-only door. Only ViewModel change state. One writer. No chaos.

**Try it:**
- Class `LiveVitalsViewModel`, extends `ViewModel()`.
- Inject `GetLiveVitalSignsUseCase` and `GetWearableDevicesUseCase`.
  Name params `getLiveVitalSigns`, `getWearableDevices` → call reads like verb: `getLiveVitalSigns(id)`. That why `operator fun invoke` exist (guide 3).
- Start state with `careRecipientFirstName = DemoSession.CARE_RECIPIENT_FIRST_NAME`.
- `init` calls `load()`. Leave `load()` empty for now.

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetLiveVitalSignsUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetWearableDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class LiveVitalsViewModel @Inject constructor(
    private val getLiveVitalSigns: GetLiveVitalSignsUseCase,
    private val getWearableDevices: GetWearableDevicesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LiveVitalsUiState(careRecipientFirstName = DemoSession.CARE_RECIPIENT_FIRST_NAME)
    )
    val uiState: StateFlow<LiveVitalsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        // Step 4
    }
}
```
</details>

Compile now. Catch typos early:
```bash
./gradlew :app:compileDebugKotlin
```

---

## Step 4 — `load()` + `refresh()`

Split in two. Same as `ActiveAlertsViewModel`:

- `load()` → public. Show spinner. Then `refresh()`. Screen "Reintentar" button call this.
- `refresh()` → `private suspend`. No spinner. Fetch + update. Auto-refresh (step 5) reuse it.

> 🤔 Why split? Auto-refresh every 10 s. Spinner every 10 s = screen blink. Ugly. So spinner only on first load / retry.

Inside `refresh()`:

1. Call `getWearableDevices(...)`. Wristband nice-to-have. Fail? Not fatal.
   → `.getOrDefault(emptyList())`, then `.any { it.deviceType == DeviceType.WRISTBAND }`.
2. Call `getLiveVitalSigns(...)`. Main data. Fail? Show error.
   → `.onSuccess { ... }` / `.onFailure { ... }`.
3. Every `_uiState.update { it.copy(...) }` must set `isLoading = false` at the end. Forget it → spinner forever.

Important on failure: **don't clear `vitals`**. Old reading better than empty screen. `copy` keep fields you don't touch. So just don't touch it.

Error text: `e.message ?: "No se pudieron cargar los signos vitales"`.
`apiCall` already give Spanish messages ("No se pudo conectar con el servidor"). Fallback only for null.

**Try it:** write `load()` with `viewModelScope.launch { ... }`, and `private suspend fun refresh()`.

> 🤔 Why `viewModelScope`? ViewModel die → scope cancel → requests stop. No leak. No update to dead screen.

<details><summary>Answer key</summary>

```kotlin
// add imports:
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.DeviceType
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            refresh()
        }
    }

    private suspend fun refresh() {
        // Without the device list the vitals still make sense, so its failure is not shown
        val hasWristband = getWearableDevices(DemoSession.CARE_RECIPIENT_PROFILE_ID)
            .getOrDefault(emptyList())
            .any { it.deviceType == DeviceType.WRISTBAND }

        getLiveVitalSigns(DemoSession.CARE_RECIPIENT_PROFILE_ID)
            .onSuccess { vitals ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = null, vitals = vitals, hasWristband = hasWristband)
                }
            }
            .onFailure { e ->
                // vitals is left as it was: the last known readings stay on screen
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasWristband = hasWristband,
                        errorMessage = e.message ?: "No se pudieron cargar los signos vitales"
                    )
                }
            }
    }
```
</details>

---

## Step 5 — Auto-refresh

Wristband send reading often. Backend mark `liveSignal = false` after 60 s.
Screen must stay fresh. So poll. Every 10 s.

Pattern from `ActiveAlertsViewModel.startAutoRefresh()`:
- `viewModelScope.launch { while (true) { delay(...); refresh() } }`
- Skip tick if `isLoading` already true. No double request.
- Number in `private companion object` as `const val`. No magic number.

> 🤔 `while (true)` forever? Scary? No. `delay` is suspend point. Scope cancel → `delay` throw `CancellationException` → loop end. Safe.

**Try it:** add `startAutoRefresh()`, call it in `init` after `load()`.

<details><summary>Answer key</summary>

```kotlin
// add import:
import kotlinx.coroutines.delay

    init {
        load()
        startAutoRefresh()
    }

    // Readings arrive on the server all the time, so they are read again periodically
    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(AUTO_REFRESH_MS)
                if (!_uiState.value.isLoading) refresh()
            }
        }
    }

    private companion object {
        const val AUTO_REFRESH_MS = 10_000L
    }
```
</details>

---

## Step 6 — Full file check

Compare yours. Order: constructor → state → `init` → public fns → private fns → companion.

<details><summary>Full <code>LiveVitalsViewModel.kt</code></summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetLiveVitalSignsUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.application.GetWearableDevicesUseCase
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.DeviceType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LiveVitalsViewModel @Inject constructor(
    private val getLiveVitalSigns: GetLiveVitalSignsUseCase,
    private val getWearableDevices: GetWearableDevicesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LiveVitalsUiState(careRecipientFirstName = DemoSession.CARE_RECIPIENT_FIRST_NAME)
    )
    val uiState: StateFlow<LiveVitalsUiState> = _uiState.asStateFlow()

    init {
        load()
        startAutoRefresh()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            refresh()
        }
    }

    private suspend fun refresh() {
        // Without the device list the vitals still make sense, so its failure is not shown
        val hasWristband = getWearableDevices(DemoSession.CARE_RECIPIENT_PROFILE_ID)
            .getOrDefault(emptyList())
            .any { it.deviceType == DeviceType.WRISTBAND }

        getLiveVitalSigns(DemoSession.CARE_RECIPIENT_PROFILE_ID)
            .onSuccess { vitals ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = null, vitals = vitals, hasWristband = hasWristband)
                }
            }
            .onFailure { e ->
                // vitals is left as it was: the last known readings stay on screen
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasWristband = hasWristband,
                        errorMessage = e.message ?: "No se pudieron cargar los signos vitales"
                    )
                }
            }
    }

    // Readings arrive on the server all the time, so they are read again periodically
    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(AUTO_REFRESH_MS)
                if (!_uiState.value.isLoading) refresh()
            }
        }
    }

    private companion object {
        const val AUTO_REFRESH_MS = 10_000L
    }
}
```
</details>

---

## Step 7 — Compile, prove, commit

```bash
./gradlew :app:compileDebugKotlin
```

**Prove it (no screen yet, throw-away):** backend local up + seeded (guide 3, step 3).
Temporarily add in `init`, after `startAutoRefresh()`:

```kotlin
viewModelScope.launch { uiState.collect { Log.d("HM", it.toString()) } }
```

Problem: nobody create ViewModel yet. No screen → no `hiltViewModel()`.
Quick hack: in any existing screen composable, add `val vm: LiveVitalsViewModel = hiltViewModel()`.
Open that screen. Logcat filter `HM`.

| You see | Meaning |
|---|---|
| `isLoading=true` then `vitals=LiveVitalSigns(...)`, `hasWristband=true` | 🎉 works |
| New line every ~10 s | auto-refresh alive |
| `vitals=null, errorMessage=No se pudo conectar…` | backend down / `adb reverse` missing |
| `hasWristband=false` but vitals fine | device not linked → redo 3.2 step 1 |

Then **remove hack**. Log line + `hiltViewModel()` line both. `git restore` the screen file.

```bash
git add app/src/main/java/com/example/guardian_plus_mobile_app/features/healthmonitoring/presentation
git commit -m "feat(health-monitoring): add live vitals ui state and view model"
```

---

## Done ✅ — what screen get next session

```kotlin
val state by viewModel.uiState.collectAsStateWithLifecycle()

state.isLoading                                // spinner
state.errorMessage                             // error box + "Reintentar" → viewModel.load()
state.vitals?.get(VitalSignType.HR)?.value     // 78.0
VitalSignType.HR.label                         // "Ritmo cardíaco"
state.allWithinRange                           // "Elena está bien"
state.hasWristband                             // "Pulsera conectada"
```

Leftovers for screen session:
- Blood pressure = two readings. Format `"${sys.toInt()}/${dia.toInt()}"`. Screen job, or small helper.
- "Hace 1 min" → from `measuredAt`. Needs a clock. Look `core/time/ServerClock.kt`.
- Battery 68 % → not in backend. Hide or hard-code.

---

## Exam questions

Answer out loud. No peek.

1. Why `_uiState` private and `uiState` public? What break if screen could write?
2. Why `UiState` is `data class` with `val`s, not `var`s?
3. Where `allWithinRange` live: domain, UiState, or both? Why UiState version handle null?
4. Why Spanish labels in presentation, not domain?
5. Why `load()` show spinner but `refresh()` not?
6. Wearable call fail. What user see? Why that choice?
7. Vitals call fail on 3rd auto-refresh. What happen to numbers on screen? Which line make that happen?
8. Rotate phone. Request fire again? Why / why not?
9. Leave screen. Does `while (true)` keep running? What stop it?
10. Why ViewModel inject use case, not `VitalSignRepository`?
