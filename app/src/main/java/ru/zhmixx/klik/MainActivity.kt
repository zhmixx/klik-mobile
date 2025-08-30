package ru.zhmixx.klik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KlikApp()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun KlikApp() {
    var showShop by remember { mutableStateOf(false) }

    var kliks by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(1) }
    var exp by remember { mutableIntStateOf(0) }
    var expToNext by remember { mutableIntStateOf(100) }
    var klikMulti by remember { mutableIntStateOf(1) }

    var clickUpgradePrice by remember { mutableIntStateOf(50) }
    var autoclickerPrice by remember { mutableIntStateOf(100) }
    var autoclickerLevel by remember { mutableIntStateOf(0) }

    AnimatedContent(
        targetState = showShop,
        transitionSpec = {
            fadeIn(animationSpec = tween(500)).togetherWith(fadeOut(animationSpec = tween(500)))
        }
    ) { targetShowShop ->
        if (targetShowShop) {
            ShopPage(
                kliks = kliks,
                onKliksChange = { kliks = it },
                clickUpgradePrice = clickUpgradePrice,
                onClickUpgradePriceChange = { clickUpgradePrice = it },
                autoclickerPrice = autoclickerPrice,
                onAutoclickerPriceChange = { autoclickerPrice = it },
                onCloseShop = { showShop = false }
            )
        } else {
            ClickerGame(
                kliks = kliks,
                onKliksChange = { kliks = it },
                level = level,
                onLevelChange = { level = it },
                exp = exp,
                onExpChange = { exp = it },
                expToNext = expToNext,
                onExpToNextChange = { expToNext = it },
                klikMulti = klikMulti,
                onKlikMultiChange = { klikMulti = it },
                clickUpgradePrice = clickUpgradePrice,
                onClickUpgradePriceChange = { clickUpgradePrice = it },
                autoclickerPrice = autoclickerPrice,
                onAutoclickerPriceChange = { autoclickerPrice = it },
                autoclickerLevel = autoclickerLevel,
                onAutoclickerLevelChange = { autoclickerLevel = it },
                onOpenShop = { showShop = true }
            )
        }
    }
}

@Composable
fun ClickerGame(
    kliks: Int,
    onKliksChange: (Int) -> Unit,
    level: Int,
    onLevelChange: (Int) -> Unit,
    exp: Int,
    onExpChange: (Int) -> Unit,
    expToNext: Int,
    onExpToNextChange: (Int) -> Unit,
    klikMulti: Int,
    onKlikMultiChange: (Int) -> Unit,
    clickUpgradePrice: Int,
    onClickUpgradePriceChange: (Int) -> Unit,
    autoclickerPrice: Int,
    onAutoclickerPriceChange: (Int) -> Unit,
    autoclickerLevel: Int,
    onAutoclickerLevelChange: (Int) -> Unit,
    onOpenShop: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta, Color(0xFFFFA500), Color(0xFF800080))
    var currentColor by remember { mutableStateOf(colors.random()) }

    fun gainExp(amount: Int) {
        val newExp = exp + amount
        var tempExp = newExp
        var tempLevel = level
        var tempExpToNext = expToNext
        while (tempExp >= tempExpToNext) {
            tempExp -= tempExpToNext
            tempLevel += 1
            tempExpToNext = (100 * tempLevel.toDouble().pow(1.5)).toInt()
            currentColor = colors.random()
        }
        onExpChange(tempExp)
        onLevelChange(tempLevel)
        onExpToNextChange(tempExpToNext)
    }

    fun klik() {
        onKliksChange(kliks + klikMulti)
        gainExp(klikMulti * 2)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("level: $level", fontSize = 20.sp)
        LinearProgressIndicator(
        progress = { exp.toFloat() / expToNext },
        modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .padding(vertical = 8.dp),
        color = currentColor,
        trackColor = ProgressIndicatorDefaults.linearTrackColor,
        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )
        Text("exp: $exp/$expToNext", fontSize = 16.sp)
        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(1f)
                .background(currentColor, shape = CircleShape)
                .clickable { klik() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.klik),
                contentDescription = "klik button",
                modifier = Modifier.fillMaxSize(0.7f)
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("kliks: $kliks", fontSize = 18.sp)

        Spacer(Modifier.height(32.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { onOpenShop() }) {
                Text("open shop")
            }
        }
    }
}

@Composable
fun ShopPage(
    kliks: Int,
    onKliksChange: (Int) -> Unit,
    clickUpgradePrice: Int,
    onClickUpgradePriceChange: (Int) -> Unit,
    autoclickerPrice: Int,
    onAutoclickerPriceChange: (Int) -> Unit,
    onCloseShop: () -> Unit
) {
    fun buyClickUpgrade() {
        if (kliks >= clickUpgradePrice) {
            onKliksChange(kliks - clickUpgradePrice)
            onClickUpgradePriceChange((clickUpgradePrice * 1.3).toInt())
        }
    }

    fun buyAutoclicker() {
        if (kliks >= autoclickerPrice) {
            onKliksChange(kliks - autoclickerPrice)
            onAutoclickerPriceChange((autoclickerPrice * 1.3).toInt())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("shop", fontSize = 24.sp)
        Spacer(Modifier.height(16.dp))
        Text("kliks: $kliks", fontSize = 18.sp)

        Spacer(Modifier.height(16.dp))
        Button(onClick = { buyClickUpgrade() }) {
            Text("upgrade klik: $clickUpgradePrice")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { buyAutoclicker() }) {
            Text("autoklicker: $autoclickerPrice")
        }

        Spacer(Modifier.height(32.dp))
        Button(onClick = { onCloseShop() }) {
            Text("close shop")
        }
    }
}
