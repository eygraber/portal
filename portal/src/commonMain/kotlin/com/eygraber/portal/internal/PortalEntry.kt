package com.eygraber.portal.internal

import com.eygraber.portal.Portal
import com.eygraber.portal.PortalRendererState

internal typealias Extra = PortalEntry.Extra
internal typealias Entry<KeyT, ExtraT, PortalT> = PortalEntry<KeyT, ExtraT, PortalT>

public interface PortalEntry<KeyT, ExtraT : Extra, PortalT : Portal> {
  public val key: KeyT
  public val wasContentPreviouslyVisible: Boolean
  public val isDisappearing: Boolean
  public val isBackstackMutation: Boolean
  public val rendererState: PortalRendererState
  public val extra: ExtraT?
  public val portal: PortalT

  public interface Extra

  public interface Callbacks<KeyT, EntryT : Entry<KeyT, ExtraT, PortalT>, ExtraT : Extra, PortalT : Portal> {
    public fun create(
      key: KeyT,
      wasContentPreviouslyVisible: Boolean,
      isDisappearing: Boolean,
      isBackstackMutation: Boolean,
      rendererState: PortalRendererState,
      extra: ExtraT?,
      portal: PortalT
    ): EntryT

    public fun attach(
      entry: EntryT,
      isBackstackMutation: Boolean,
      wasContentPreviouslyVisible: Boolean,
      rendererState: PortalRendererState,
      extra: ExtraT?
    ): EntryT

    public fun detach(
      entry: EntryT,
      isBackstackMutation: Boolean,
      wasContentPreviouslyVisible: Boolean,
      rendererState: PortalRendererState,
      extra: ExtraT?
    ): EntryT

    public fun remove(
      entry: EntryT,
      isBackstackMutation: Boolean,
      wasContentPreviouslyVisible: Boolean,
      isDisappearing: Boolean,
      rendererState: PortalRendererState,
      extra: ExtraT?
    ): EntryT
  }
}
