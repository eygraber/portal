package com.eygraber.portal.internal

import com.eygraber.portal.Portal
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun <KeyT, EntryT, ExtraT : Extra, PortalT : Portal> serializePortalManagerState(
  keySerializer: (KeyT) -> String,
  state: PortalState<KeyT, EntryT, ExtraT, PortalT>
) where EntryT : Entry<KeyT, ExtraT, PortalT> = Json.encodeToString(
  buildJsonObject {
    put("entries", state.portalEntries.serializeEntries(keySerializer))
    put("backstack", state.backstackEntries.serializeBackstackEntries(keySerializer))
  }
)

private fun <KeyT, EntryT, ExtraT, PortalT : Portal> List<EntryT>.serializeEntries(
  keySerializer: (KeyT) -> String
) where EntryT : Entry<KeyT, ExtraT, PortalT>, ExtraT : Extra = buildJsonArray {
  forEach { entry ->
    add(
      buildJsonObject {
        put("key", keySerializer(entry.key))
        put("wasContentPreviouslyVisible", entry.wasContentPreviouslyVisible)
        put("isAttached", entry.rendererState.isAddedOrAttached)
        put("isDisappearing", entry.isDisappearing)
        put("isBackstackMutation", entry.isBackstackMutation)
        put("rendererState", entry.rendererState.name)
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

        is PortalBackstackMutation.Disappearing -> put("type", "disappearing")
      }
    }
  }
}
