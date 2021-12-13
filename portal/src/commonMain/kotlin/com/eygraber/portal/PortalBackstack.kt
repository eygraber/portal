package com.eygraber.portal

import com.eygraber.portal.internal.Entry
import com.eygraber.portal.internal.Extra
import com.eygraber.portal.internal.PortalBackstackEntry
import com.eygraber.portal.internal.PortalBackstackEntryBuilder
import com.eygraber.portal.internal.PortalBackstackMutation
import com.eygraber.portal.internal.PortalEntryBuilder
import com.eygraber.portal.internal.PortalState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@DslMarker
internal annotation class BackstackDsl

public interface ReadOnlyBackstack {
  public val size: Int

  public val changes: Flow<Unit>

  public operator fun contains(backstackEntryId: String): Boolean

  public fun peek(): String?
}

public interface PortalBackstack<KeyT, ExtraT : Extra, PortalT : Portal> : ReadOnlyBackstack {
  public fun push(
    backstackEntryId: String,
    @BackstackDsl builder: PushBuilder<KeyT, ExtraT, PortalT>.() -> Unit
  )

  public fun pop(
    untilBackstackEntryId: String? = null,
    inclusive: Boolean = true,
    enterExtra: ((KeyT) -> ExtraT?)? = null,
    exitExtra: ((KeyT) -> ExtraT?)? = null
  ): Boolean

  public fun clear(
    suppressTransitions: Boolean = true,
    enterExtra: ((KeyT) -> ExtraT?)? = null,
    exitExtra: ((KeyT) -> ExtraT?)? = null
  ): Boolean

  public interface PushBuilder<KeyT, ExtraT : Extra, PortalT : Portal> {
    public fun add(
      key: KeyT,
      isAttachedToComposition: Boolean = true,
      extra: ExtraT? = null,
      portal: PortalT
    )

    public fun attachToComposition(
      key: KeyT,
      extra: ExtraT? = null
    )

    public fun detachFromComposition(
      key: KeyT,
      extra: ExtraT? = null
    )
  }
}

internal class PortalBackstackImpl<KeyT, EntryT, ExtraT : Extra, PortalT : Portal>(
  private val portalState: PortalState<KeyT, EntryT, ExtraT, PortalT>
) : PortalBackstack<KeyT, ExtraT, PortalT> where EntryT : Entry<KeyT, ExtraT, PortalT> {
  override val size: Int get() = portalState.backstackEntries.size

  override val changes = portalState.backstackEntriesUpdateFlow().map {}

  override fun contains(backstackEntryId: String) =
    portalState.backstackEntries.indexOfLast { it.id == backstackEntryId } >= 0

  override fun peek() = portalState.backstackEntries.lastOrNull()?.id

  override fun push(
    backstackEntryId: String,
    builder: PortalBackstack.PushBuilder<KeyT, ExtraT, PortalT>.() -> Unit
  ) {
    portalState.transactWithBackstack { backstackStack ->
      val backstackMutations =
        PortalBackstackEntryBuilder(this)
          .apply(builder)
          .build()

      backstackStack += listOf(
        PortalBackstackEntry(
          id = backstackEntryId,
          mutations = backstackMutations
        )
      )
    }
  }

  override fun clear(
    suppressTransitions: Boolean,
    enterExtra: ((KeyT) -> ExtraT?)?,
    exitExtra: ((KeyT) -> ExtraT?)?
  ): Boolean = portalState.transactWithBackstack { backstackStack ->
    val originalSize = backstackStack.size

    backstackStack
      .reversed()
      .forEach { entry ->
        applyBackstackMutations(
          entry.mutations.reversed(),
          enterExtra,
          exitExtra
        )
      }

    backstackStack.clear()

    originalSize > backstackStack.size
  }

  override fun pop(
    untilBackstackEntryId: String?,
    inclusive: Boolean,
    enterExtra: ((KeyT) -> ExtraT?)?,
    exitExtra: ((KeyT) -> ExtraT?)?
  ): Boolean = portalState.transactWithBackstack { backstackStack ->
    val originalSize = backstackStack.size
    var stop = false
    do {
      tryToActuallyPopBackstack(
        backstackStack,
        enterExtra,
        exitExtra
      ) { entryToPop ->
        if(untilBackstackEntryId == null || entryToPop.id == untilBackstackEntryId) {
          stop = true

          untilBackstackEntryId == null || inclusive
        }
        else {
          true
        }
      }
    } while(!stop && backstackStack.size > 0)

    originalSize > backstackStack.size
  }

  private fun PortalEntryBuilder<KeyT, EntryT, ExtraT, PortalT>.tryToActuallyPopBackstack(
    backstackStack: MutableList<PortalBackstackEntry<KeyT>>,
    enterExtra: ((KeyT) -> ExtraT?)?,
    exitExtra: ((KeyT) -> ExtraT?)?,
    popPredicate: (PortalBackstackEntry<KeyT>) -> Boolean
  ) {
    when(val peek = backstackStack.lastOrNull()) {
      null -> Unit

      else -> when {
        popPredicate(peek) -> {
          backstackStack.removeLast()

          applyBackstackMutations(
            peek.mutations,
            enterExtra,
            exitExtra
          )
        }
      }
    }
  }

  private inline fun <R> PortalState<KeyT, EntryT, ExtraT, PortalT>.transactWithBackstack(
    crossinline block: PortalEntryBuilder<KeyT, EntryT, ExtraT, PortalT>.(
      MutableList<PortalBackstackEntry<KeyT>>
    ) -> R
  ) = transact {
    usingBackstack(block)
  }
}

private fun <KeyT, EntryT, ExtraT, PortalT> PortalEntryBuilder<KeyT, EntryT, ExtraT, PortalT>.applyBackstackMutations(
  mutations: List<PortalBackstackMutation<KeyT>>,
  enterExtra: ((KeyT) -> ExtraT?)?,
  exitExtra: ((KeyT) -> ExtraT?)?
) where EntryT : Entry<KeyT, ExtraT, PortalT>, ExtraT : Extra, PortalT : Portal {
  mutations.forEach { mutation ->
    when(mutation) {
      is PortalBackstackMutation.Remove -> remove(
        key = mutation.key,
        extra = exitExtra?.invoke(mutation.key)
      )

      is PortalBackstackMutation.Attach -> attachToComposition(
        key = mutation.key,
        extra = enterExtra?.invoke(mutation.key)
      )

      is PortalBackstackMutation.Detach -> detachFromComposition(
        key = mutation.key,
        extra = exitExtra?.invoke(mutation.key)
      )

      is PortalBackstackMutation.Disappearing -> disappear(
        key = mutation.key
      )
    }
  }
}

public fun <KeyT, ExtraT : Extra, PortalT : Portal> PortalBackstack<KeyT, ExtraT, PortalT>.push(
  backstackEntryId: KeyT,
  builder: PortalBackstack.PushBuilder<KeyT, ExtraT, PortalT>.() -> Unit
) {
  push(backstackEntryId.toString(), builder)
}

public fun <KeyT, ExtraT : Extra, PortalT : Portal> PortalBackstack<KeyT, ExtraT, PortalT>.pop(
  untilBackstackEntryId: KeyT? = null,
  inclusive: Boolean = true,
  enterExtra: ((KeyT) -> ExtraT?)? = null,
  exitExtra: ((KeyT) -> ExtraT?)? = null
): Boolean = pop(
  untilBackstackEntryId.toString(),
  inclusive,
  enterExtra,
  exitExtra
)

public fun <KeyT, ExtraT : Extra, PortalT : Portal> PortalBackstack<KeyT, ExtraT, PortalT>.contains(
  backstackEntryId: KeyT
): Boolean = backstackEntryId.toString() in this

public fun <KeyT, ExtraT : Extra, PortalT : Portal> PortalBackstack<KeyT, ExtraT, PortalT>.isTop(
  backstackEntryId: String
): Boolean = peek() == backstackEntryId

public fun <KeyT, ExtraT : Extra, PortalT : Portal> PortalBackstack<KeyT, ExtraT, PortalT>.isTop(
  backstackEntryId: KeyT
): Boolean = isTop(backstackEntryId.toString())
