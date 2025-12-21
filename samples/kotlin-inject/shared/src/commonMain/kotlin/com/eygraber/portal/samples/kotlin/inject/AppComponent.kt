package com.eygraber.portal.samples.kotlin.inject

import com.eygraber.portal.samples.kotlin.inject.main.MainPortal
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Scope

@Scope
annotation class AppScope

@Suppress("AbstractClassCanBeInterface")
@AppScope
@Component
abstract class AppComponent {
  abstract val mainPortal: MainPortal
}

val appComponent = AppComponent::class.create()
