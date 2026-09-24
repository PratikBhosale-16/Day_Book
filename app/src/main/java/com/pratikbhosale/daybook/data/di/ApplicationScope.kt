package com.pratikbhosale.daybook.data.di

import javax.inject.Qualifier

/**
 * Qualifies the application-scoped [kotlinx.coroutines.CoroutineScope] provided by
 * [DatabaseModule]. Using a qualifier avoids ambiguity if other scoped coroutines
 * are ever added to the graph.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
