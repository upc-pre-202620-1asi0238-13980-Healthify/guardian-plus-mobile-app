# Guardian+ — Login Screen Code Guide

Step-by-step guide to finish the login screen, add the backend (Retrofit + Hilt), and handle the session token (DataStore).
The architecture mirrors the `iam` feature of **easyvet-mobile**, and the package names are adapted to Guardian+.

> Base package: `com.example.guardian_plus_mobile_app`
> All feature paths are relative to `app/src/main/java/com/example/guardian_plus_mobile_app/`

Final structure:

```
GuardianApp.kt
MainActivity.kt
core/di/NetworkModule.kt
navigation/AppNavHost.kt
features/
├── auth/
│   ├── domain/
│   │   ├── model/UserAccount.kt
│   │   └── repositories/AuthRepository.kt
│   ├── application/LoginUseCase.kt
│   ├── infrastructure/
│   │   ├── remote/LoginRequestDto.kt
│   │   ├── remote/LoginResponseDto.kt
│   │   ├── remote/AuthService.kt
│   │   ├── remote/AuthInterceptor.kt
│   │   ├── local/TokenManager.kt
│   │   ├── repositories/AuthRepositoryImpl.kt
│   │   ├── repositories/FakeAuthRepository.kt      (optional)
│   │   └── di/AuthApiModule.kt, AuthRepositoryModule.kt, AuthDataStoreModule.kt
│   └── presentation/
│       ├── LoginUiState.kt
│       ├── LoginContent.kt      (stateless UI, all the design)
│       ├── LoginScreen.kt       (stateful wrapper)
│       ├── LoginViewModel.kt
│       ├── SessionViewModel.kt
│       └── navigation/AuthNavGraph.kt
└── home/presentation/HomeScreen.kt   (placeholder)
```

| Part | What | Commit |
|---|---|---|
| 1 | UI (no backend yet) | `feat: add login form` |
| 2 | Gradle dependencies | `chore: add hilt, retrofit, datastore` |
| 3 | Hilt bootstrap | `chore: setup hilt application` |
| 4 | Domain + infrastructure (backend) | `feat: add auth domain and infrastructure` |
| 5 | ViewModel and wiring the screen | `feat: add login viewmodel` |
| 6 | Token: send it on every request | `feat: attach auth token to requests` |
| 7 | Navigation + auto-login + logout | `feat: add auth navigation and session` |

---

## Part 1 — UI

The idea is to split the screen in two:
- **`LoginContent`**: *stateless*. It only receives `state` + callbacks and draws the design. It can be previewed.
- **`LoginScreen`**: *stateful*. It owns the state. For now it uses `remember`; in Part 5 it switches to the ViewModel **without touching `LoginContent`**.

### 1.1 `features/auth/presentation/LoginUiState.kt` (new)

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.presentation

import com.example.guardian_plus_mobile_app.features.auth.domain.model.UserAccount

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordHidden: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val user: UserAccount? = null,
    val isAuthenticated: Boolean = false
)
```

### 1.2 `features/auth/presentation/LoginContent.kt` (new)

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.ui.theme.BrandGreen
import com.example.guardian_plus_mobile_app.ui.theme.FieldBorder
import com.example.guardian_plus_mobile_app.ui.theme.GuardianplusmobileappTheme
import com.example.guardian_plus_mobile_app.ui.theme.LabelCaps
import com.example.guardian_plus_mobile_app.ui.theme.MintBg
import com.example.guardian_plus_mobile_app.ui.theme.SheetBg
import com.example.guardian_plus_mobile_app.ui.theme.TextMuted
import com.example.guardian_plus_mobile_app.ui.theme.TextPrimary

@Composable
fun LoginContent(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    onForgotPassword: () -> Unit = {},
    onRegister: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onBiometricClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SheetBg)
            .verticalScroll(rememberScrollState())
    ) {
        LoginHeader()

        // Sheet: it goes up 24dp over the green header
        Surface(
            color = SheetBg,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 40.dp, height = 4.dp)
                        .background(FieldBorder, RoundedCornerShape(2.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Iniciar sesión",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Accede a tu cuenta de cuidador",
                    color = TextMuted,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Email
                FieldLabel(text = "CORREO ELECTRÓNICO")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    placeholder = { Text(text = "nombre@correo.com", color = TextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
œ                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = loginFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password
                FieldLabel(text = "CONTRASEÑA")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    placeholder = { Text(text = "••••••••", color = TextMuted) },
                    singleLine = true,
                    visualTransformation = if (state.isPasswordHidden)
                        PasswordVisualTransformation()
                    else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { onLoginClick() }),
                    trailingIcon = {
                        IconButton(onClick = onTogglePasswordVisibility) {
                            Icon(
                                imageVector = if (state.isPasswordHidden)
                                    Icons.Outlined.Visibility
                                else Icons.Outlined.VisibilityOff,
                                contentDescription = if (state.isPasswordHidden)
                                    "Mostrar contraseña"
                                else "Ocultar contraseña",
                                tint = TextMuted
                            )
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = loginFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = BrandGreen,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable(onClick = onForgotPassword)
                )

                // Error message (only if there is one)
                state.errorMessage?.let { message ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Main button
                Button(
                    onClick = onLoginClick,
                    enabled = !state.isLoading,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = Color.White,
                        disabledContainerColor = BrandGreen.copy(alpha = 0.7f),
                        disabledContentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.VerifiedUser,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ingresar de forma segura",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))


                // Divider "o continúa con"
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = FieldBorder)
                    Text(
                        text = "o continúa con",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = FieldBorder)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Google
                OutlinedButton(
                    onClick = onGoogleClick,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, FieldBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = null,
                        tint = Color.Unspecified, // keeps the original Google colors
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Continuar con Google", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Biometric
                Button(
                    onClick = onBiometricClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MintBg,
                        contentColor = BrandGreen
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Fingerprint,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Acceso biométrico", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Register
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(text = "¿No tienes cuenta? ", color = TextMuted, fontSize = 15.sp)
                    Text(
                        text = "Crear cuenta",
                        color = BrandGreen,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(onClick = onRegister)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Terms
                Text(
                    text = buildAnnotatedString {
                        append("Al ingresar, aceptas los ")
                        withStyle(SpanStyle(color = BrandGreen)) { append("Términos de uso") }
                        append(" y la ")
                        withStyle(SpanStyle(color = BrandGreen)) { append("Política de privacidad") }
                    },
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun LoginHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandGreen)
            .statusBarsPadding()
            .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 56.dp)
    ) {
        // Logo + brand
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.VerifiedUser,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "PLATAFORMA DE SALUD",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Guardian+",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Cuidado\nconectado.",
            color = Color.White,
            fontSize = 40.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Monitoreo en tiempo real para quienes más importan.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            HeaderStat(value = "24/7", label = "Monitoreo")
            HeaderStat(value = "5s", label = "Actualización")
            HeaderStat(value = "99.8%", label = "Disponibilidad")
        }
    }
}

@Composable
private fun HeaderStat(value: String, label: String) {
    Column {
        Text(
            text = value,
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = LabelCaps,
        fontSize = 12.sp,
        letterSpacing = 1.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedBorderColor = BrandGreen,
    unfocusedBorderColor = FieldBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = BrandGreen
)

@Preview(showBackground = true, heightDp = 1500)
@Composable
private fun LoginContentPreview() {
    GuardianplusmobileappTheme(dynamicColor = false) {
        LoginContent(
            state = LoginUiState(email = "nombre@correo.com", password = "12345678"),
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onLoginClick = {}
        )
    }
}
```

### 1.3 `features/auth/presentation/LoginScreen.kt` (replace the whole file)

Temporary version that uses local state and a fake `delay`. Part 5 replaces it.

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onRegister: () -> Unit = {}
) {
    var state by remember { mutableStateOf(LoginUiState()) }
    val scope = rememberCoroutineScope()

    LoginContent(
        state = state,
        modifier = modifier,
        onEmailChange = { state = state.copy(email = it, errorMessage = null) },
        onPasswordChange = { state = state.copy(password = it, errorMessage = null) },
        onTogglePasswordVisibility = {
            state = state.copy(isPasswordHidden = !state.isPasswordHidden)
        },
        onLoginClick = {
            if (state.email.isBlank() || state.password.isBlank()) {
                state = state.copy(errorMessage = "Completa todos los campos para continuar")
            } else {
                state = state.copy(isLoading = true, errorMessage = null)
                scope.launch {
                    delay(1200)
                    state = state.copy(isLoading = false)
                    onLoginSuccess()
                }
            }
        },
        onForgotPassword = onForgotPassword,
        onRegister = onRegister
    )
}
```

### 1.4 `MainActivity.kt` (replace the whole file)

Remove the `Scaffold`. Its `innerPadding` plus the header's `statusBarsPadding()` would pad the top twice, leaving a white strip above the green header.

```kotlin
package com.example.guardian_plus_mobile_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.guardian_plus_mobile_app.features.auth.presentation.LoginScreen
import com.example.guardian_plus_mobile_app.ui.theme.GuardianplusmobileappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GuardianplusmobileappTheme(dynamicColor = false) {
                LoginScreen()
            }
        }
    }
}
```

✅ **Checkpoint 1:** run the app or open the preview and compare it with the mockup. With empty fields the error should appear; with text the button should show the spinner for 1.2s.

---

## Part 2 — Gradle

### 2.1 `gradle/libs.versions.toml` (replace the whole file)

> ⚠️ `kotlin` goes up from `2.2.10` to `2.4.20` so it matches the `ksp` / `hilt` versions that already work in easyvet.

```toml
[versions]
agp = "9.4.1"
coreKtx = "1.19.1"
junit = "4.13.2"
junitVersion = "1.3.0"
espressoCore = "3.7.0"
lifecycleRuntimeKtx = "2.11.0"
activityCompose = "1.13.0"
kotlin = "2.4.20"
composeBom = "2026.02.01"
navigationCompose = "2.9.5"
coreSplashscreen = "1.0.1"
ksp = "2.3.4"
hilt = "2.60.1"
hiltNavigationCompose = "1.4.0"
retrofit = "3.0.0"
datastorePreferences = "1.2.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-compose-material-icons-core = { group = "androidx.compose.material", name = "material-icons-core" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
androidx-core-splashscreen = { group = "androidx.core", name = "core-splashscreen", version.ref = "coreSplashscreen" }

retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }
converter-gson = { module = "com.squareup.retrofit2:converter-gson", version.ref = "retrofit" }

hilt-android = { module = "com.google.dagger:hilt-android", version.ref = "hilt" }
hilt-android-compiler = { module = "com.google.dagger:hilt-android-compiler", version.ref = "hilt" }
androidx-hilt-navigation-compose = { module = "androidx.hilt:hilt-navigation-compose", version.ref = "hiltNavigationCompose" }

androidx-datastore-preferences = { module = "androidx.datastore:datastore-preferences", version.ref = "datastorePreferences" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

### 2.2 Root `build.gradle.kts` (replace the whole file)

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
```

### 2.3 `app/build.gradle.kts`: `plugins` and `dependencies` blocks

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// android { ... }  ← leave it exactly as it is

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material.icons.core)

    // ViewModel + lifecycle-aware state
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // DataStore (token)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
```

✅ **Checkpoint 2:** *Sync Now*, then *Build > Make Project*. It should build with no errors.

---

## Part 3 — Hilt bootstrap

### 3.1 `GuardianApp.kt` (new, in the root package next to `MainActivity`)

```kotlin
package com.example.guardian_plus_mobile_app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GuardianApp : Application()
```

### 3.2 `AndroidManifest.xml` (replace the whole file)

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:name=".GuardianApp"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Guardianplusmobileapp">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.Guardianplusmobileapp"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

### 3.3 `MainActivity.kt`: add `@AndroidEntryPoint`

```kotlin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // ...same as before
}
```

✅ **Checkpoint 3:** the app opens just as it did in Part 1. If it crashes with *"Hilt Activity must be attached to an @HiltAndroidApp Application"*, the `android:name` is missing from the manifest.

---

## Part 4 — Backend (domain → application → infrastructure)

Build it in this order. Each layer only knows about the ones before it.

### 4.1 `features/auth/domain/model/UserAccount.kt` (replace)

> Adjust the fields to what **your** backend returns. The **token does NOT go here**, because it's an infrastructure detail.

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.domain.model

data class UserAccount(
    val id: Int,
    val fullName: String,
    val email: String
)
```

### 4.2 `features/auth/domain/repositories/AuthRepository.kt` (new)

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.domain.repositories

import com.example.guardian_plus_mobile_app.features.auth.domain.model.UserAccount
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<UserAccount>

    fun isLoggedIn(): Flow<Boolean>   // used in Part 7 (auto-login)

    suspend fun logout()              // used in Part 7
}
```

### 4.3 `features/auth/application/LoginUseCase.kt` (new)

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.application

import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String) =
        repository.login(email, password)
}
```

### 4.4 `features/auth/infrastructure/remote/LoginRequestDto.kt` (new)

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote

data class LoginRequestDto(
    val email: String,
    val password: String
)
```

### 4.5 `features/auth/infrastructure/remote/LoginResponseDto.kt` (new)

> ⚠️ Gson maps by **exact name**. The fields must match your backend's JSON exactly.
> If your API returns e.g. `"access_token"`, use `@SerializedName("access_token") val token: String`.

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote

data class LoginResponseDto(
    val id: Int,
    val fullName: String,
    val email: String,
    val token: String
)
```

### 4.6 `features/auth/infrastructure/remote/AuthService.kt` (new)

> Replace `"authentication/sign-in"` with your backend's real endpoint. Don't start it with `/`.

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {

    @POST("authentication/sign-in")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>
}
```

### 4.7 `core/di/NetworkModule.kt` (new)

> `BASE_URL` **must end with `/`**, or Retrofit throws an exception.

```kotlin
package com.example.guardian_plus_mobile_app.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://guardian-plus-api.example.com/api/v1/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
```

### 4.8 `features/auth/infrastructure/di/AuthDataStoreModule.kt` (new)

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.infrastructure.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Top-level: there must be only ONE DataStore instance per file
private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Module
@InstallIn(SingletonComponent::class)
object AuthDataStoreModule {

    @Provides
    @Singleton
    fun provideAuthDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.authDataStore
    }
}
```

### 4.9 `features/auth/infrastructure/local/TokenManager.kt` (new)

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.infrastructure.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenManager @Inject constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        val TOKEN = stringPreferencesKey("token")
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN)
        }
    }

    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN]
        }
    }
}
```

### 4.10 `features/auth/infrastructure/repositories/AuthRepositoryImpl.kt` (new)

Same as easyvet, with two improvements:
- Readable error messages. `response.message()` usually comes back empty on HTTP/2.
- A separate `IOException` catch for when there's no internet.

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.infrastructure.repositories

import com.example.guardian_plus_mobile_app.features.auth.domain.model.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.local.TokenManager
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.AuthService
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.LoginRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<UserAccount> {
        return try {
            val response = service.login(LoginRequestDto(email, password))
            val body = response.body()

            if (response.isSuccessful && body != null) {
                // 🔑 This is where the token gets saved
                tokenManager.saveToken(body.token)

                val user = UserAccount(
                    id = body.id,
                    fullName = body.fullName,
                    email = body.email
                )
                Result.success(user)
            } else {
                val message = when (response.code()) {
                    400, 401, 404 -> "Correo o contraseña incorrectos"
                    else -> "Error del servidor (${response.code()})"
                }
                Result.failure(Exception(message))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión. Revisa tu internet e inténtalo de nuevo"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return tokenManager.getToken().map { token -> !token.isNullOrBlank() }
    }

    override suspend fun logout() {
        tokenManager.clearToken()
    }
}
```

### 4.11 `features/auth/infrastructure/repositories/FakeAuthRepository.kt` (optional)

Use it **while you don't have a backend**. Demo credentials: `demo@guardian.com` / `123456`.

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.infrastructure.repositories

import com.example.guardian_plus_mobile_app.features.auth.domain.model.UserAccount
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.local.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FakeAuthRepository @Inject constructor(
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<UserAccount> {
        delay(1000) // simulate the network
        return if (email == "demo@guardian.com" && password == "123456") {
            tokenManager.saveToken("fake-jwt-token")
            Result.success(UserAccount(id = 1, fullName = "Cuidador Demo", email = email))
        } else {
            Result.failure(Exception("Correo o contraseña incorrectos"))
        }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return tokenManager.getToken().map { token -> !token.isNullOrBlank() }
    }

    override suspend fun logout() {
        tokenManager.clearToken()
    }
}
```

### 4.12 `features/auth/infrastructure/di/AuthApiModule.kt` (new)

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.infrastructure.di

import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.AuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthApiModule {

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }
}
```

### 4.13 `features/auth/infrastructure/di/AuthRepositoryModule.kt` (new)

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.infrastructure.di

import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.repositories.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AuthRepositoryModule {

    // Without a backend? Change AuthRepositoryImpl → FakeAuthRepository (here and in the import)
    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
```

> 📝 **Exam question: `@Provides` vs `@Binds`.**
> `@Provides` (in an `object`) is used when **you** have to build the instance, e.g. `retrofit.create(...)` or a builder.
> `@Binds` (in an `interface`) only tells Hilt *"when someone asks for `AuthRepository`, give them `AuthRepositoryImpl`"*. Hilt already knows how to build the impl through its `@Inject constructor`.

✅ **Checkpoint 4:** *Make Project* builds. Hilt validates the dependency graph at compile time, so a missing `@Inject` or module shows up as a build error here.

---

## Part 5 — ViewModel and wiring the screen

### 5.1 `features/auth/presentation/LoginViewModel.kt` (new)

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.auth.application.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _state.update { it.copy(isPasswordHidden = !it.isPasswordHidden) }
    }

    fun login() {
        val email = _state.value.email.trim()
        val password = _state.value.password

        if (email.isBlank() || password.isBlank()) {
            _state.update { it.copy(errorMessage = "Completa todos los campos para continuar") }
            return
        }
        if (_state.value.isLoading) return // avoid double clicks

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        // No Dispatchers.IO needed: Retrofit's suspend functions already run off the main thread
        viewModelScope.launch {
            loginUseCase(email, password)
                .onSuccess { user ->
                    _state.update {
                        it.copy(isLoading = false, user = user, isAuthenticated = true)
                    }
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "No se pudo iniciar sesión"
                        )
                    }
                }
        }
    }
}
```

### 5.2 `features/auth/presentation/LoginScreen.kt` (replace the whole file)

`LoginContent` **doesn't change**. Only the state source does.

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onRegister: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // When isAuthenticated changes to true → navigate (only once)
    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onLoginSuccess()
        }
    }

    LoginContent(
        state = state,
        modifier = modifier,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onLoginClick = viewModel::login,
        onForgotPassword = onForgotPassword,
        onRegister = onRegister
    )
}
```

> If Android Studio can't find `androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel`, try `androidx.hilt.navigation.compose.hiltViewModel` (the older package; both come with `hilt-navigation-compose`).

✅ **Checkpoint 5:** with `FakeAuthRepository`, the wrong password shows the error and `demo@guardian.com` / `123456` shows the spinner and logs in.
To see the saved token: *View > Tool Windows > Device Explorer* → `data/data/com.example.guardian_plus_mobile_app/files/datastore/session.preferences_pb`.

---

## Part 6 — Token: send it on every request

easyvet **saves** the token but **never sends it**: its `CartService` doesn't include it. Guardian+ needs authenticated endpoints (patients, vitals, alerts…), so an **OkHttp interceptor** adds `Authorization: Bearer <token>` to every request automatically.

### 6.1 `features/auth/infrastructure/remote/AuthInterceptor.kt` (new)

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.auth.infrastructure.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // OkHttp runs this on a background thread, so runBlocking is OK here
        val token = runBlocking { tokenManager.getToken().first() }

        val request = if (token.isNullOrBlank()) {
            chain.request()
        } else {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }
        return chain.proceed(request)
    }
}
```

### 6.2 `core/di/NetworkModule.kt` (replace the whole file)

```kotlin
package com.example.guardian_plus_mobile_app.core.di

import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://guardian-plus-api.example.com/api/v1/"

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
```

> OkHttp already comes with Retrofit. If `okhttp3` shows up red anyway, add `implementation("com.squareup.okhttp3:okhttp:5.1.0")`.

Full token flow:

```
Login OK ──► AuthRepositoryImpl.saveToken() ──► DataStore ("session")
                                                   │
Any request ──► AuthInterceptor ──► reads token ◄──┘ ──► "Authorization: Bearer ..."
                                                   │
App start ──► SessionViewModel.isLoggedIn ◄────────┘  (Part 7)
Logout ──► clearToken() ──► DataStore empty
```

---

## Part 7 — Navigation, auto-login and logout

### 7.1 `features/auth/presentation/SessionViewModel.kt` (new)

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    // null = still checking, true = has a token, false = no token
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            // .first(): we only care about the value at startup
            _isLoggedIn.value = repository.isLoggedIn().first()
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onDone() // navigate only AFTER the token has been cleared
        }
    }
}
```

### 7.2 `features/home/presentation/HomeScreen.kt` (new, placeholder)

```kotlin
package com.example.guardian_plus_mobile_app.features.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.guardian_plus_mobile_app.features.auth.presentation.SessionViewModel
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        Text(text = "Bienvenido a Guardian+", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { sessionViewModel.logout(onDone = onLogout) }) {
            Text(text = "Cerrar sesión")
        }
    }
}
```

### 7.3 `features/auth/presentation/navigation/AuthNavGraph.kt` (new)

```kotlin
package com.example.guardian_plus_mobile_app.features.auth.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.guardian_plus_mobile_app.features.auth.presentation.LoginScreen
import com.example.guardian_plus_mobile_app.features.home.presentation.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
data object AuthNavGraphRoute

@Serializable
data object LoginRoute

@Serializable
data object RegisterRoute

fun NavGraphBuilder.authNavGraph(navController: NavController) {

    navigation<AuthNavGraphRoute>(startDestination = LoginRoute) {

        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(HomeRoute) {
                        // remove the whole auth graph so "Back" doesn't return to login
                        popUpTo<AuthNavGraphRoute> { inclusive = true }
                    }
                },
                onRegister = { navController.navigate(RegisterRoute) }
            )
        }

        composable<RegisterRoute> {
            // TODO: RegisterScreen
        }
    }
}
```

### 7.4 `navigation/AppNavHost.kt` (new)

```kotlin
package com.example.guardian_plus_mobile_app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.guardian_plus_mobile_app.features.auth.presentation.navigation.AuthNavGraphRoute
import com.example.guardian_plus_mobile_app.features.auth.presentation.navigation.authNavGraph
import com.example.guardian_plus_mobile_app.features.home.presentation.HomeRoute
import com.example.guardian_plus_mobile_app.features.home.presentation.HomeScreen

@Composable
fun AppNavHost(navController: NavHostController, startDestination: Any) {

    NavHost(navController = navController, startDestination = startDestination) {

        authNavGraph(navController)

        composable<HomeRoute> {
            HomeScreen(
                onLogout = {
                    navController.navigate(AuthNavGraphRoute) {
                        popUpTo<HomeRoute> { inclusive = true }
                    }
                }
            )
        }
    }
}
```

### 7.5 `MainActivity.kt` (final version, replace the whole file)

```kotlin
package com.example.guardian_plus_mobile_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.guardian_plus_mobile_app.features.auth.presentation.SessionViewModel
import com.example.guardian_plus_mobile_app.features.auth.presentation.navigation.AuthNavGraphRoute
import com.example.guardian_plus_mobile_app.features.home.presentation.HomeRoute
import com.example.guardian_plus_mobile_app.navigation.AppNavHost
import com.example.guardian_plus_mobile_app.ui.theme.BrandGreen
import com.example.guardian_plus_mobile_app.ui.theme.GuardianplusmobileappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GuardianplusmobileappTheme(dynamicColor = false) {
                val sessionViewModel: SessionViewModel = hiltViewModel()
                val isLoggedIn by sessionViewModel.isLoggedIn.collectAsStateWithLifecycle()

                when (val loggedIn = isLoggedIn) {
                    // Reading DataStore (a few ms): show the brand color instead of flashing the login
                    null -> Box(modifier = Modifier.fillMaxSize().background(BrandGreen))
                    else -> AppNavHost(
                        navController = rememberNavController(),
                        startDestination = if (loggedIn) HomeRoute else AuthNavGraphRoute
                    )
                }
            }
        }
    }
}
```

✅ **Final checkpoint:**
1. Log in → you land on Home. Press Back → the app closes; it does not return to login.
2. Close the app and open it again → you go **straight to Home** (auto-login with the saved token).
3. "Cerrar sesión" → you return to login. Reopen the app → login appears (the token was cleared).

---

## Common errors

| Error | Cause / fix |
|---|---|
| `Hilt Activity must be attached to an @HiltAndroidApp Application` | `android:name=".GuardianApp"` is missing from the manifest |
| `[Dagger/MissingBinding] AuthRepository cannot be provided` | `AuthRepositoryModule` is missing, or `@Binds` has the wrong type |
| `Cannot create an instance of class LoginViewModel` | `@HiltViewModel` or `@AndroidEntryPoint` on `MainActivity` is missing |
| `baseUrl must end in /` | Add `/` at the end of `BASE_URL` |
| `CLEARTEXT communication not permitted` | Your backend is `http://` (local). Use `https`, or add `android:usesCleartextTraffic="true"` to `<application>` (dev only) |
| Emulator can't reach `localhost` | From the emulator, your PC is `http://10.0.2.2:<port>/` |
| `NullPointerException` when mapping the DTO | The JSON field names don't match the DTO; check them with `@SerializedName` |
| KSP / Hilt errors with odd versions | Check that `kotlin`, `ksp` and `hilt` are exactly as in Part 2.1 |
| White strip above the green header | You still have the `Scaffold` with `innerPadding` in `MainActivity` |
