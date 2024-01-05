package com.eygraber.portal.internal

import com.eygraber.portal.PortalEntry

public sealed class PortalBackstackMutation<KeyT> {
  public abstract val key: KeyT
  public abstract val uid: PortalEntry.Id

  public data class Remove<KeyT>(
    override val key: KeyT,
    override val uid: PortalEntry.Id,
  ) : PortalBackstackMutation<KeyT>()

  public data class Attach<KeyT>(
    override val key: KeyT,
    override val uid: PortalEntry.Id,
  ) : PortalBackstackMutation<KeyT>()

  public data class Detach<KeyT>(
    override val key: KeyT,
    override val uid: PortalEntry.Id,
  ) : PortalBackstackMutation<KeyT>()
}
