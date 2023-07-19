package com.eygraber.portal

import com.eygraber.portal.PortalTransitionOverride.Alignment.All
import com.eygraber.portal.PortalTransitionOverride.Alignment.Horizontal
import com.eygraber.portal.PortalTransitionOverride.Alignment.Vertical
import kotlin.time.Duration

public interface PortalTransitionOverride {
  public sealed interface Alignment {
    public enum class Horizontal : Alignment {
      Start,
      Center,
      End
    }

    public enum class Vertical : Alignment {
      Top,
      Center,
      Bottom
    }

    public enum class All : Alignment {
      TopStart,
      TopCenter,
      TopEnd,
      CenterStart,
      Center,
      CenterEnd,
      BottomStart,
      BottomCenter,
      BottomEnd
    }
  }
}

public sealed interface EnterTransitionOverride : PortalTransitionOverride {
  public val duration: Duration? get() = null

  public data class ExpandIn(
    override val duration: Duration? = null,
    val expandFrom: All = All.BottomEnd
  ) : EnterTransitionOverride

  public data class ExpandInHorizontally(
    override val duration: Duration? = null,
    val expandFrom: Horizontal = Horizontal.End
  ) : EnterTransitionOverride

  public data class ExpandInVertically(
    override val duration: Duration? = null,
    val expandFrom: Vertical = Vertical.Bottom
  ) : EnterTransitionOverride

  public data class FadeIn(
    override val duration: Duration? = null,
    val initialAlpha: Float = 0F
  ) : EnterTransitionOverride

  public data object None : EnterTransitionOverride {
    override val duration: Duration? = null
  }

  public data class ScaleIn(
    override val duration: Duration? = null,
    val initialScale: Float = 0F
  ) : EnterTransitionOverride

  public data class SlideInFromLeft(
    override val duration: Duration? = null
  ) : EnterTransitionOverride

  public data class SlideInFromTop(
    override val duration: Duration? = null
  ) : EnterTransitionOverride

  public data class SlideInFromRight(
    override val duration: Duration? = null
  ) : EnterTransitionOverride

  public data class SlideInFromBottom(
    override val duration: Duration? = null
  ) : EnterTransitionOverride

  public fun interface Custom : EnterTransitionOverride {
    public fun custom(): Any
  }

  public companion object
}

public sealed interface ExitTransitionOverride : PortalTransitionOverride {
  public val duration: Duration? get() = null

  public data class FadeOut(
    override val duration: Duration? = null,
    val targetAlpha: Float = 0F
  ) : ExitTransitionOverride

  public data object None : ExitTransitionOverride {
    override val duration: Duration? = null
  }

  public data class ScaleOut(
    override val duration: Duration? = null,
    val targetScale: Float = 0F
  ) : ExitTransitionOverride

  public data class ShrinkOut(
    override val duration: Duration? = null,
    val shrinkTowards: All = All.BottomEnd
  ) : ExitTransitionOverride

  public data class ShrinkOutHorizontally(
    override val duration: Duration? = null,
    val shrinkTowards: Horizontal = Horizontal.End
  ) : ExitTransitionOverride

  public data class ShrinkOutVertically(
    override val duration: Duration? = null,
    val shrinkTowards: Vertical = Vertical.Bottom
  ) : ExitTransitionOverride

  public data class SlideOutToLeft(
    override val duration: Duration? = null
  ) : ExitTransitionOverride

  public data class SlideOutToTop(
    override val duration: Duration? = null
  ) : ExitTransitionOverride

  public data class SlideOutToRight(
    override val duration: Duration? = null
  ) : ExitTransitionOverride

  public data class SlideOutToBottom(
    override val duration: Duration? = null
  ) : ExitTransitionOverride

  public fun interface Custom : ExitTransitionOverride {
    public fun custom(): Any
  }

  public companion object
}
