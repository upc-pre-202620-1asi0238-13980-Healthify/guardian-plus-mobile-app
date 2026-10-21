package com.example.guardian_plus_mobile_app.features.auth.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.BrandGreen
import com.example.guardian_plus_mobile_app.core.designsystem.theme.FieldBorder
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.LabelCaps
import com.example.guardian_plus_mobile_app.core.designsystem.theme.SheetBg
import com.example.guardian_plus_mobile_app.core.designsystem.theme.TextMuted
import com.example.guardian_plus_mobile_app.core.designsystem.theme.TextPrimary

/** Code length of the verification and OTP emails IAM sends. */
const val AUTH_CODE_LENGTH = 6

/**
 * Layout shared by the sign-up and recovery steps: the login's off-white sheet with a back button, an
 * optional step ("Paso 2 de 5"), a title and a subtitle above the form.
 */
@Composable
fun AuthStepLayout(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    step: String? = null,
    onBackClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SheetBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        OutlinedIconButton(
            onClick = onBackClick,
            shape = CircleShape,
            border = BorderStroke(1.dp, FieldBorder),
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left),
                contentDescription = stringResource(R.string.auth_back),
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        step?.let {
            Text(
                text = it.uppercase(),
                color = BrandGreen,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(text = title, color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = subtitle, color = TextMuted, fontSize = 15.sp, lineHeight = 21.sp)
        Spacer(modifier = Modifier.height(28.dp))
        content()
    }
}

@Composable
fun AuthFieldLabel(text: String) {
    Text(
        text = text,
        color = LabelCaps,
        fontSize = 12.sp,
        letterSpacing = 1.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun AuthTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AuthFieldLabel(text = label)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = TextMuted) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = KeyboardActions(onAny = { onImeAction() }),
            shape = RoundedCornerShape(14.dp),
            colors = authFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AuthPasswordField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isHidden: Boolean,
    onToggleVisibility: () -> Unit,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AuthFieldLabel(text = label)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = TextMuted) },
            singleLine = true,
            visualTransformation = if (isHidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
            keyboardActions = KeyboardActions(onAny = { onImeAction() }),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (isHidden) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = stringResource(
                            if (isHidden) R.string.auth_show_password else R.string.auth_hide_password
                        ),
                        tint = TextMuted
                    )
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = authFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Six-digit code field; anything that is not a digit is ignored. */
@Composable
fun AuthCodeField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AuthFieldLabel(text = stringResource(R.string.auth_code_label))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { input -> onValueChange(input.filter(Char::isDigit).take(AUTH_CODE_LENGTH)) },
            placeholder = {
                Text(
                    text = stringResource(R.string.auth_code_placeholder),
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 24.sp,
                    letterSpacing = 8.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily.Monospace,
                letterSpacing = 8.sp,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            shape = RoundedCornerShape(14.dp),
            colors = authFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AuthPrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandGreen,
            contentColor = Color.White,
            disabledContainerColor = BrandGreen.copy(alpha = 0.7f),
            disabledContentColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

/** Why the last attempt was rejected, right above the main button. */
@Composable
fun AuthErrorMessage(message: String?) {
    message ?: return
    Spacer(modifier = Modifier.height(12.dp))
    Text(text = message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
}

@Composable
fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedBorderColor = BrandGreen,
    unfocusedBorderColor = FieldBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = BrandGreen
)

@Preview
@Composable
private fun AuthStepLayoutPreview() {
    GuardianTheme(dynamicColor = false) {
        AuthStepLayout(
            title = "Código de verificación",
            subtitle = "Ingresa el código de 6 dígitos que enviamos a maria@correo.com",
            step = "Paso 2 de 5",
            onBackClick = {}
        ) {
            AuthCodeField(value = "1234", onValueChange = {}, onDone = {})
            AuthErrorMessage(message = "El código no es válido o ya venció")
            Spacer(modifier = Modifier.height(24.dp))
            AuthPrimaryButton(text = "Verificar correo", isLoading = false, onClick = {})
        }
    }
}
