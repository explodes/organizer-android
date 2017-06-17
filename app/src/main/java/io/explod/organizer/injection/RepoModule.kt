package io.explod.organizer.injection

import dagger.Module
import dagger.Provides
import io.explod.organizer.service.repo.AppRepo
import io.explod.organizer.service.repo.AppRepoImpl
import io.explod.organizer.service.repo.AsyncAppRepo
import javax.inject.Singleton


@Module
class RepoModule {

    @Provides
    @Singleton
    internal fun providesAppRepo(): AppRepo = AppRepoImpl()

    @Provides
    @Singleton
    internal fun providesAsyncAppRepo(): AsyncAppRepo = AsyncAppRepo()

}

