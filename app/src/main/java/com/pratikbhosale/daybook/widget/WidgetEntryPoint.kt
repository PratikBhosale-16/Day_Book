package com.pratikbhosale.daybook.widget

import android.content.Context
import com.pratikbhosale.daybook.domain.repository.DaybookRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Hilt entry point for the widget package.
 *
 * Glance components ([DaybookWidget], [ToggleTaskAction]) run outside the normal
 * Hilt component hierarchy — they cannot use @Inject constructor injection.
 * This entry point lets them retrieve the repository from the application-level
 * Hilt component via [EntryPointAccessors].
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun repository(): DaybookRepository
}

/** Convenience accessor from any [Context]. */
fun Context.widgetRepository(): DaybookRepository =
    EntryPointAccessors
        .fromApplication(applicationContext, WidgetEntryPoint::class.java)
        .repository()
