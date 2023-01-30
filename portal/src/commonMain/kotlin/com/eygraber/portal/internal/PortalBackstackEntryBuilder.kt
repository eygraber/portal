package com.eygraber.portal.internal

import com.eygraber.portal.EnterTransitionOverride
import com.eygraber.portal.ExitTransitionOverride
import com.eygraber.portal.KeyedPortal
import com.eygraber.portal.PortalBackstack

internal class PortalBackstackEntryBuilder<KeyT>(
  private val builder: PortalEntryBuilder<KeyT>
) : PortalBackstack.PushBuilder<KeyT> {
  private val backstackMutations = mutableListOf<PortalBackstackMutation<KeyT>>()

  override fun add(
    portal: KeyedPortal<out KeyT>,
    isAttachedToComposition: Boolean,
    transitionOverride: EnterTransitionOverride?
  ) {
    builder.add(portal, isAttachedToComposition, transitionOverride)

    backstackMutations += PortalBackstackMutation.Remove(
      key = portal.key
    )
  }

  override fun attachToComposition(
    key: KeyT,
    transitionOverride: EnterTransitionOverride?
  ) {
    builder.attachToComposition(key, transitionOverride)

    backstackMutations += PortalBackstackMutation.Detach(
      key = key
    )
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
