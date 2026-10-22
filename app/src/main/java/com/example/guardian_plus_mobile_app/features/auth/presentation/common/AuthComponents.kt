package com.example.guardian_plus_mobile_app.features.auth.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.guardian_plus_mobile_app.core.designsystem.theme.BrandGreen
import com.example.guardian_plus_mobile_app.core.designsystem.theme.FieldBorder
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.LabelCaps
import com.example.guardian_plus_mobile_app.core.designsystem.theme.NeutralMuted
import com.example.guardian_plus_mobile_app.core.designsystem.theme.Primary
import com.example.guardian_plus_mobile_app.core.designsystem.theme.SheetBg
import com.example.guardian_plus_mobile_app.core.designsystem.theme.TextMuted
import com.example.guardian_plus_mobile_app.core.designsystem.theme.TextPrimary

/** Code length of the verification and OTP emails IAM sends. */
const val AUTH_CODE_LENGTH = 6

/**
 * Layout of the prototype's auth screens: the login's green header with the brand, a headline and a tagline,
 * and the off-white sheet that rises over it with the form.
 */
@Composable
fun AuthLayout(
    modifier: Modifier = Modifier,
    headerTitle: String,
    headerSubtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SheetBg)
            .verticalScroll(rememberScrollState())
    ) {
        AuthHeader(title = headerTitle, subtitle = headerSubtitle)
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
                Spacer(modifier = Modifier.height(20.dp))
                content()
            }
        }
    }
}

@Composable
private fun AuthHeader(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary)
            .clipToBounds()
    ) {
        // Faint card outlines of the prototype behind the text
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 48.dp, y = (-12).dp)
                .size(130.dp)
                .rotate(18f)
                .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(24.dp))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = 40.dp)
                .size(170.dp)
                .rotate(18f)
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(28.dp))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-70).dp, y = 8.dp)
                .size(width = 120.dp, height = 260.dp)
                .rotate(-8f)
                .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(24.dp))
        )
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 32.dp, end = 32.dp, top = 40.dp, bottom = 72.dp)
        ) {
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
                        text = stringResource(R.string.auth_brand_caption),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = stringResource(R.string.app_name),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 40.sp,
                lineHeight = 44.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        }
    }
}

/**
 * Title and subtitle at the top of the sheet. [highlight] follows the subtitle in a stronger tone (the masked
 * email on the OTP screen), and [onBackClick] adds the small back button beside the title.
 */
@Composable
fun AuthSheetTitle(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    highlight: String? = null,
    onBackClick: (() -> Unit)? = null
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        onBackClick?.let { onClick ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(NeutralMuted, RoundedCornerShape(10.dp))
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_left),
                    contentDescription = stringResource(R.string.auth_back),
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column {
            Text(text = title, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = buildAnnotatedString {
                    append(subtitle)
                    highlight?.let {
                        append(" ")
                        withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.Medium)) { append(it) }
                    }
                },
                color = TextMuted,
                fontSize = 15.sp,
                lineHeight = 21.sp
            )
        }
    }
}

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

/** One box per digit of the code; anything that is not a digit is ignored. */
@Composable
fun OtpCodeBoxes(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        value = value,
        onValueChange = { input -> onValueChange(input.filter(Char::isDigit).take(AUTH_CODE_LENGTH)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        interactionSource = interactionSource,
        modifier = modifier.fillMaxWidth(),
        // The digits are drawn in the boxes, so the field's own text is not shown
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(AUTH_CODE_LENGTH) { index ->
                    val digit = value.getOrNull(index)
                    // The box the next digit goes into stands out while typing
                    val isCurrent = isFocused && index == value.length.coerceAtMost(AUTH_CODE_LENGTH - 1)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(
                                width = 1.5.dp,
                                color = if (isCurrent) BrandGreen else FieldBorder,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = digit?.toString().orEmpty(),
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun AuthPrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    isLoading: Boolean,
    enabled: Boolean = true,
    icon: Painter? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandGreen,
            contentColor = Color.White,
            // While loading it stays almost solid; an incomplete form fades it as in the prototype
            disabledContainerColor = BrandGreen.copy(alpha = if (isLoading) 0.7f else 0.4f),
            disabledContentColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            icon?.let {
                Icon(painter = it, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
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

/** Divider with a caption in the middle, such as "o regístrate con". */
@Composable
fun AuthDivider(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = FieldBorder)
        Text(
            text = text,
            color = TextMuted,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = FieldBorder)
    }
}

@Composable
fun GoogleButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, FieldBorder),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = TextPrimary),
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
        Text(text = text, fontSize = 16.sp)
    }
}

@Composable
fun AuthTermsText() {
    Text(
        text = buildAnnotatedString {
            append(stringResource(R.string.auth_terms_prefix))
            withStyle(SpanStyle(color = BrandGreen)) { append(stringResource(R.string.auth_terms_of_use)) }
            append(stringResource(R.string.auth_terms_and))
            withStyle(SpanStyle(color = BrandGreen)) { append(stringResource(R.string.auth_privacy_policy)) }
        },
        color = TextMuted,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        fontFamily = FontFamily.Monospace,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

/** "< Volver a iniciar sesión", centered under the main button. */
@Composable
fun BackToLoginLink(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(R.string.auth_back_to_login), color = TextMuted, fontSize = 15.sp)
    }
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
private fun AuthLayoutPreview() {
    GuardianTheme(dynamicColor = false) {
        AuthLayout(headerTitle = "Verifica\ntu identidad.", headerSubtitle = "Confirmamos que eres tú antes de darte acceso.") {
            AuthSheetTitle(
                title = "Código de verificación",
                subtitle = "Enviamos un código de 6 dígitos a",
                highlight = "ma••••@correo.com",
                onBackClick = {}
            )
            Spacer(modifier = Modifier.height(24.dp))
            OtpCodeBoxes(value = "1234", onValueChange = {}, onDone = {})
            Spacer(modifier = Modifier.height(24.dp))
            AuthPrimaryButton(text = "Verificar código", isLoading = false, enabled = false, onClick = {})
            BackToLoginLink(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {})
        }
    }
}
