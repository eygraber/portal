package com.eygraber.portal

private typealias TypedPortalManager = AbstractPortalManager<*, *, *, *, *>

public sealed interface PortalTraversal {
  public object Depth {
    public object Pre : PortalTraversal
    public object Post : PortalTraversal
  }

  public object Breadth : PortalTraversal
}

public fun ParentPortal.traverseChildren(
  onPortal: (Portal) -> Unit = {},
  onPortalManager: (TypedPortalManager) -> Unit = {},
  traversal: PortalTraversal = PortalTraversal.Depth.Pre
) {
  if(traversal == PortalTraversal.Breadth) {
    val initial = ArrayDeque<TypedPortalManager>().also {
      it.addAll(portalManagers)
    }
    traverseChildrenBFS(onPortal, onPortalManager, initial)
  }
  else {
    traverseChildrenDFS(onPortal, onPortalManager, traversal)
  }
}

private fun ParentPortal.traverseChildrenDFS(
  onPortal: (Portal) -> Unit,
  onPortalManager: (TypedPortalManager) -> Unit,
  traversal: PortalTraversal = PortalTraversal.Depth.Pre
) {
  for(portalManager in portalManagers) {
    if(traversal == PortalTraversal.Depth.Pre) {
      onPortalManager(portalManager)
    }

    portalManager.portals.forEach { (_, childPortal) ->
      if(childPortal is ParentPortal) {
        if(traversal == PortalTraversal.Depth.Pre) {
          onPortal(childPortal)
        }

        childPortal.traverseChildrenDFS(onPortal, onPortalManager, traversal)

        if(traversal == PortalTraversal.Depth.Post) {
          onPortal(childPortal)
        }
      }
      else {
        onPortal(childPortal)
      }
    }

    if(traversal == PortalTraversal.Depth.Post) {
      onPortalManager(portalManager)
    }
  }
}

private fun traverseChildrenBFS(
  onPortal: (Portal) -> Unit,
  onPortalManager: (TypedPortalManager) -> Unit,
  portalManagers: ArrayDeque<TypedPortalManager>
) {
  while(portalManagers.isNotEmpty()) {
    val portalManager = portalManagers.removeLast()

    onPortalManager(portalManager)

    for((_, portal) in portalManager.portals) {
      onPortal(portal)

      if(portal is ParentPortal) {
        portalManagers.addAll(portal.portalManagers)
      }
    }
  }
}
