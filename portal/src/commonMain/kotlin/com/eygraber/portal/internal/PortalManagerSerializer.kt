package com.eygraber.portal.internal

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun <PortalKey> serializePortalManagerState(
  portalKeySerializer: (PortalKey) -> String,
  state: PortalState<PortalKey>
) = Json.encodeToString(
  buildJsonObject {
    put("entries", state.portalEntries.serializeEntries(portalKeySerializer))
    put("backstack", state.backstackEntries.serializeBackstackEntries(portalKeySerializer))
  }
)

private fun <PortalKey> List<PortalEntry<PortalKey>>.serializeEntries(
  portalKeySerializer: (PortalKey) -> String
) = buildJsonArray {
  forEach { entry ->
    add(
      buildJsonObject {
        put("key", portalKeySerializer(entry.key))
        put("wasContentPreviouslyVisible", entry.wasContentPreviouslyVisible)
        put("isAttachedToComposition", entry.isAttachedToComposition)
        put("isDisappearing", entry.isDisappearing)
        put("isBackstackMutation", entry.isBackstackMutation)
        put("compositionState", entry.compositionState.name)
      }
    )
  }
}

private fun <PortalKey> List<PortalBackstackEntry<PortalKey>>.serializeBackstackEntries(
  portalKeySerializer: (PortalKey) -> String
) = buildJsonArray {
  forEach { entry ->
    add(
      buildJsonObject {
        put("id", entry.id)
        put("mutations", entry.mutations.serializeBackstackMutations(portalKeySerializer))
      }
    )
  }
}

private fun <PortalKey> List<PortalBackstackMutation<PortalKey>>.serializeBackstackMutations(
  portalKeySerializer: (PortalKey) -> String
) = buildJsonArray {
  forEach { mutation ->
    buildJsonObject {
      put("key", portalKeySerializer(mutation.key))

      when(mutation) {
        is PortalBackstackMutation.Remove -> put("type", "remove")

        is PortalBackstackMutation.AttachToComposition -> put("type", "attach")

        is PortalBackstackMutation.DetachFromComposition -> put("type", "detach")

        is PortalBackstackMutation.Disappearing -> put("type", "disappearing")
      }
    }
  }
}
