package com.system37.gecko.modules

import android.app.Application
import android.content.Context
import com.system37.gecko.downloads.DownloadService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import mozilla.components.browser.engine.gecko.GeckoEngine
import mozilla.components.concept.engine.Engine
import javax.inject.Singleton
import dagger.hilt.android.components.ApplicationComponent
import mozilla.components.browser.engine.gecko.fetch.GeckoViewFetchClient
import mozilla.components.browser.session.SessionManager
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.concept.fetch.Client
import mozilla.components.feature.downloads.DownloadMiddleware
import mozilla.components.feature.downloads.DownloadsUseCases
import org.mozilla.geckoview.GeckoView

@Module
@InstallIn(ApplicationComponent::class)
class CoreComponentsModule {
    @Provides
    @Singleton
    fun providesEngine(application: Application): Engine {
        return GeckoEngine(application)
    }
    @Provides
    @Singleton
    fun provideStore(application: Application):BrowserStore{
        return BrowserStore(middleware = listOf(DownloadMiddleware(application.applicationContext, DownloadService::class.java)))
    }

    @Provides
    @Singleton
    fun providesSessionManager(engine : Engine, store : BrowserStore): SessionManager{
        return SessionManager(engine, store)
    }

    @Provides
    @Singleton
    fun providesClient(application: Application): Client {
        return GeckoViewFetchClient(application)
    }

}