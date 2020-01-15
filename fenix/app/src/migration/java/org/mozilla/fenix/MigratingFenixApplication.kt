/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.runBlocking
import mozilla.components.support.migration.FennecMigrator
import mozilla.components.support.migration.state.MigrationStore

/**
 * An application class which knows how to migrate Fennec data.
 */
class MigratingFenixApplication : FenixApplication() {
    val migrator by lazy {
        FennecMigrator.Builder(this, this.components.analytics.crashReporter)
            .migrateOpenTabs(this.components.core.sessionManager)
            .migrateHistory(this.components.core.historyStorage)
            .migrateBookmarks(this.components.core.bookmarksStorage)
            .migrateLogins(
                this.components.core.passwordsStorage.store,
                this.components.core.passwordsEncryptionKey
            )
            .migrateFxa(this.components.backgroundServices.accountManager)
            .migrateAddons(this.components.core.engine)
            .build()
    }

    val migrationStore by lazy { MigrationStore() }

    val migrationPushSubscriber by lazy {
        MigrationPushSubscriber(
            this,
            components.backgroundServices.pushService,
            migrationStore
        )
    }

    override fun setupInMainProcessOnly() {
        // These migrations need to run before regular initialization happens.
        migrateBlocking()

        // Fenix application initialization can happen now.
        super.setupInMainProcessOnly()

        // The rest of the migrations can happen now.
        migrationPushSubscriber.start()
        migrator.startMigrationIfNeeded(migrationStore, MigrationService::class.java)

        // Start migration UI
        val intent = Intent(this, MigrationProgressActivity::class.java)
        startActivity(intent)
    }

    private fun migrateBlocking() {
        val migrator = FennecMigrator.Builder(this, this.components.analytics.crashReporter)
            .migrateGecko()
            // Telemetry may have been disabled in Fennec, so we need to migrate Settings first
            // to correctly initialize telemetry.
            .migrateSettings()
            .build()

        runBlocking {
            migrator.migrateAsync().await()
        }
    }
}

fun Context.getMigratorFromApplication(): FennecMigrator {
    return (applicationContext as MigratingFenixApplication).migrator
}

fun Context.getMigrationStoreFromApplication(): MigrationStore {
    return (applicationContext as MigratingFenixApplication).migrationStore
}
