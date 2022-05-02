package com.eygraber.portal

import com.eygraber.portal.internal.EnterExtra
import com.eygraber.portal.internal.Entry
import com.eygraber.portal.internal.ExitExtra
import com.eygraber.portal.internal.PortalBackstackEntry
import com.eygraber.portal.internal.PortalEntry
import com.eygraber.portal.internal.PortalState
import com.eygraber.portal.internal.deserializePortalManagerState
import com.eygraber.portal.internal.serializePortalManagerState
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

public interface PortalManagerQueries<KeyT> {
  public val size: Int

  public val portals: List<Pair<KeyT, Portal>>

  public operator fun contains(key: KeyT): Boolean
}

@DslMarker
internal annotation class PortalTransactionBuilderDsl

public abstract class AbstractPortalManager<KeyT, EntryT, EnterExtraT, ExitExtraT, PortalT>(
  private val defaultErrorHandler: ((Throwable) -> Unit)? = null,
  private val entryCallbacks: PortalEntry.Callbacks<KeyT, EntryT, EnterExtraT, ExitExtraT, PortalT>,
  validation: PortalManagerValidation = PortalManagerValidation()
) : PortalManagerQueries<KeyT>
  where EntryT : Entry<KeyT, EnterExtraT, ExitExtraT, PortalT>,
        EnterExtraT : EnterExtra,
        ExitExtraT : ExitExtra,
        PortalT : Portal {
  override val size: Int get() = portalState.portalEntries.filterNot { it.isDisappearing }.size

  override val portals: List<Pair<KeyT, PortalT>> get() = portalState.portalEntries.map { it.key to it.portal }

  override operator fun contains(key: KeyT): Boolean =
    portalState.portalEntries.findLast { entry ->
      entry.key == key
    } != null

  public fun saveState(
    keySerializer: (KeyT) -> String
  ): String = lock.withLock {
    serializePortalManagerState(keySerializer, portalState)
  }

  public fun restoreState(
    serializedState: String,
    keyDeserializer: (String) -> KeyT,
    portalFactory: (KeyT) -> PortalT
  ) {
    lock.withLock {
      val (entries, backstack) = deserializePortalManagerState(
        serializedState = serializedState,
        keyDeserializer = keyDeserializer,
        portalFactory = portalFactory,
        entryCallbacks = entryCallbacks
      )

      portalState.restoreState(
        entries = entries,
        backstack = backstack
      )
    }
  }

  public fun withTransaction(
    errorHandler: ((Throwable) -> Unit)? = defaultErrorHandler,
    @PortalTransactionBuilderDsl builder: EntryBuilder<KeyT, EnterExtraT, ExitExtraT, PortalT>.() -> Unit
  ) {
    lock.withLock {
      portalState.startTransaction(_backstack)

      try {
        portalState.transact(builder)
        portalState.commitTransaction()
      }
      catch(error: Throwable) {
        errorHandler?.invoke(error)
        portalState.rollbackTransaction()
      }
    }
  }

  protected fun makeEntryDisappear(key: KeyT) {
    portalState.transact {
      disappear(key)
    }
  }

  public fun updates(): Flow<Unit> = portalState.portalEntriesUpdateFlow().map {}

  private val lock = reentrantLock()
  private val portalState = PortalState(
    validation = validation,
    entryCallbacks = entryCallbacks
  )

  protected fun portalEntriesUpdateFlow(): StateFlow<List<EntryT>> =
    portalState.portalEntriesUpdateFlow()

  protected fun backstackEntriesUpdateFlow(): StateFlow<List<PortalBackstackEntry<KeyT>>> =
    portalState.backstackEntriesUpdateFlow()

  private val _backstack: PortalBackstack<KeyT, EnterExtraT, ExitExtraT, PortalT> =
    PortalBackstackImpl(portalState)

  public val backstack: ReadOnlyBackstack = _backstack

  public interface EntryBuilder<KeyT, EnterExtraT, ExitExtraT, PortalT> : PortalManagerQueries<KeyT>
    where EnterExtraT : EnterExtra,
          ExitExtraT : ExitExtra,
          PortalT : Portal {
    public val backstack: PortalBackstack<KeyT, EnterExtraT, ExitExtraT, PortalT>

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

    public fun remove(
      key: KeyT,
      extra: ExitExtraT? = null
    )

    public fun clear(
      extraProvider: ((KeyT) -> ExitExtraT?)? = null
    )
  }
}
