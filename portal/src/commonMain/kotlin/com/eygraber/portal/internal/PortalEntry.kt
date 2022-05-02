package com.eygraber.portal.internal

import com.eygraber.portal.Portal
import com.eygraber.portal.PortalRendererState

internal typealias EnterExtra = PortalEntry.Extra.Enter
internal typealias ExitExtra = PortalEntry.Extra.Exit
internal typealias Entry<KeyT, EnterExtraT, ExitExtraT, PortalT> = PortalEntry<KeyT, EnterExtraT, ExitExtraT, PortalT>

public interface PortalEntry<KeyT, EnterExtraT, ExitExtraT, PortalT>
  where EnterExtraT : EnterExtra,
        ExitExtraT : ExitExtra,
        PortalT : Portal {
  public val key: KeyT
  public val wasContentPreviouslyVisible: Boolean
  public val isDisappearing: Boolean
  public val isBackstackMutation: Boolean
  public val rendererState: PortalRendererState
  public val enterExtra: EnterExtraT?
  public val exitExtra: ExitExtraT?
  public val portal: PortalT

  public interface Extra {
    public interface Enter : Extra
    public interface Exit : Extra
  }

  public interface Callbacks<KeyT, EntryT, EnterExtraT, ExitExtraT, PortalT>
    where EntryT : Entry<KeyT, EnterExtraT, ExitExtraT, PortalT>,
          EnterExtraT : EnterExtra,
          ExitExtraT : ExitExtra,
          PortalT : Portal {
    public fun create(
      key: KeyT,
      wasContentPreviouslyVisible: Boolean,
      isDisappearing: Boolean,
      isBackstackMutation: Boolean,
      rendererState: PortalRendererState,
      extra: EnterExtraT?,
      portal: PortalT
    ): EntryT

    public fun attach(
      entry: EntryT,
      isBackstackMutation: Boolean,
      wasContentPreviouslyVisible: Boolean,
      rendererState: PortalRendererState,
      extra: EnterExtraT?
    ): EntryT

    public fun detach(
      entry: EntryT,
      isBackstackMutation: Boolean,
      wasContentPreviouslyVisible: Boolean,
      rendererState: PortalRendererState,
      extra: ExitExtraT?
    ): EntryT

    public fun remove(
      entry: EntryT,
      isBackstackMutation: Boolean,
      wasContentPreviouslyVisible: Boolean,
      isDisappearing: Boolean,
      rendererState: PortalRendererState,
      extra: ExitExtraT?
    ): EntryT
  }
}
