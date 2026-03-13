package com.wlrus

/**
 * WLRUS Built-in Subscription System
 *
 * This file manages default subscriptions that ship with the app.
 * To add/remove default subscriptions, simply edit the DEFAULT_BUILTIN_SUBS list below.
 * Each entry is a BuiltinSub(name, url, autoUpdate).
 *
 * HOW TO USE:
 *   - Add your subscription URL here before building
 *   - Set autoUpdate = true to refresh on launch
 *   - These will be pre-installed for new users
 *
 * HOW TO REMOVE BUTTONS / SIMPLIFY UI:
 *   - See com.wlrus.ui.MainActivity and look for // [SIMPLE_MODE] comments
 *   - Set SIMPLE_MODE = true in this file to hide advanced buttons automatically
 */
object BuiltinSubscriptions {

    // ─── SIMPLE MODE ──────────────────────────────────────────────────────────
    // When true: hides QR scan, routing settings, logcat, tasker buttons.
    // Great for beginner-focused builds. Toggle here, no other changes needed.
    const val SIMPLE_MODE = false

    // ─── DEFAULT SUBSCRIPTIONS ────────────────────────────────────────────────
    // Edit this list to add your pre-installed subscriptions.
    val DEFAULT_BUILTIN_SUBS: List<BuiltinSub> = listOf(
        // Example (uncomment and fill in):
        // BuiltinSub(
        //     name = "WLRUS Default",
        //     url  = "https://your-subscription-url.com/sub",
        //     autoUpdate = true
        // ),
    )

    // ─── HELPER ───────────────────────────────────────────────────────────────
    data class BuiltinSub(
        val name: String,
        val url: String,
        val autoUpdate: Boolean = true,
        val remarks: String = "WLRUS default subscription",
        val enabled: Boolean = true,
    )

    /**
     * Returns true if any builtin subscriptions are configured.
     */
    fun hasBuiltins(): Boolean = DEFAULT_BUILTIN_SUBS.isNotEmpty()

    // Alias used by SettingsManager — maps to DEFAULT_BUILTIN_SUBS
    val BUILTIN_SUBSCRIPTIONS: List<BuiltinSub> get() = DEFAULT_BUILTIN_SUBS

    // Used by SettingsManager to decide whether to overwrite existing subs
    const val FORCE_UPDATE_EXISTING = false

    /**
     * Buttons / features to hide in SIMPLE_MODE.
     * Referenced by UI activities to conditionally show/hide views.
     */
    object SimpleMode {
        val HIDE_BUTTONS = if (SIMPLE_MODE) setOf(
            "btn_scan",          // QR code scanner button
            "btn_routing",       // Routing settings
            "btn_logcat",        // Log viewer
            "btn_tasker",        // Tasker integration
            "btn_backup",        // Backup/restore
            "btn_custom_config", // Custom JSON config editor
        ) else emptySet<String>()

        fun shouldHide(buttonId: String): Boolean = buttonId in HIDE_BUTTONS
    }
}
