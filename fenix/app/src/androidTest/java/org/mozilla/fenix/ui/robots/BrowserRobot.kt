/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

@file:Suppress("TooManyFunctions")

package org.mozilla.fenix.ui.robots

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.BundleMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.By.text
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.Until.hasObject
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertTrue
import org.mozilla.fenix.R
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.helpers.Constants.LongClickDuration
import org.mozilla.fenix.helpers.TestAssetHelper
import org.mozilla.fenix.helpers.TestAssetHelper.waitingTime
import org.mozilla.fenix.helpers.TestAssetHelper.waitingTimeShort
import org.mozilla.fenix.helpers.click
import org.mozilla.fenix.helpers.ext.waitNotNull

class BrowserRobot {

    fun verifyBrowserScreen() {
        onView(ViewMatchers.withResourceName("browserLayout"))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    fun verifyCurrentPrivateSession(context: Context) {
        val session = context.components.core.sessionManager.selectedSession
        assertTrue("Current session is private", session?.private!!)
    }

    fun verifyUrl(url: String) {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(
            Until.findObject(By.res("org.mozilla.fenix.debug:id/mozac_browser_toolbar_url_view")),
            waitingTime
        )
        TestAssetHelper.waitingTime
        onView(withId(R.id.mozac_browser_toolbar_url_view))
            .check(matches(withText(containsString(url.replace("http://", "")))))
    }

    fun verifyHelpUrl() {
        verifyUrl("support.mozilla.org/")
    }

    fun verifyWhatsNewURL() {
        verifyUrl("support.mozilla.org/")
    }

    fun verifyRateOnGooglePlayURL() {
        verifyUrl("play.google.com/store/apps/details?id=org.mozilla.fenix")
    }

    /* Asserts that the text within DOM element with ID="testContent" has the given text, i.e.
    *  document.querySelector('#testContent').innerText == expectedText
    */
    fun verifyPageContent(expectedText: String) {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(Until.findObject(By.textContains(expectedText)), waitingTime)
    }

    fun verifyTabCounter(expectedText: String) {
        onView(withId(R.id.counter_text))
            .check((matches(withText(containsString(expectedText)))))
    }

    fun verifySnackBarText(expectedText: String) {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(Until.findObject(text(expectedText)), waitingTime)

        onView(withText(expectedText)).check(
            matches(isCompletelyDisplayed())
        )
    }

    fun verifyLinkContextMenuItems(containsURL: Uri) {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(
            Until.findObject(By.textContains(containsURL.toString())),
            waitingTime
        )
        mDevice.waitNotNull(
            Until.findObject(text("Open link in new tab")),
            waitingTime
        )
        mDevice.waitNotNull(
            Until.findObject(text("Open link in private tab")),
            waitingTime
        )
        mDevice.waitNotNull(Until.findObject(text("Copy link")), waitingTime)
        mDevice.waitNotNull(Until.findObject(text("Share link")), waitingTime)
    }

    fun verifyLinkImageContextMenuItems(containsURL: Uri) {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(Until.findObject(By.textContains(containsURL.toString())))
        mDevice.waitNotNull(
            Until.findObject(text("Open link in new tab")), waitingTime
        )
        mDevice.waitNotNull(
            Until.findObject(text("Open link in private tab")), waitingTime
        )
        mDevice.waitNotNull(Until.findObject(text("Copy link")), waitingTime)
        mDevice.waitNotNull(Until.findObject(text("Share link")), waitingTime)
        mDevice.waitNotNull(
            Until.findObject(text("Open image in new tab")), waitingTime
        )
        mDevice.waitNotNull(Until.findObject(text("Save image")), waitingTime)
        mDevice.waitNotNull(
            Until.findObject(text("Copy image location")), waitingTime
        )
    }

    fun verifyNoLinkImageContextMenuItems(containsTitle: String) {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(Until.findObject(By.textContains(containsTitle)))
        mDevice.waitNotNull(
            Until.findObject(text("Open image in new tab")),
            waitingTime
        )
        mDevice.waitNotNull(Until.findObject(text("Save image")), waitingTime)
        mDevice.waitNotNull(
            Until.findObject(text("Copy image location")), waitingTime
        )
    }

    fun clickContextOpenLinkInNewTab() {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(
            Until.findObject(text("Open link in new tab")),
            waitingTime
        )

        val menuOpenInNewTab = mDevice.findObject(text("Open link in new tab"))
        menuOpenInNewTab.click()
    }

    fun clickContextOpenLinkInPrivateTab() {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(
            Until.findObject(text("Open link in private tab")),
            waitingTime
        )

        val menuOpenInPrivateTab = mDevice.findObject(text("Open link in private tab"))
        menuOpenInPrivateTab.click()
    }

    fun clickContextCopyLink() {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(Until.findObject(text("Copy link")), waitingTime)

        val menuCopyLink = mDevice.findObject(text("Copy link"))
        menuCopyLink.click()
    }

    fun clickContextShareLink(url: Uri) {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(Until.findObject(text("Share link")), waitingTime)

        val menuShareLink = mDevice.findObject(text("Share link"))
        menuShareLink.click()

        // verify share intent is launched and matched with associated passed in URL
        Intents.intended(
            allOf(
                IntentMatchers.hasAction(Intent.ACTION_CHOOSER),
                IntentMatchers.hasExtras(
                    allOf(
                        BundleMatchers.hasEntry(
                            Intent.EXTRA_INTENT,
                            allOf(
                                IntentMatchers.hasAction(Intent.ACTION_SEND),
                                IntentMatchers.hasType("text/plain"),
                                IntentMatchers.hasExtra(
                                    Intent.EXTRA_TEXT,
                                    url.toString()
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    fun clickContextCopyImageLocation() {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(
            Until.findObject(text("Copy image location")),
            waitingTime
        )

        val menuCopyImageLocation = mDevice.findObject(text("Copy image location"))
        menuCopyImageLocation.click()
    }

    fun clickContextOpenImageNewTab() {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(
            Until.findObject(text("Open image in new tab")),
            waitingTime
        )

        val menuOpenImageNewTab = mDevice.findObject(text("Open image in new tab"))
        menuOpenImageNewTab.click()
    }

    fun clickContextSaveImage() {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(Until.findObject(text("Save image")), waitingTime)

        val menuSaveImage = mDevice.findObject(text("Save image"))
        menuSaveImage.click()
    }

    fun createBookmark(url: Uri) {
        navigationToolbar {
        }.enterURLAndEnterToBrowser(url) {
        }.openThreeDotMenu {
            clickAddBookmarkButton()
        }
    }

    fun clickLinkMatchingText(expectedText: String) {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(Until.findObject(text(expectedText)), waitingTime)

        val element = mDevice.findObject(text(expectedText))
        element.click()
    }

    fun longClickMatchingText(expectedText: String) {
        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mDevice.waitNotNull(Until.findObject(text(expectedText)), waitingTime)

        val element = mDevice.findObject(text(expectedText))
        element.click(LongClickDuration.LONG_CLICK_DURATION)
    }

    fun snackBarButtonClick(expectedText: String) {
        onView(allOf(withId(R.id.snackbar_btn), withText(expectedText))).check(
            matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
        ).perform(ViewActions.click())
    }

    fun verifySaveLoginPromptIsShown() {
        mDevice.waitNotNull(Until.findObjects(text("test@example.com")), waitingTime)
        val submitButton = mDevice.findObject(By.res("submit"))
        submitButton.clickAndWait(Until.newWindow(), waitingTime)
        // Click save to save the login
        mDevice.waitNotNull(Until.findObjects(text("Save")))
    }

    fun saveLoginFromPrompt(optionToSaveLogin: String) {
        mDevice.findObject(text(optionToSaveLogin)).click()
    }

    fun clickMediaPlayerPlayButton() {
        mDevice.waitNotNull(
            hasObject(
                By
                    .clazz("android.widget.Button")
                    .textContains("Play")
            ),
            waitingTime
        )
        mediaPlayerPlayButton().click()
    }

    fun waitForPlaybackToStart() {
        mDevice.waitNotNull(
            hasObject(
                text("Media file is playing")
            ), waitingTimeShort
        )
    }

    fun verifyMediaIsPaused() {
        mDevice.waitNotNull(
            hasObject(
                text("Media file is paused")
            ), waitingTimeShort
        )

        mDevice.findObject(UiSelector().text("Media file is paused")).exists()
    }

    class Transition {
        private val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        private fun threeDotButton() = onView(
            allOf(
                ViewMatchers.withContentDescription(
                    "Menu"
                )
            )
        )

        fun openThreeDotMenu(interact: ThreeDotMenuMainRobot.() -> Unit): ThreeDotMenuMainRobot.Transition {
            mDevice.waitForIdle(waitingTime)
            threeDotButton().perform(ViewActions.click())

            ThreeDotMenuMainRobot().interact()
            return ThreeDotMenuMainRobot.Transition()
        }

        fun openNavigationToolbar(interact: NavigationToolbarRobot.() -> Unit): NavigationToolbarRobot.Transition {

            navURLBar().click()

            NavigationToolbarRobot().interact()
            return NavigationToolbarRobot.Transition()
        }

        fun openHomeScreen(interact: HomeScreenRobot.() -> Unit): HomeScreenRobot.Transition {
            mDevice.waitForIdle()

            tabsCounter().click()

            mDevice.waitNotNull(
                Until.findObject(By.res("org.mozilla.fenix.debug:id/header_text")),
                waitingTime
            )

            HomeScreenRobot().interact()
            return HomeScreenRobot.Transition()
        }

        fun openNotificationShade(interact: NotificationRobot.() -> Unit): NotificationRobot.Transition {
            mDevice.openNotification()

            NotificationRobot().interact()
            return NotificationRobot.Transition()
        }
    }
}

fun browserScreen(interact: BrowserRobot.() -> Unit): BrowserRobot.Transition {
    BrowserRobot().interact()
    return BrowserRobot.Transition()
}

private fun dismissOnboardingButton() = onView(withId(R.id.close_onboarding))

fun dismissTrackingOnboarding() {
    mDevice.wait(Until.findObject(By.res("close_onboarding")), waitingTime)
    dismissOnboardingButton().click()
}

fun navURLBar() = onView(withId(R.id.mozac_browser_toolbar_url_view))

private fun tabsCounter() = onView(withId(R.id.mozac_browser_toolbar_browser_actions))

private fun mediaPlayerPlayButton() =
    mDevice.findObject(
        By
            .clazz("android.widget.Button")
            .textContains("Play")
    )
