package com.pratikbhosale.daybook.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * AppWidgetProvider for the Daybook widget.
 *
 * android:exported is intentionally NOT set to true here — the manifest entry
 * will handle that. This class does nothing beyond linking the receiver to the
 * widget implementation.
 */
class DaybookWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DaybookWidget()
}
