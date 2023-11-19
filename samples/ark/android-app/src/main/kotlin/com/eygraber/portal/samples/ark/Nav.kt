package com.eygraber.portal.samples.ark

abstract class Nav {
  val name: String = requireNotNull(this::class.simpleName)
  open val isPrimary: Boolean = true
}
