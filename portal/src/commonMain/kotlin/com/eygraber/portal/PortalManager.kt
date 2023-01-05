package com.eygraber.portal

import com.eygraber.portal.internal.PortalBackstackEntry
import com.eygraber.portal.internal.PortalState
import com.eygraber.portal.internal.deserializePortalManagerState
import com.eygraber.portal.internal.serializePortalManagerState
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

public interface PortalManagerQueries<KeyT> {
  public val size: Int

  public val portalEntries: List<PortalEntry<KeyT>>

  public operator fun contains(key: KeyT): Boolean
}

@DslMarker
internal annotation class PortalTransactionBuilderDsl

public abstract class PortalManager<KeyT>(
  private val defaultErrorHandler: ((Throwable) -> Unit)? = null,
  validation: PortalManagerValidation = PortalManagerValidation()
) : PortalManagerQueries<KeyT> {
  override val size: Int
    get() = portalState.portalEntries.filterNot { it.isDisappearing }.size

  override val portalEntries: List<PortalEntry<KeyT>>
    get() = portalState.portalEntries

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
    portalFactory: (KeyT) -> KeyedPortal<KeyT>
  ) {
    lock.withLock {
      val (entries, backstack) = deserializePortalManagerState(
        serializedState = serializedState,
        keyDeserializer = keyDeserializer,
        portalFactory = portalFactory
      )

      portalState.restoreState(
        entries = entries,
        backstack = backstack
      )
    }
  }

  public fun withTransaction(
    errorHandler: ((Throwable) -> Unit)? = defaultErrorHandler,
    @PortalTransactionBuilderDsl builder: EntryBuilder<KeyT>.() -> Unit
  ) {
    lock.withLock {
      val isRootTransaction = portalState.startTransaction(_backstack)

      try {
        portalState.transact(builder)
        if(isRootTransaction) portalState.commitTransaction()
      }
      catch(error: Throwable) {
        errorHandler?.invoke(error)
        portalState.rollbackTransaction()
      }
    }
  }

  protected fun markAddedEntryAsAttached(key: KeyT) {
    portalState.transact {
      attachToComposition(key)
    }
  }

  protected fun makeEntryDisappear(key: KeyT) {
    portalState.transact {
      disappear(key)
    }
  }

  public fun updates(): Flow<List<PortalEntry<KeyT>>> = portalState.portalEntriesUpdateFlow()

  private val lock = reentrantLock()
  private val portalState = PortalState<KeyT>(validation = validation)

  protected fun portalEntriesUpdateFlow(): StateFlow<List<PortalEntry<KeyT>>> =
    portalState.portalEntriesUpdateFlow()

  protected fun backstackEntriesUpdateFlow(): StateFlow<List<PortalBackstackEntry<KeyT>>> =
    portalState.backstackEntriesUpdateFlow()

  private val _backstack: PortalBackstack<KeyT> = PortalBackstackImpl(portalState)

  public val backstack: ReadOnlyBackstack = _backstack

  public interface EntryBuilder<KeyT> : PortalManagerQueries<KeyT> {
    public val backstack: PortalBackstack<KeyT>

    public fun add(
      portal: KeyedPortal<KeyT>,
      isAttachedToComposition: Boolean = true,
      transitionOverride: EnterTransitionOverride? = null
    )

    public fun attachToComposition(
      key: KeyT,
      transitionOverride: EnterTransitionOverride? = null
    )

    public fun detachFromComposition(
      key: KeyT,
      transitionOverride: ExitTransitionOverride? = null
    )

    public fun remove(
      key: KeyT,
      transitionOverride: ExitTransitionOverride? = null
    )

    /**
     * [clearDisappearingEntries] can be set to `true`
     * if you want to clear removed entries that are still running their [ExitTransitionOverride].
     */
    public fun clear(
      clearDisappearingEntries: Boolean = false,
      transitionOverrideProvider: ((KeyT) -> ExitTransitionOverride?)? = null
    )
  }
}

public val PortalManager<*>.isEmpty: Boolean get() = size == 0
public val PortalManager<*>.isNotEmpty: Boolean get() = size > 0
