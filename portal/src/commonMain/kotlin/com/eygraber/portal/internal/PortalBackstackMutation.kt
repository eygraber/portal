package com.eygraber.portal.internal

internal sealed class PortalBackstackMutation<PortalKey> {
  abstract val key: PortalKey

  data class Remove<PortalKey>(
    override val key: PortalKey
  ) : PortalBackstackMutation<PortalKey>()

  data class AttachToComposition<PortalKey>(
    override val key: PortalKey
  ) : PortalBackstackMutation<PortalKey>()

  data class DetachFromComposition<PortalKey>(
    override val key: PortalKey
  ) : PortalBackstackMutation<PortalKey>()

  data class Disappearing<PortalKey>(
    override val key: PortalKey
  ) : PortalBackstackMutation<PortalKey>()
}
