package com.system37.gecko.downloads

import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.concept.fetch.Client
import mozilla.components.feature.downloads.AbstractFetchDownloadService
import javax.inject.Inject

class DownloadService : AbstractFetchDownloadService() {
    @Inject override lateinit var store: BrowserStore
    @Inject override lateinit var httpClient: Client

    override fun onCreate() {
        super.onCreate()
    }
}