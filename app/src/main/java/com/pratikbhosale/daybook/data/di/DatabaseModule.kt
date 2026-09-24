package com.pratikbhosale.daybook.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.pratikbhosale.daybook.data.local.BucketDao
import com.pratikbhosale.daybook.data.local.DatabaseSeeder
import com.pratikbhosale.daybook.data.local.DaybookDatabase
import com.pratikbhosale.daybook.data.local.PageDao
import com.pratikbhosale.daybook.data.local.TaskDao
import com.pratikbhosale.daybook.data.repository.DaybookRepositoryImpl
import com.pratikbhosale.daybook.domain.repository.DaybookRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindRepository(impl: DaybookRepositoryImpl): DaybookRepository

    companion object {

        /**
         * Application-scoped coroutine scope used for the database seeder callback.
         * SupervisorJob ensures one failure doesn't cancel unrelated work.
         */
        @Provides
        @Singleton
        @ApplicationScope
        fun provideApplicationScope(): CoroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.IO)

        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context,
            @ApplicationScope applicationScope: CoroutineScope,
        ): DaybookDatabase {
            // Build the DB first, then wire the seeder via a lazy reference
            // so we can pass the DAO accessor without a circular dependency.
            lateinit var database: DaybookDatabase
            val seeder = DatabaseSeeder(
                coroutineScope = applicationScope,
                bucketDaoProvider = { database.bucketDao() },
            )
            database = Room.databaseBuilder(
                context,
                DaybookDatabase::class.java,
                DaybookDatabase.DATABASE_NAME,
            )
                .addCallback(seeder)
                .fallbackToDestructiveMigrationOnDowngrade(true)
                .build()
            return database
        }

        @Provides
        fun provideBucketDao(db: DaybookDatabase): BucketDao = db.bucketDao()

        @Provides
        fun providePageDao(db: DaybookDatabase): PageDao = db.pageDao()

        @Provides
        fun provideTaskDao(db: DaybookDatabase): TaskDao = db.taskDao()

        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            context.settingsDataStore
    }
}
