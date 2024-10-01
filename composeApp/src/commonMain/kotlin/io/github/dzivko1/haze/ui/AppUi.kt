package io.github.dzivko1.haze.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import haze.composeapp.generated.resources.*
import io.github.dzivko1.haze.ui.home.HomeSectionRoute
import io.github.dzivko1.haze.ui.inventory.InventorySectionRoute
import io.github.dzivko1.haze.ui.profile.ProfileSectionRoute
import io.github.dzivko1.haze.ui.store.StoreSectionRoute
import io.github.dzivko1.haze.ui.trading.TradingSectionRoute
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppUi() {
  Scaffold { contentPadding ->
    Column(Modifier.padding(contentPadding)) {
      val navController = rememberNavController()

      NavBar(navController)

      AppNavHost(
        navController = navController
      )
    }
  }
}

@Composable
private fun NavBar(
  navController: NavHostController,
) {
  val navSections = listOf(
    NavSection(stringResource(Res.string.nav_home), HomeSectionRoute),
    NavSection(stringResource(Res.string.nav_store), StoreSectionRoute),
    NavSection(stringResource(Res.string.nav_inventory), InventorySectionRoute),
    NavSection(stringResource(Res.string.nav_trading), TradingSectionRoute),
    NavSection(stringResource(Res.string.nav_profile), ProfileSectionRoute)
  )
  val currentBackStackEntry by navController.currentBackStackEntryAsState()

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.height(50.dp)
  ) {
    navSections.forEachIndexed { index, navSection ->
      NavButton(
        text = navSection.name,
        selected = currentBackStackEntry?.destination?.parent?.hasRoute(navSection.route::class) == true,
        onClick = { navController.navigate(navSection.route) },
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight()
      )
      if (index != navSections.lastIndex) VerticalDivider()
    }
  }
  HorizontalDivider()
}

@Composable
private fun NavButton(
  text: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    onClick = onClick,
    color = if (selected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
    modifier = modifier
  ) {
    Text(
      text = text.uppercase(),
      style = MaterialTheme.typography.headlineSmall,
      textAlign = TextAlign.Center,
      modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically)
    )
  }
}

private data class NavSection(
  val name: String,
  val route: Any,
)
