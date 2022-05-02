package com.eygraber.portal.internal

import com.eygraber.portal.Portal
import com.eygraber.portal.PortalBackstack

internal class PortalBackstackEntryBuilder<KeyT, EntryT, EnterExtraT, ExitExtraT, PortalT>(
  private val builder: PortalEntryBuilder<KeyT, EntryT, EnterExtraT, ExitExtraT, PortalT>
) : PortalBackstack.PushBuilder<KeyT, EnterExtraT, ExitExtraT, PortalT>
  where EntryT : Entry<KeyT, EnterExtraT, ExitExtraT, PortalT>,
        EnterExtraT : EnterExtra,
        ExitExtraT : ExitExtra,
        PortalT : Portal {
  private val backstackMutations = mutableListOf<PortalBackstackMutation<KeyT>>()

  override fun add(
    key: KeyT,
    isAttachedToComposition: Boolean,
    extra: EnterExtraT?,
    portal: PortalT
  ) {
    builder.add(key, isAttachedToComposition, extra, portal)

    backstackMutations += PortalBackstackMutation.Remove(
      key = key
    )
  }

  override fun attachToComposition(
    key: KeyT,
    extra: EnterExtraT?
  ) {
    builder.attachToComposition(key, extra)
  }

  override fun detachFromComposition(
    key: KeyT,
    extra: ExitExtraT?
  ) {
    builder.detachFromComposition(key, extra)

    backstackMutations += PortalBackstackMutation.Attach(
      key = key
    )
  }

  fun build() = backstackMutations
}
