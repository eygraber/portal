package com.eygraber.portal.internal

public sealed class PortalBackstackMutation<KeyT> {
  public abstract val key: KeyT

  public data class Remove<KeyT>(
    override val key: KeyT
  ) : PortalBackstackMutation<KeyT>()

  public data class Attach<KeyT>(
    override val key: KeyT
  ) : PortalBackstackMutation<KeyT>()

  public data class Detach<KeyT>(
    override val key: KeyT
  ) : PortalBackstackMutation<KeyT>()
}
