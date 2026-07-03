package io.hammerhead.climberextension.extension

/**
 * Single shared "no data" placeholder for every metric cell, in both Field 1 and Field 2
 * (CLAUDE.md v2: "single shared constant/function, referenced by all cells regardless of
 * which field they're in").
 *
 * Covers three cases uniformly:
 * - A climb metric missing its required context (no active route for Avg Climb Grade; no
 *   climb detected for Distance/Elev to Top - CLAUDE.md's route-dependency notes treat
 *   both as this same fallback, including the unconfirmed no-route-and-no-climb case).
 * - A native stream that isn't currently producing a value.
 * - A calculated metric whose formula isn't implemented yet.
 */
object MetricFallback {
    const val NOT_AVAILABLE = "n/a"
}
