package com.eygraber.portal.internal

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import com.eygraber.portal.Portal
import com.eygraber.portal.PortalBackstack

internal class PortalBackstackEntryBuilder<PortalKey>(
  private val builder: PortalEntryBuilder<PortalKey>
) : PortalBackstack.PushBuilder<PortalKey> {
  private val backstackMutations = mutableListOf<PortalBackstackMutation<PortalKey>>()

  override fun add(
    key: PortalKey,
    isAttachedToComposition: Boolean,
    transitionOverride: EnterTransition?,
    portal: Portal
  ) {
    builder.add(key, isAttachedToComposition, transitionOverride, portal)

    backstackMutations += PortalBackstackMutation.Remove(
      key = key
    )
  }

  override fun attachToComposition(
    key: PortalKey,
    transitionOverride: EnterTransition?
  ) {
    builder.attachToComposition(key, transitionOverride)
  }

  override fun detachFromComposition(
    key: PortalKey,
    transitionOverride: ExitTransition?
  ) {
    builder.detachFromComposition(key, transitionOverride)

    backstackMutations += PortalBackstackMutation.AttachToComposition(
      key = key
    )
  }

  fun build() = backstackMutations
}
