package com.example.guardian_plus_mobile_app.features.auth.presentation
import com.example.guardian_plus_mobile_app.features.auth.presentation.LoginUiState
import com.example.guardian_plus_mobile_app.ui.theme.LabelCaps
import com.example.guardian_plus_mobile_app.R
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.ui.theme.Primary
import com.example.guardian_plus_mobile_app.ui.theme.SheetBg
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.guardian_plus_mobile_app.ui.theme.BrandGreen
import com.example.guardian_plus_mobile_app.ui.theme.FieldBorder
import com.example.guardian_plus_mobile_app.ui.theme.MintBg
import com.example.guardian_plus_mobile_app.ui.theme.TextMuted
import com.example.guardian_plus_mobile_app.ui.theme.TextPrimary


@Composable
fun LoginHeader(
    
) {

    //useStates, para recordar variables y que se actualicen en tiempo real

       Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        //header
        Column(
            modifier = Modifier.fillMaxWidth().background(Primary).statusBarsPadding().padding(start = 32.dp, end = 32.dp, top = 47.dp, bottom = 47.dp)
        ) {
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp)) 
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text= "PLATAFORMA DE SALUD",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text= "Guardian+",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    
 
                }
            }

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = "Cuidado\n\nconectado.",
                        color = Color.White,
                        fontSize = 45.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
 
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Monitoreo en tiempo real para quienes importan",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Default,
                        color = Color.White.copy(alpha = 0.60f)

                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    
                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)){

                      HeaderStat(value = "24/7", label = "Monitoreo")
        
                      HeaderStat(value = "5s", label = "Actualización")
            
                      HeaderStat(value = "99.8%", label = "Disponibilidad")
                    
                    }
                
            
                }

        
       
    }
}
 
@Composable
fun LoginSurface(
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
        modifier = modifier.fillMaxSize().background(SheetBg).verticalScroll(rememberScrollState())
    ) {
    LoginHeader()
    //Sheet
        Surface(
            color = SheetBg, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            modifier = Modifier.fillMaxWidth().offset(y = (-24).dp )
        ) {
           Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp).navigationBarsPadding()) {
               // Drag and Handle
               Box(
                   modifier = Modifier
                   .align(Alignment.CenterHorizontally)
                   .size(width = 40.dp, height = 4.dp)
                   .background(FieldBorder, RoundedCornerShape(2.dp))
               )
               Spacer(modifier = Modifier.size(24.dp))


               Text(
                   text = "Iniciar Sesion",
                   color = TextMuted,
                   fontSize = 15.sp
               )

               Spacer(modifier = Modifier.height(4.dp))

               Text(
                   text = "Accede a tu cuenta de cuidador",
                   color = TextMuted,
                   fontSize = 15.sp
               )

               //email
               FieldLabel(text = "CORREO ELECTRONICO")
               Spacer(modifier = Modifier.height(8.dp))
               OutlinedTextField(
                   value = state.email,
                   onValueChange = onEmailChange,
                   placeholder = {Text(text = "nombre@correo.com", color = TextMuted)},
                   singleLine = true,
                   keyboardOptions = KeyboardOptions(
                       keyboardType = KeyboardType.Email,
                       imeAction = ImeAction.Next
                   ),
                   shape = RoundedCornerShape(14.dp),
                   modifier = Modifier.fillMaxWidth(),
                   colors = loginFieldColors()
               )

               Spacer(modifier = Modifier.height(16.dp))

               //password
               FieldLabel("Password")
               Spacer(modifier = Modifier.height(8.dp))
               OutlinedTextField(
                   value = state.password,
                   onValueChange = onPasswordChange,
                   placeholder = {Text(text= "Ingrese u contrasena aca", color = TextMuted)},
                   shape = RoundedCornerShape(14.dp),
                   singleLine = true,
                   modifier = Modifier.fillMaxWidth(),
                   colors = loginFieldColors(),
                   visualTransformation = if (state.isPasswordHidden) {
                       PasswordVisualTransformation()
                   }
                   else {
                       VisualTransformation.None
                   },
                   keyboardActions = KeyboardActions(onDone = {onLoginClick()}),
                   trailingIcon = {
                       IconButton(onClick = onTogglePasswordVisibility) {
                           Icon(
                               imageVector = if (state.isPasswordHidden) Icons.Outlined.Visibility
                               else Icons.Outlined.VisibilityOff,
                               contentDescription = if(state.isPasswordHidden) "Mostrar Contrasena"
                               else "Ocultar Contrasena",
                               tint = TextMuted
                           )
                       }
                   }
               )

               Spacer(modifier = Modifier.height(12.dp))


               //Forgot Passowrd
               Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = BrandGreen,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .align(Alignment.End).clickable(onClick = onForgotPassword)
                )

                state.errorMessage?.let {
                    message ->

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
    
                            text = message,
    
                            color = MaterialTheme.colorScheme.error,
    
                            fontSize = 13.sp

                        )
                }

                Spacer(modifier = Modifier.height(24.dp))

                //Main button
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
private fun HeaderStat(value: String, label: String){
    Column{
        Text(
            text = value,
            color = Color.White,
            fontSize = 20.sp,
            fontFamily =  FontFamily.Monospace
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


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit = {}
) {
    var state by remember { mutableStateOf(LoginUiState()) }
    val scope = rememberCoroutineScope()

    LoginSurface(
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
        }
    )
}
