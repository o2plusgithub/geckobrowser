package com.system37.gecko

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_main.*
import mozilla.components.browser.engine.gecko.GeckoEngine
import mozilla.components.browser.session.Session
import mozilla.components.browser.session.SessionManager
import mozilla.components.browser.state.action.ContentAction
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.concept.engine.Engine
import mozilla.components.concept.engine.EngineSession
import mozilla.components.concept.engine.EngineView
import mozilla.components.feature.downloads.DownloadsFeature
import mozilla.components.feature.downloads.DownloadsUseCases
import mozilla.components.feature.downloads.manager.FetchDownloadManager
import mozilla.components.feature.session.SessionFeature
import mozilla.components.feature.session.SessionUseCases
import mozilla.components.feature.session.FullScreenFeature
import mozilla.components.support.base.feature.ViewBoundFeatureWrapper
import mozilla.components.support.base.log.logger.Logger.Companion.debug
import org.mozilla.geckoview.WebExtension
import java.util.logging.Logger
import mozilla.components.support.base.log.logger.Logger.Companion.debug
import com.system37.gecko.downloads.DownloadService
import dagger.hilt.android.AndroidEntryPoint
import mozilla.components.browser.session.usecases.EngineSessionUseCases
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var engine: Engine
    @Inject lateinit var store: BrowserStore
    @Inject lateinit var sessionManager: SessionManager


    private lateinit var sessionFeature: SessionFeature
    private lateinit var toolbar: Toolbar
    private lateinit var downloadsFeature: DownloadsFeature
    private lateinit var feature: FullScreenFeature

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val sessionUseCases = SessionUseCases(sessionManager)
        val engineUseCases = EngineSessionUseCases(sessionManager)
        val downloadUseCases = DownloadsUseCases(store)


        sessionFeature = SessionFeature(store,
                sessionUseCases.goBack,
                engineUseCases,
                findViewById<View>(R.id.engineView) as EngineView
        )

        downloadsFeature = DownloadsFeature(
                applicationContext,
                store,
                downloadUseCases,
                onNeedToRequestPermissions = { permissions ->
                    requestPermissions(permissions, 1)
                },
                downloadManager = FetchDownloadManager(
                        applicationContext,
                        store,
                        DownloadService::class
                ),
                fragmentManager = supportFragmentManager
        )

        feature = FullScreenFeature(
                store,
                sessionUseCases,
                fullScreenChanged = ::fullScreenChanged
        )

        sessionManager.add(
                Session("www.google.com")
        )

        lifecycle.addObserver(downloadsFeature)
        lifecycle.addObserver(sessionFeature)
        lifecycle.addObserver(feature)

        //val engineSession = engine.createSession(private = true)
        //engineView.render(engineSession)
        //engineSession.loadUrl("https://www.google.com");
        //var observedUrl = ""
        //engineSession.register(object : EngineSession.Observer {
        //    override fun onLocationChange(url: String) {
        //        observedUrl = url;
        //        Log.d("tapas", url);
        //    }
        //    override fun onFullScreenChange(enabled: Boolean) {
        //        if (enabled){
        //            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        //            Log.d("tapas", "fullscreen");
        //        } else {
        //            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //            Log.d("tapas", "not fullscreen");
        //        }
        //    }
        //})

    }

    private fun fullScreenChanged(enabled: Boolean) {
        if (enabled){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            Log.d("tapas", "fullscreen");
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Log.d("tapas", "not fullscreen");
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array< String>,
        grantResults: IntArray
    ) {
        downloadsFeature.onPermissionsResult(permissions, grantResults)
    }


    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        if (name == EngineView::class.java.name) {
            return engine.createView(context, attrs).asView()
        }
        return super.onCreateView(name, context, attrs)
    }


}