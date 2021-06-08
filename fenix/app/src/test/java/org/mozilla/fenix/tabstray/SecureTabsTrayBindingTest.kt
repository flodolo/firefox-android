/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.tabstray

import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import mozilla.components.support.test.libstate.ext.waitUntilIdle
import mozilla.components.support.test.rule.MainCoroutineRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mozilla.fenix.ext.removeSecure
import org.mozilla.fenix.ext.secure
import org.mozilla.fenix.utils.Settings

class SecureTabsTrayBindingTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutinesTestRule = MainCoroutineRule(TestCoroutineDispatcher())

    private val settings: Settings = mockk(relaxed = true)
    private val fragment: Fragment = mockk(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(AppCompatResources::class)
        every { AppCompatResources.getDrawable(any(), any()) } returns mockk(relaxed = true)
        every { fragment.secure() } just Runs
        every { fragment.removeSecure() } just Runs
    }

    @Test
    fun `WHEN tab selected page switches to private THEN set fragment to secure`() {
        val tabsTrayStore = TabsTrayStore(TabsTrayState())
        val secureTabsTrayBinding = SecureTabsTrayBinding(
            store = tabsTrayStore,
            settings = settings,
            fragment = fragment
        )

        secureTabsTrayBinding.start()
        tabsTrayStore.dispatch(TabsTrayAction.PageSelected(Page.positionToPage(Page.PrivateTabs.ordinal)))
        tabsTrayStore.waitUntilIdle()

        verify { fragment.secure() }
    }

    @Test
    fun `GIVEN not in private mode WHEN tab selected page switches to normal tabs from private THEN set fragment to un-secure`() {
        every { settings.lastKnownMode.isPrivate } returns false
        val tabsTrayStore = TabsTrayStore(TabsTrayState())
        val secureTabsTrayBinding = SecureTabsTrayBinding(
            store = tabsTrayStore,
            settings = settings,
            fragment = fragment
        )

        secureTabsTrayBinding.start()
        tabsTrayStore.dispatch(TabsTrayAction.PageSelected(Page.positionToPage(Page.NormalTabs.ordinal)))
        tabsTrayStore.waitUntilIdle()

        verify { fragment.removeSecure() }
    }

    @Test
    fun `GIVEN private mode WHEN tab selected page switches to normal tabs from private THEN do nothing`() {
        every { settings.lastKnownMode.isPrivate } returns true
        val tabsTrayStore = TabsTrayStore(TabsTrayState())
        val secureTabsTrayBinding = SecureTabsTrayBinding(
            store = tabsTrayStore,
            settings = settings,
            fragment = fragment
        )

        secureTabsTrayBinding.start()
        tabsTrayStore.dispatch(TabsTrayAction.PageSelected(Page.positionToPage(Page.NormalTabs.ordinal)))
        tabsTrayStore.waitUntilIdle()

        verify(exactly = 0) { fragment.removeSecure() }
    }
}
