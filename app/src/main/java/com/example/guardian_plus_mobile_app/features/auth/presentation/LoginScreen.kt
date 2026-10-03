package com.example.guardian_plus_mobile_app.features.auth.presentation


import com.example.guardian_plus_mobile_app.ui.theme.LabelCaps
import android.graphics.Paint
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Icon
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
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.res.FontResourcesParserCompat
import com.example.guardian_plus_mobile_app.ui.theme.FieldBorder
import com.example.guardian_plus_mobile_app.ui.theme.TextMuted


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onRegister: () -> Unit = {}
) {

    //useStates, para recordar variables y que se actualicen en tiempo real

    var email by remember {
        mutableStateOf("")
    }
    var password by remember { mutableStateOf("")}
    var showPw by remember { mutableStateOf(false)}
    var loading by remember { mutableStateOf(false)}
    var error by remember{ mutableStateOf("")}


    val scope = rememberCoroutineScope()

    fun handleLogin() {
        if (email.isBlank() || password.isBlank()) {
            error = "Completa todos los campos para continuar"
            return
        }

        error = ""
        loading = true
        scope.launch {
            delay(1200)
            loading = false
            onLoginSuccess()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().background(SheetBg).verticalScroll(rememberScrollState())
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

                LoginSurface()

        
       
    }
}

@Composable
private fun LoginSurface() {

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

               val email = remember {
                   mutableStateOf("")
               }
               //email
               FieldLabel(text = "CORREO ELECTRONICO")
               Spacer(modifier = Modifier.height(8.dp))
                   onValueChange = "Email"
               )
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
