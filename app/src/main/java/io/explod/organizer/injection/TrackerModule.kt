package io.explod.organizer.injection

import dagger.Module
import dagger.Provides
import io.explod.organizer.BuildConfig
import io.explod.organizer.service.tracking.FabricTracker
import io.explod.organizer.service.tracking.LoggingTracker
import io.explod.organizer.service.tracking.MultiTracker
import io.explod.organizer.service.tracking.Tracker
import javax.inject.Singleton


@Module
class TrackerModule {

    @Provides
    @Singleton
    internal fun providesTracker(): Tracker {
        if (BuildConfig.DEBUG) {
            return LoggingTracker()
        } else {
            return MultiTracker(
                    FabricTracker(),
                    LoggingTracker()
            )
        }
    }

}

