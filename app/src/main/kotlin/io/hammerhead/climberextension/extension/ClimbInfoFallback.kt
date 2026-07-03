package io.hammerhead.climberextension.extension

/**
 * Shared placeholder text for "Climb Info" cells (CLAUDE.md: Field 1 behavior - route dependency).
 *
 * [NO_ROUTE] applies only to Avg Climb Grade, which is sourced from
 * `OnNavigationState.NavigatingRoute.Climb.grade` and is unavailable without an active
 * navigated route with climb data.
 *
 * [NO_CLIMB_DETECTED] covers Distance to Top / Elev to Top when neither a route nor an
 * ambient climb is in progress. That empty state isn't fully specced yet - single constant
 * kept separate so it's easy to adjust once it is.
 */
object ClimbInfoFallback {
    const val NO_ROUTE = "n/a"
    const val NO_CLIMB_DETECTED = "--"
}
