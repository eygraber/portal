package com.eygraber.portal.internal

import com.eygraber.portal.KeyedPortal
import com.eygraber.portal.PortalBackstackState
import com.eygraber.portal.PortalEntry
import com.eygraber.portal.PortalRendererState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun <KeyT> deserializePortalManagerState(
  serializedState: String,
  keyDeserializer: (String) -> KeyT,
  portalFactory: (KeyT) -> KeyedPortal<KeyT>,
): Pair<List<PortalEntry<KeyT>>, List<PortalBackstackEntry<KeyT>>> {
  val json = Json.parseToJsonElement(serializedState).jsonObject

  val entries = requireNotNull(json["entries"]) {
    "Serialized PortalManager state must have a top level field named \"entries\""
  }.jsonArray.deserializeToPortalEntries(
    keyDeserializer = keyDeserializer,
    portalFactory = portalFactory,
  )

  val backstack = requireNotNull(json["backstack"]) {
    "Serialized PortalManager state must have a top level field named \"backstack\""
  }.jsonArray.deserializeToBackstackEntries(keyDeserializer)

  return entries to backstack
}

private fun <KeyT> JsonArray.deserializeToPortalEntries(
  keyDeserializer: (String) -> KeyT,
  portalFactory: (KeyT) -> KeyedPortal<KeyT>,
) = map { entry ->
  val jsonEntry = entry.jsonObject

  val key = requireNotNull(
    jsonEntry["key"]?.jsonPrimitive?.contentOrNull,
  ) {
    "A serialized PortalEntry needs a \"key\" field"
  }.let(keyDeserializer)

  PortalEntry(
    portal = portalFactory(key),
    wasContentPreviouslyVisible = requireNotNull(
      jsonEntry["wasContentPreviouslyVisible"]?.jsonPrimitive?.contentOrNull,
    ) {
      "A serialized PortalEntry needs a \"wasContentPreviouslyVisible\" field"
    }.toBoolean(),
    backstackState = requireNotNull(
      jsonEntry["backstackState"]?.jsonPrimitive?.contentOrNull,
    ) {
      "A serialized PortalEntry needs a \"backstackState\" field"
    }.let(PortalBackstackState::valueOf),
    rendererState = requireNotNull(
      jsonEntry["rendererState"]?.jsonPrimitive?.contentOrNull,
    ) {
      "A serialized PortalEntry needs a \"rendererState\" field"
    }.let(PortalRendererState::valueOf),
    enterTransitionOverride = null,
    exitTransitionOverride = null,
    uid = requireNotNull(
      jsonEntry["uid"]?.jsonPrimitive?.contentOrNull?.toIntOrNull()?.let { PortalEntry.Id(it) },
    ) {
      "A serialized PortalEntry needs a \"uid\" field"
    },
  )
}

private fun <KeyT> JsonArray.deserializeToBackstackEntries(
  keyDeserializer: (String) -> KeyT,
) = map { entry ->
  val jsonEntry = entry.jsonObject

  PortalBackstackEntry(
    id = requireNotNull(
      jsonEntry["id"]?.jsonPrimitive?.contentOrNull,
    ) {
      "A serialized PortalBackstackEntry needs an \"id\" field"
    },
    mutations = requireNotNull(jsonEntry["mutations"]) {
      "A serialized PortalBackstackEntry needs a \"mutations\" field"
    }.jsonArray.deserializeToBackstackMutations(keyDeserializer),
  )
}

private fun <KeyT> JsonArray.deserializeToBackstackMutations(
  keyDeserializer: (String) -> KeyT,
) = map { mutation ->
  val jsonMutation = mutation.jsonObject

  val key = requireNotNull(
    jsonMutation["key"]?.jsonPrimitive?.contentOrNull,
  ) {
    "A serialized PortalBackstackMutation needs a \"key\" field"
  }.let(keyDeserializer)

  val uid = requireNotNull(
    jsonMutation["uid"]?.jsonPrimitive?.contentOrNull?.toIntOrNull()?.let { PortalEntry.Id(it) },
  ) {
    "A serialized PortalBackstackMutation needs a \"uid\" field"
  }

  val type = requireNotNull(
    jsonMutation["type"]?.jsonPrimitive?.contentOrNull,
  ) {
    "A serialized PortalBackstackMutation needs a \"type\" field"
  }

  when(type) {
    "remove" -> PortalBackstackMutation.Remove(
      key = key,
      uid = uid,
    )

    "attach" -> PortalBackstackMutation.Attach(
      key = key,
      uid = uid,
    )

    "detach" -> PortalBackstackMutation.Detach(
      key = key,
      uid = uid,
    )

    else -> error("\"$type\" is not a valid value")
  }
}
