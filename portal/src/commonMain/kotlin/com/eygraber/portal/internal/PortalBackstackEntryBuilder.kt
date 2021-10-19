package com.eygraber.portal.internal

import com.eygraber.portal.PortalBackstack
import com.eygraber.portal.PortalRender
import com.eygraber.portal.PortalTransitions

internal class PortalBackstackEntryBuilder<PortalKey>(
  private val builder: PortalEntryBuilder<PortalKey>
) : PortalBackstack.PushBuilder<PortalKey> {
  private val backstackMutations = mutableListOf<PortalBackstackMutation<PortalKey>>()

  override fun add(
    key: PortalKey,
    isAttachedToComposition: Boolean,
    transitionsOverride: PortalTransitions?,
    render: PortalRender
  ) {
    builder.add(key, isAttachedToComposition, transitionsOverride, render)

    backstackMutations += PortalBackstackMutation.Remove(
      key = key,
      transitionsOverride = transitionsOverride
    )
  }

  override fun attachToComposition(
    key: PortalKey,
    transitionsOverride: PortalTransitions?
  ) {
    builder.attachToComposition(key, transitionsOverride)

    backstackMutations += PortalBackstackMutation.DetachFromComposition(
      key = key,
      transitionsOverride = transitionsOverride
    )
  }

  override fun detachFromComposition(
    key: PortalKey,
    transitionsOverride: PortalTransitions?
  ) {
    builder.detachFromComposition(key, transitionsOverride)

    backstackMutations += PortalBackstackMutation.AttachToComposition(
      key = key,
      transitionsOverride = transitionsOverride
    )
  }

  fun build() = backstackMutations
}
