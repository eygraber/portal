package com.eygraber.portal

import com.eygraber.portal.internal.PortalBackstackEntry
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

  public val portalEntries: List<PortalEntry<KeyT>>

  public operator fun contains(key: KeyT): Boolean
  public operator fun contains(uid: PortalEntry.Id): Boolean
}

@Target(AnnotationTarget.CLASS)
@DslMarker
public annotation class PortalTransactionBuilderDsl

public abstract class PortalManager<KeyT>(
  private val defaultErrorHandler: ((Throwable) -> Unit)? = null,
  validation: PortalManagerValidation = PortalManagerValidation(),
) : PortalManagerQueries<KeyT> {
  override val size: Int
    get() = portalState.portalEntries.size

  override val portalEntries: List<PortalEntry<KeyT>>
    get() = portalState.portalEntries

  override operator fun contains(key: KeyT): Boolean =
    portalState.portalEntries.findLast { entry ->
      entry.key == key
    } != null

  override operator fun contains(uid: PortalEntry.Id): Boolean =
    portalState.portalEntries.findLast { entry ->
      entry.uid == uid
    } != null

  public fun saveState(
    keySerializer: (KeyT) -> String,
  ): String = lock.withLock {
    serializePortalManagerState(keySerializer, portalState)
  }

  public fun restoreState(
    serializedState: String,
    keyDeserializer: (String) -> KeyT,
    portalFactory: (KeyT) -> KeyedPortal<KeyT>,
  ) {
    lock.withLock {
      val (entries, backstack) = deserializePortalManagerState(
        serializedState = serializedState,
        keyDeserializer = keyDeserializer,
        portalFactory = portalFactory,
      )

      portalState.restoreState(
        entries = entries,
        backstack = backstack,
      )
    }
  }

  public fun withTransaction(
    errorHandler: ((Throwable) -> Unit)? = defaultErrorHandler,
    builder: EntryBuilder<KeyT>.() -> Unit,
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

  protected fun makeEntryDisappear(uid: PortalEntry.Id) {
    portalState.transact {
      disappear(uid)
    }
  }

  public fun updates(): Flow<List<PortalEntry<KeyT>>> = portalState.portalEntriesUpdateFlow().map { it.entries }

  private val lock = reentrantLock()
  private val portalState = PortalState<KeyT>(validation = validation)

  protected fun portalEntriesUpdateFlow(): StateFlow<Entries<KeyT>> =
    portalState.portalEntriesUpdateFlow()

  protected fun backstackEntriesUpdateFlow(): StateFlow<List<PortalBackstackEntry<KeyT>>> =
    portalState.backstackEntriesUpdateFlow()

  private val _backstack: PortalBackstack<KeyT> = PortalBackstackImpl(portalState)

  public val backstack: ReadOnlyBackstack<KeyT> = _backstack

  public data class Entries<KeyT>(
    val entries: List<PortalEntry<KeyT>>,
    val disappearingEntries: List<DisappearingPortalEntry<KeyT>>,
  )

  @PortalTransactionBuilderDsl
  public interface EntryBuilder<KeyT> : PortalManagerQueries<KeyT> {
    public val backstack: PortalBackstack<KeyT>

    public fun add(
      portal: KeyedPortal<out KeyT>,
      isAttachedToComposition: Boolean = true,
      transitionOverride: EnterTransitionOverride? = null,
    ): PortalEntry<KeyT>

    public fun attachToComposition(
      key: KeyT,
      transitionOverride: EnterTransitionOverride? = null,
    ): PortalEntry<KeyT>?

    public fun attachToComposition(
      uid: PortalEntry.Id,
      transitionOverride: EnterTransitionOverride? = null,
    ): PortalEntry<KeyT>?

    public fun detachFromComposition(
      key: KeyT,
      transitionOverride: ExitTransitionOverride? = null,
    ): PortalEntry<KeyT>?

    public fun detachFromComposition(
      uid: PortalEntry.Id,
      transitionOverride: ExitTransitionOverride? = null,
    ): PortalEntry<KeyT>?

    public fun remove(
      key: KeyT,
      transitionOverride: ExitTransitionOverride? = null,
    ): PortalEntry<KeyT>?

    public fun remove(
      uid: PortalEntry.Id,
      transitionOverride: ExitTransitionOverride? = null,
    ): PortalEntry<KeyT>?

    /**
     * [clearDisappearingEntries] can be set to `true`
     * if you want to clear removed entries that are still running their [ExitTransitionOverride].
     */
    public fun clear(
      clearDisappearingEntries: Boolean = false,
      transitionOverrideProvider: ((KeyT) -> ExitTransitionOverride?)? = null,
    )
  }
}

public val PortalManager<*>.isEmpty: Boolean get() = size == 0
public val PortalManager<*>.isNotEmpty: Boolean get() = size > 0
