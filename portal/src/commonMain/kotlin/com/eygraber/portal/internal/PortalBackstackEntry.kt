package com.eygraber.portal.internal

public data class PortalBackstackEntry<KeyT>(
  val id: String,
  val mutations: List<PortalBackstackMutation<KeyT>>,
)
