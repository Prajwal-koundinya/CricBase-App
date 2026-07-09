package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ThemeColor
import com.example.ui.theme.*

enum class AvatarSize(val sizeDp: Dp, val fontSizeSp: Float, val showBorder: Boolean) {
    XS(20.dp, 8f, false),
    SM(32.dp, 12f, false),
    MD(48.dp, 18f, true),
    XL(120.dp, 40f, true)
}

@Composable
fun PlayerAvatar(
    name: String,
    alias: String? = null,
    color: ThemeColor,
    size: AvatarSize,
    modifier: Modifier = Modifier
) {
    val initials = getInitials(name, alias)
    val brush = Brush.linearGradient(
        colors = listOf(
            Color(android.graphics.Color.parseColor(color.hex)).copy(alpha = 0.9f),
            Color(android.graphics.Color.parseColor(color.hex))
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size.sizeDp)
            .shadow(if (size.showBorder) 2.dp else 0.dp, CircleShape)
            .clip(CircleShape)
            .background(brush)
            .then(
                if (size.showBorder) Modifier.border(2.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                else Modifier
            )
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = size.fontSizeSp.sp,
            textAlign = TextAlign.Center
        )
    }
}

private fun getInitials(name: String, alias: String?): String {
    val useName = if (!alias.isNullOrBlank()) alias else name
    val trimmed = useName.trim()
    if (trimmed.isEmpty()) return "XX"
    val words = trimmed.split(Regex("\\s+"))
    return if (words.size >= 2) {
        val first = words.first().firstOrNull() ?: ' '
        val last = words.last().firstOrNull() ?: ' '
        "$first$last".uppercase()
    } else {
        val single = words.first()
        if (single.length >= 2) {
            single.substring(0, 2).uppercase()
        } else if (single.length == 1) {
            "${single.first()}${single.first()}".uppercase()
        } else {
            "XX"
        }
    }
}

@Composable
fun StatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label.uppercase(),
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CommonEmptyState(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(CardBlack),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            color = TextMuted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(text = actionLabel, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
