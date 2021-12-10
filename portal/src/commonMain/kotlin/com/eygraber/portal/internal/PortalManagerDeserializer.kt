package com.eygraber.portal.internal

import com.eygraber.portal.Portal
import com.eygraber.portal.PortalCompositionState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun <PortalKey> deserializePortalManagerState(
  serializedState: String,
  portalKeyDeserializer: (String) -> PortalKey,
  portalFactory: (PortalKey) -> Portal
): Pair<List<PortalEntry<PortalKey>>, List<PortalBackstackEntry<PortalKey>>> {
  val json = Json.parseToJsonElement(serializedState).jsonObject

  val entries = requireNotNull(json["entries"]) {
    "Serialized PortalManager state must have a top level field named \"entries\""
  }.jsonArray.deserializeToPortalEntries(
    portalKeyDeserializer = portalKeyDeserializer,
    portalFactory = portalFactory
  )

  val backstack = requireNotNull(json["backstack"]) {
    "Serialized PortalManager state must have a top level field named \"backstack\""
  }.jsonArray.deserializeToBackstackEntries(portalKeyDeserializer)

  return entries to backstack
}

private fun <PortalKey> JsonArray.deserializeToPortalEntries(
  portalKeyDeserializer: (String) -> PortalKey,
  portalFactory: (PortalKey) -> Portal
) = map { entry ->
  val jsonEntry = entry.jsonObject

  val portalKey = requireNotNull(
    jsonEntry["key"]?.jsonPrimitive?.contentOrNull
  ) {
    "A serialized PortalEntry needs a \"key\" field"
  }.let(portalKeyDeserializer)

  PortalEntry(
    key = portalKey,
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
    compositionState = requireNotNull(
      jsonEntry["isBackstackMutation"]?.jsonPrimitive?.contentOrNull
    ) {
      "A serialized PortalEntry needs a \"compositionState\" field"
    }.let(PortalCompositionState::valueOf),
    transitionOverride = null,
    portal = portalFactory(portalKey)
  )
}

private fun <PortalKey> JsonArray.deserializeToBackstackEntries(
  portalKeyDeserializer: (String) -> PortalKey
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
    }.jsonArray.deserializeToBackstackMutations(portalKeyDeserializer)
  )
}

private fun <PortalKey> JsonArray.deserializeToBackstackMutations(
  portalKeyDeserializer: (String) -> PortalKey
) = map { mutation ->
  val jsonMutation = mutation.jsonObject

  val portalKey = requireNotNull(
    jsonMutation["key"]?.jsonPrimitive?.contentOrNull
  ) {
    "A serialized PortalBackstackMutation needs a \"key\" field"
  }.let(portalKeyDeserializer)

  val type = requireNotNull(
    jsonMutation["type"]?.jsonPrimitive?.contentOrNull
  ) {
    "A serialized PortalBackstackMutation needs a \"type\" field"
  }

  when(type) {
    "remove" -> PortalBackstackMutation.Remove(
      key = portalKey
    )

    "attach" -> PortalBackstackMutation.AttachToComposition(
      key = portalKey
    )

    "detach" -> PortalBackstackMutation.DetachFromComposition(
      key = portalKey
    )

    "disappearing" -> PortalBackstackMutation.Disappearing(key = portalKey)

    else -> error("\"$type\" is not a valid value")
  }
}
