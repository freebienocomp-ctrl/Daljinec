package si.daljinec

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight

// ---------- barve ----------

val ScreenColor = Color(0xFF050607)
val TextColor = Color(0xFFEEF0F2)
val DimColor = Color(0xFF8A9099)
val LabelColor = Color(0xFF9AA0A8)
val AccentColor = Color(0xFF3B82F6)
val GreenColor = Color(0xFF4ADE80)
val GlowColor = Color(0xFF60A5FA)
val ArrowColor = Color(0xFFC8CCD2)
val ArrowLit = Color(0xFF93C5FD)
val BodyBrush = Brush.verticalGradient(listOf(Color(0xFF0C0E10), Color(0xFF060708)))

val KeyBrush = Brush.verticalGradient(listOf(Color(0xFF2C3138), Color(0xFF22262C)))

// ---------- pisava ----------

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold)
)

/** Vsi slogi besedila v Material temi s pisavo Inter. */
fun interTypography(): Typography {
    val b = Typography()
    return b.copy(
        displayLarge = b.displayLarge.copy(fontFamily = Inter),
        displayMedium = b.displayMedium.copy(fontFamily = Inter),
        displaySmall = b.displaySmall.copy(fontFamily = Inter),
        headlineLarge = b.headlineLarge.copy(fontFamily = Inter),
        headlineMedium = b.headlineMedium.copy(fontFamily = Inter),
        headlineSmall = b.headlineSmall.copy(fontFamily = Inter),
        titleLarge = b.titleLarge.copy(fontFamily = Inter),
        titleMedium = b.titleMedium.copy(fontFamily = Inter),
        titleSmall = b.titleSmall.copy(fontFamily = Inter),
        bodyLarge = b.bodyLarge.copy(fontFamily = Inter),
        bodyMedium = b.bodyMedium.copy(fontFamily = Inter),
        bodySmall = b.bodySmall.copy(fontFamily = Inter),
        labelLarge = b.labelLarge.copy(fontFamily = Inter),
        labelMedium = b.labelMedium.copy(fontFamily = Inter),
        labelSmall = b.labelSmall.copy(fontFamily = Inter)
    )
}
