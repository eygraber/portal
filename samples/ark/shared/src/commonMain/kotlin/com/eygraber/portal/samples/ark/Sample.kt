package com.eygraber.portal.samples.ark

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow

// @Immutable
// enum class BottomNavItem {
//   Home,
//   Chat,
//   Settings
// }
//
// sealed interface SampleIntent {
//   data class NavItemSelected(val item: BottomNavItem) : SampleIntent
// }
//
// @Immutable
// data class SampleViewState(
//   val items: Set<BottomNavItem>,
//   val selectedItem: BottomNavItem
// )
//
// class SamplePresenter : ArkPresenter<SampleIntent, SampleViewState> {
//   private var selectedNavItem by mutableStateOf(BottomNavItem.Home)
//
//   @Composable
//   override fun present(intents: Flow<SampleIntent>) =
//     SampleViewState(
//       items = BottomNavItem.values().toSet(),
//       selectedItem = selectedNavItem
//     )
//
//   override suspend fun onIntent(intent: SampleIntent) {
//     when(intent) {
//       is SampleIntent.NavItemSelected -> selectedNavItem = intent.item
//     }
//   }
// }
//
// class SampleView : ArkView<SampleIntent, SampleViewState> {
//   @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
//   @Composable
//   override fun Render(state: SampleViewState, onIntent: (SampleIntent) -> Unit) {
//     Scaffold(
//       bottomBar = {
//         NavigationBar {
//           state.items.forEach { item ->
//             NavigationBarItem(
//               selected = item == state.selectedItem,
//               onClick = {
//                 onIntent(SampleIntent.NavItemSelected(item))
//               },
//               icon = {
//                 Icon(Icons.Filled.Favorite, contentDescription = "Favorite")
//               },
//               label = {
//                 Text(item.name)
//               }
//             )
//           }
//         }
//       }
//     ) { innerPadding ->
//       Box(
//         modifier = Modifier
//           .consumedWindowInsets(innerPadding)
//           .padding(innerPadding)
//           .fillMaxSize(),
//         contentAlignment = Alignment.Center
//       ) {
//         Text("Hello, world!")
//       }
//     }
//   }
// }
//
// class SampleArkPortal : ArkPortal<SamplePresenter, SampleView, SampleIntent, SampleViewState>() {
//   override val presenter: SamplePresenter by lazy { SamplePresenter() }
//   override val view: SampleView by lazy { SampleView() }
//
//   override val key = Unit
//
//   @Composable
//   override fun Render() {
//     super.Render()
//
//     LaunchedEffect(Unit) {
//       println("This is just here to illustrate Render being overridden and calling super.Render()")
//     }
//   }
// }
