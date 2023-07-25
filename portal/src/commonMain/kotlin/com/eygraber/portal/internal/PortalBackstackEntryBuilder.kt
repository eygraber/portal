package com.eygraber.portal.internal

import com.eygraber.portal.EnterTransitionOverride
import com.eygraber.portal.ExitTransitionOverride
import com.eygraber.portal.KeyedPortal
import com.eygraber.portal.PortalBackstack
import com.eygraber.portal.PortalEntry

internal class PortalBackstackEntryBuilder<KeyT>(
  private val builder: PortalEntryBuilder<KeyT>
) : PortalBackstack.PushBuilder<KeyT> {
  private val backstackMutations = mutableListOf<PortalBackstackMutation<KeyT>>()

  override fun add(
    portal: KeyedPortal<out KeyT>,
    isAttachedToComposition: Boolean,
    transitionOverride: EnterTransitionOverride?
  ) {
    val entry = builder.add(portal, isAttachedToComposition, transitionOverride)

    backstackMutations += PortalBackstackMutation.Remove(
      key = entry.key,
      uid = entry.uid
    )
  }

  override fun attachToComposition(
    key: KeyT,
    transitionOverride: EnterTransitionOverride?
  ) {
    val entry = builder.attachToComposition(key, transitionOverride)

    if(entry != null) {
      backstackMutations += PortalBackstackMutation.Detach(
        key = entry.key,
        uid = entry.uid
      )
    }
  }

  override fun attachToComposition(
    uid: PortalEntry.Id,
    transitionOverride: EnterTransitionOverride?
  ) {
    val entry = builder.attachToComposition(uid, transitionOverride)

    if(entry != null) {
      backstackMutations += PortalBackstackMutation.Detach(
        key = entry.key,
        uid = entry.uid
      )
    }
  }

  override fun detachFromComposition(
    key: KeyT,
    transitionOverride: ExitTransitionOverride?
  ) {
    val entry = builder.detachFromComposition(key, transitionOverride)

    if(entry != null) {
      backstackMutations += PortalBackstackMutation.Attach(
        key = entry.key,
        uid = entry.uid
      )
    }
  }

  override fun detachFromComposition(
    uid: PortalEntry.Id,
    transitionOverride: ExitTransitionOverride?
  ) {
    val entry = builder.detachFromComposition(uid, transitionOverride)

    if(entry != null) {
      backstackMutations += PortalBackstackMutation.Attach(
        key = entry.key,
        uid = entry.uid
      )
    }
  }

  fun build() = backstackMutations
}
