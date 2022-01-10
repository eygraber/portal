package com.eygraber.portal.internal

import com.eygraber.portal.Portal
import com.eygraber.portal.PortalRendererState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun <KeyT, EntryT, ExtraT : Extra, PortalT : Portal> deserializePortalManagerState(
  serializedState: String,
  keyDeserializer: (String) -> KeyT,
  portalFactory: (KeyT) -> PortalT,
  entryCallbacks: PortalEntry.Callbacks<KeyT, EntryT, ExtraT, PortalT>
): Pair<List<EntryT>, List<PortalBackstackEntry<KeyT>>> where EntryT : Entry<KeyT, ExtraT, PortalT> {
  val json = Json.parseToJsonElement(serializedState).jsonObject

  val entries = requireNotNull(json["entries"]) {
    "Serialized PortalManager state must have a top level field named \"entries\""
  }.jsonArray.deserializeToPortalEntries(
    keyDeserializer = keyDeserializer,
    portalFactory = portalFactory,
    entryCallbacks = entryCallbacks
  )

  val backstack = requireNotNull(json["backstack"]) {
    "Serialized PortalManager state must have a top level field named \"backstack\""
  }.jsonArray.deserializeToBackstackEntries(keyDeserializer)

  return entries to backstack
}

private fun <KeyT, EntryT, ExtraT, PortalT> JsonArray.deserializeToPortalEntries(
  keyDeserializer: (String) -> KeyT,
  portalFactory: (KeyT) -> PortalT,
  entryCallbacks: PortalEntry.Callbacks<KeyT, EntryT, ExtraT, PortalT>
) where EntryT : Entry<KeyT, ExtraT, PortalT>, ExtraT : Extra, PortalT : Portal = map { entry ->
  val jsonEntry = entry.jsonObject

  val key = requireNotNull(
    jsonEntry["key"]?.jsonPrimitive?.contentOrNull
  ) {
    "A serialized PortalEntry needs a \"key\" field"
  }.let(keyDeserializer)

  entryCallbacks.create(
    key = key,
    wasContentPreviouslyVisible = requireNotNull(
      jsonEntry["wasContentPreviouslyVisible"]?.jsonPrimitive?.contentOrNull
    ) {
      "A serialized PortalEntry needs a \"wasContentPreviouslyVisible\" field"
    }.toBoolean(),
    isDisappearing = requireNotNull(
      jsonEntry["isDisappearing"]?.jsonPrimitive?.contentOrNull
    ) {
      "A serialized PortalEntry needs a \"isDisappearing\" field"
    }.toBoolean(),
    isBackstackMutation = requireNotNull(
      jsonEntry["isBackstackMutation"]?.jsonPrimitive?.contentOrNull
    ) {
      "A serialized PortalEntry needs a \"isBackstackMutation\" field"
    }.toBoolean(),
    rendererState = requireNotNull(
      jsonEntry["isBackstackMutation"]?.jsonPrimitive?.contentOrNull
    ) {
      "A serialized PortalEntry needs a \"rendererState\" field"
    }.let(PortalRendererState::valueOf),
    extra = null,
    portal = portalFactory(key)
  )
}

private fun <KeyT> JsonArray.deserializeToBackstackEntries(
  keyDeserializer: (String) -> KeyT
) = map { entry ->
  val jsonEntry = entry.jsonObject

  PortalBackstackEntry(
    id = requireNotNull(
      jsonEntry["id"]?.jsonPrimitive?.contentOrNull
    ) {
      "A serialized PortalBackstackEntry needs an \"id\" field"
    },
    mutations = requireNotNull(jsonEntry["mutations"]) {
      "A serialized PortalBackstackEntry needs a \"mutations\" field"
    }.jsonArray.deserializeToBackstackMutations(keyDeserializer)
  )
}

private fun <KeyT> JsonArray.deserializeToBackstackMutations(
  keyDeserializer: (String) -> KeyT
) = map { mutation ->
  val jsonMutation = mutation.jsonObject

  val key = requireNotNull(
    jsonMutation["key"]?.jsonPrimitive?.contentOrNull
  ) {
    "A serialized PortalBackstackMutation needs a \"key\" field"
  }.let(keyDeserializer)

  val type = requireNotNull(
    jsonMutation["type"]?.jsonPrimitive?.contentOrNull
  ) {
    "A serialized PortalBackstackMutation needs a \"type\" field"
  }

  when(type) {
    "remove" -> PortalBackstackMutation.Remove(
      key = key
    )

    "attach" -> PortalBackstackMutation.Attach(
      key = key
    )

    "detach" -> PortalBackstackMutation.Detach(
      key = key
    )

    "disappearing" -> PortalBackstackMutation.Disappearing(
      key = key
    )

    else -> error("\"$type\" is not a valid value")
  }
}