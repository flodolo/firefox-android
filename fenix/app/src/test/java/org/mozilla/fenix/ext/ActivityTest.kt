/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.ext

import android.app.Activity
import android.view.View
import android.view.WindowManager
import mozilla.components.support.test.robolectric.testContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mozilla.fenix.browser.browsingmode.BrowsingMode
import org.mozilla.fenix.helpers.FenixRobolectricTestRunner
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf

@RunWith(FenixRobolectricTestRunner::class)
class ActivityTest {

    @Test
    fun testEnterImmersiveMode() {
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()
        val window = activity.window

        // Turn off Keep Screen on Flag if it is on
        if (shadowOf(window).getFlag(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)) window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Make sure that System UI flags are not set before the test
        val flags = arrayOf(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN, View.SYSTEM_UI_FLAG_HIDE_NAVIGATION, View.SYSTEM_UI_FLAG_FULLSCREEN, View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        if (flags.any { f -> (window.decorView.systemUiVisibility and f) == f }) {
            window.decorView.systemUiVisibility = 0
        }

        // Run
        activity.enterToImmersiveMode()

        // Test
        for (f in flags) assertEquals(f, window.decorView.systemUiVisibility and f)
        assertTrue(shadowOf(window).getFlag(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON))
    }

    @Test
    fun `testCheckAndUpdateScreenshotPermission adds flag in private mode when screenshots are not allowed `() {
        // given
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()
        val window = activity.window
        testContext.settings().lastKnownMode = BrowsingMode.Private
        testContext.settings().allowScreenshotsInPrivateMode = false

        // when
        activity.checkAndUpdateScreenshotPermission(activity.settings())

        // then
        assertTrue(shadowOf(window).getFlag(WindowManager.LayoutParams.FLAG_SECURE))
    }

    @Test
    fun `testCheckAndUpdateScreenshotPermission removes flag in private mode when screenshots are allowed `() {
        // given
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        testContext.settings().lastKnownMode = BrowsingMode.Private
        testContext.settings().allowScreenshotsInPrivateMode = true

        // when
        activity.checkAndUpdateScreenshotPermission(activity.settings())

        // then
        assertFalse(shadowOf(window).getFlag(WindowManager.LayoutParams.FLAG_SECURE))
    }

    @Test
    fun `testCheckAndUpdateScreenshotPermission removes flag in normal mode when screenshots are allowed `() {
        // given
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        testContext.settings().lastKnownMode = BrowsingMode.Normal
        testContext.settings().allowScreenshotsInPrivateMode = true

        // when
        activity.checkAndUpdateScreenshotPermission(activity.settings())

        // then
        assertFalse(shadowOf(window).getFlag(WindowManager.LayoutParams.FLAG_SECURE))
    }

    @Test
    fun `testCheckAndUpdateScreenshotPermission removes flag when in normal mode screenshots are not allowed `() {
        // given
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        testContext.settings().lastKnownMode = BrowsingMode.Normal
        testContext.settings().allowScreenshotsInPrivateMode = false

        // when
        activity.checkAndUpdateScreenshotPermission(activity.settings())

        // then
        assertFalse(shadowOf(window).getFlag(WindowManager.LayoutParams.FLAG_SECURE))
    }
}
