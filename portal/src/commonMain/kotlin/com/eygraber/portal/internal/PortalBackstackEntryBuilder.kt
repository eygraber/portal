package com.eygraber.portal.internal

import com.eygraber.portal.EnterTransitionOverride
import com.eygraber.portal.ExitTransitionOverride
import com.eygraber.portal.Portal
import com.eygraber.portal.PortalBackstack

internal class PortalBackstackEntryBuilder<KeyT>(
  private val builder: PortalEntryBuilder<KeyT>
) : PortalBackstack.PushBuilder<KeyT> {
  private val backstackMutations = mutableListOf<PortalBackstackMutation<KeyT>>()

  override fun add(
    key: KeyT,
    isAttachedToComposition: Boolean,
    transitionOverride: EnterTransitionOverride?,
    portal: Portal
  ) {
    builder.add(key, isAttachedToComposition, transitionOverride, portal)

    backstackMutations += PortalBackstackMutation.Remove(
      key = key
    )
  }

  override fun attachToComposition(
    key: KeyT,
    transitionOverride: EnterTransitionOverride?
  ) {
    builder.attachToComposition(key, transitionOverride)
  }

  override fun detachFromComposition(
    key: KeyT,
    transitionOverride: ExitTransitionOverride?
  ) {
    builder.detachFromComposition(key, transitionOverride)

    backstackMutations += PortalBackstackMutation.Attach(
      key = key
    )
  }

  fun build() = backstackMutations
}
