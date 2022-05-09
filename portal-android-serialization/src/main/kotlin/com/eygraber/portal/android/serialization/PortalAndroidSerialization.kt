package com.eygraber.portal.android.serialization

import android.os.Bundle
import com.eygraber.portal.ParentPortal
import com.eygraber.portal.SaveablePortal
import com.eygraber.portal.traverseChildren

interface ParcelablePortal {
  fun saveState(bundle: Bundle)
}

fun ParentPortal.onSaveState(bundle: Bundle?) {
  if(this is SaveablePortal) {
    saveState()
  }

  if(bundle != null && this is ParcelablePortal) {
    saveState(bundle)
  }

  traverseChildren(
    onPortal = { portal ->
      if(portal is SaveablePortal) {
        portal.saveState()
      }

      if(bundle != null && portal is ParcelablePortal) {
        portal.saveState(bundle)
      }
    }
  )
}
