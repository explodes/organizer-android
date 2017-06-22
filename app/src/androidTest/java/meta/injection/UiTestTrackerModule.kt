package meta.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import io.explod.organizer.service.tracking.Tracker
import io.reactivex.Completable
import javax.inject.Singleton


@Module
class UiTestTrackerModule {

    @Provides
    @Singleton
    internal fun providesTracker(): Tracker = UiNoOpTracker()

}

class UiNoOpTracker : Tracker {

    override fun initialize(context: Context): Completable = Completable.complete()

    override fun event(action: String, properties: Map<String, Any>?) {
        //no-op
    }

    override fun recordException(level: Int, t: Throwable) {
        //no-op
    }

    override fun log(level: Int, tag: String, message: String, t: Throwable?) {
        //no-op
    }

}