package com.eygraber.portal.internal

import com.eygraber.portal.PortalEntry
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun <KeyT> serializePortalManagerState(
  keySerializer: (KeyT) -> String,
  state: PortalState<KeyT>
) = Json.encodeToString(
  buildJsonObject {
    put("entries", state.portalEntries.serializeEntries(keySerializer))
    put("backstack", state.backstackEntries.serializeBackstackEntries(keySerializer))
  }
)

private fun <KeyT> List<PortalEntry<KeyT>>.serializeEntries(
  keySerializer: (KeyT) -> String
) = buildJsonArray {
  forEach { entry ->
    add(
      buildJsonObject {
        put("key", keySerializer(entry.key))
        put("wasContentPreviouslyVisible", entry.wasContentPreviouslyVisible)
        put("isAttached", entry.rendererState.isAddedOrAttached)
        put("backstackState", entry.backstackState.name)
        put("rendererState", entry.rendererState.name)
        put("uid", entry.uid)
      }
    )
  }
}

private fun <KeyT> List<PortalBackstackEntry<KeyT>>.serializeBackstackEntries(
  keySerializer: (KeyT) -> String
) = buildJsonArray {
  forEach { entry ->
    add(
      buildJsonObject {
        put("id", entry.id)
        put("mutations", entry.mutations.serializeBackstackMutations(keySerializer))
      }
    )
  }
}

private fun <KeyT> List<PortalBackstackMutation<KeyT>>.serializeBackstackMutations(
  keySerializer: (KeyT) -> String
) = buildJsonArray {
  forEach { mutation ->
    buildJsonObject {
      put("key", keySerializer(mutation.key))

      when(mutation) {
        is PortalBackstackMutation.Remove -> put("type", "remove")

        is PortalBackstackMutation.Attach -> put("type", "attach")

        is PortalBackstackMutation.Detach -> put("type", "detach")
      }
    }
  }
}
