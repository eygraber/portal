package com.eygraber.portal

import com.eygraber.portal.internal.EnterExtra
import com.eygraber.portal.internal.Entry
import com.eygraber.portal.internal.ExitExtra
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

public interface PortalBackstack<KeyT, EnterExtraT, ExitExtraT, PortalT> : ReadOnlyBackstack
  where EnterExtraT : EnterExtra,
        ExitExtraT : ExitExtra,
        PortalT : Portal {
  public fun push(
    backstackEntryId: String,
    @BackstackDsl builder: PushBuilder<KeyT, EnterExtraT, ExitExtraT, PortalT>.() -> Unit
  )

  public fun pop(
    untilBackstackEntryId: String? = null,
    inclusive: Boolean = true,
    enterExtra: ((KeyT) -> EnterExtraT?)? = null,
    exitExtra: ((KeyT) -> ExitExtraT?)? = null
  ): Boolean

  public fun clear(
    suppressTransitions: Boolean = true,
    enterExtra: ((KeyT) -> EnterExtraT?)? = null,
    exitExtra: ((KeyT) -> ExitExtraT?)? = null
  ): Boolean

  public interface PushBuilder<KeyT, EnterExtraT : EnterExtra, ExitExtraT : ExitExtra, PortalT : Portal> {
    public fun add(
      key: KeyT,
      isAttachedToComposition: Boolean = true,
      extra: EnterExtraT? = null,
      portal: PortalT
    )

    public fun attachToComposition(
      key: KeyT,
      extra: EnterExtraT? = null
    )

    public fun detachFromComposition(
      key: KeyT,
      extra: ExitExtraT? = null
    )
  }
}

internal class PortalBackstackImpl<KeyT, EntryT, EnterExtraT, ExitExtraT, PortalT>(
  private val portalState: PortalState<KeyT, EntryT, EnterExtraT, ExitExtraT, PortalT>
) : PortalBackstack<KeyT, EnterExtraT, ExitExtraT, PortalT>
  where EntryT : Entry<KeyT, EnterExtraT, ExitExtraT, PortalT>,
        EnterExtraT : EnterExtra,
        ExitExtraT : ExitExtra,
        PortalT : Portal {
  override val size: Int get() = portalState.backstackEntries.size

  override val changes = portalState.backstackEntriesUpdateFlow().map {}

  override fun contains(backstackEntryId: String) =
    portalState.backstackEntries.indexOfLast { it.id == backstackEntryId } >= 0

  override fun peek() = portalState.backstackEntries.lastOrNull()?.id

  override fun push(
    backstackEntryId: String,
    builder: PortalBackstack.PushBuilder<KeyT, EnterExtraT, ExitExtraT, PortalT>.() -> Unit
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
    enterExtra: ((KeyT) -> EnterExtraT?)?,
    exitExtra: ((KeyT) -> ExitExtraT?)?
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
    enterExtra: ((KeyT) -> EnterExtraT?)?,
    exitExtra: ((KeyT) -> ExitExtraT?)?
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

  private fun PortalEntryBuilder<KeyT, EntryT, EnterExtraT, ExitExtraT, PortalT>.tryToActuallyPopBackstack(
    backstackStack: MutableList<PortalBackstackEntry<KeyT>>,
    enterExtra: ((KeyT) -> EnterExtraT?)?,
    exitExtra: ((KeyT) -> ExitExtraT?)?,
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

  private inline fun <R> PortalState<KeyT, EntryT, EnterExtraT, ExitExtraT, PortalT>.transactWithBackstack(
    crossinline block: PortalEntryBuilder<KeyT, EntryT, EnterExtraT, ExitExtraT, PortalT>.(
      MutableList<PortalBackstackEntry<KeyT>>
    ) -> R
  ) = transact {
    usingBackstack(block)
  }
}

private fun <KeyT, EntryT, EnterExtraT, ExitExtraT, PortalT>
PortalEntryBuilder<KeyT, EntryT, EnterExtraT, ExitExtraT, PortalT>.applyBackstackMutations(
  mutations: List<PortalBackstackMutation<KeyT>>,
  enterExtra: ((KeyT) -> EnterExtraT?)?,
  exitExtra: ((KeyT) -> ExitExtraT?)?
) where EntryT : Entry<KeyT, EnterExtraT, ExitExtraT, PortalT>,
        EnterExtraT : EnterExtra,
        ExitExtraT : ExitExtra,
        PortalT : Portal {
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

public fun <KeyT, EnterExtraT, ExitExtraT, PortalT>
PortalBackstack<KeyT, EnterExtraT, ExitExtraT, PortalT>.push(
  backstackEntryId: KeyT,
  builder: PortalBackstack.PushBuilder<KeyT, EnterExtraT, ExitExtraT, PortalT>.() -> Unit
) where EnterExtraT : EnterExtra,
        ExitExtraT : ExitExtra,
        PortalT : Portal {
  push(backstackEntryId.toString(), builder)
}

public fun <KeyT, EnterExtraT, ExitExtraT, PortalT>
PortalBackstack<KeyT, EnterExtraT, ExitExtraT, PortalT>.pop(
  untilBackstackEntryId: KeyT? = null,
  inclusive: Boolean = true,
  enterExtra: ((KeyT) -> EnterExtraT?)? = null,
  exitExtra: ((KeyT) -> ExitExtraT?)? = null
): Boolean where EnterExtraT : EnterExtra,
                 ExitExtraT : ExitExtra,
                 PortalT : Portal =
  pop(
    untilBackstackEntryId.toString(),
    inclusive,
    enterExtra,
    exitExtra
  )

public fun <KeyT, EnterExtraT, ExitExtraT, PortalT>
PortalBackstack<KeyT, EnterExtraT, ExitExtraT, PortalT>.contains(
  backstackEntryId: KeyT
): Boolean where EnterExtraT : EnterExtra,
                 ExitExtraT : ExitExtra,
                 PortalT : Portal =
  backstackEntryId.toString() in this

public fun <KeyT, EnterExtraT, ExitExtraT, PortalT>
PortalBackstack<KeyT, EnterExtraT, ExitExtraT, PortalT>.isTop(
  backstackEntryId: String
): Boolean where EnterExtraT : EnterExtra,
                 ExitExtraT : ExitExtra,
                 PortalT : Portal =
  peek() == backstackEntryId

public fun <KeyT, EnterExtraT, ExitExtraT, PortalT>
PortalBackstack<KeyT, EnterExtraT, ExitExtraT, PortalT>.isTop(
  backstackEntryId: KeyT
): Boolean where EnterExtraT : EnterExtra,
                 ExitExtraT : ExitExtra,
                 PortalT : Portal =
  isTop(backstackEntryId.toString())
