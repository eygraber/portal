package com.eygraber.portal.internal

import com.eygraber.portal.Portal
import com.eygraber.portal.PortalBackstack

internal class PortalBackstackEntryBuilder<KeyT, EntryT, ExtraT : Extra, PortalT : Portal>(
  private val builder: PortalEntryBuilder<KeyT, EntryT, ExtraT, PortalT>
) : PortalBackstack.PushBuilder<KeyT, ExtraT, PortalT> where EntryT : Entry<KeyT, ExtraT, PortalT> {
  private val backstackMutations = mutableListOf<PortalBackstackMutation<KeyT>>()

  override fun add(
    key: KeyT,
    isAttachedToComposition: Boolean,
    extra: ExtraT?,
    portal: PortalT
  ) {
    builder.add(key, isAttachedToComposition, extra, portal)

    backstackMutations += PortalBackstackMutation.Remove(
      key = key
    )
  }

  override fun attachToComposition(
    key: KeyT,
    extra: ExtraT?
  ) {
    builder.attachToComposition(key, extra)
  }

  override fun detachFromComposition(
    key: KeyT,
    extra: ExtraT?
  ) {
    builder.detachFromComposition(key, extra)

    backstackMutations += PortalBackstackMutation.Attach(
      key = key
    )
  }

  fun build() = backstackMutations
}
