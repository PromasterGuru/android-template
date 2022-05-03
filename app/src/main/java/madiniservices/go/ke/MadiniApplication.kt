package madiniservices.go.ke

import android.app.Application
import android.util.Log
import madiniservices.go.ke.data.prefs.AppPreferences
import madiniservices.go.ke.di.KoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

/**
 * Created by promasterguru on 02/05/2022.
 */
class MadiniApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppPreferences.initializeInstance(applicationContext)
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return String.format(
                        "Class:%s: Method: %s, Line: %s",
                        super.createStackElementTag(element),
                        element.methodName,
                        element.lineNumber
                    )
                }
            })
        } else {
            Timber.plant(ReleaseTree())
        }
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE)
            androidContext(this@MadiniApplication)
            modules(
                listOf(
                    KoinModules.prefModule,
                    KoinModules.retrofitModule,
                    KoinModules.apiModule
                )
            )
        }
    }
}

class ReleaseTree : @org.jetbrains.annotations.NotNull Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.ERROR || priority == Log.WARN) {
            // t?.let { Sentry.captureException(it) } TODO: Uncomment when sentry is configured for madini project
        }
    }
}
