package madiniservices.go.ke

import android.app.Application
import android.util.Log
import org.koin.android.BuildConfig
import timber.log.Timber

/**
 * Created by promasterguru on 02/05/2022.
 */
class MadiniApplication : Application() {
    override fun onCreate() {
        super.onCreate()
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
    }
}

class ReleaseTree : @org.jetbrains.annotations.NotNull Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        if (priority == Log.ERROR || priority == Log.WARN) {
            // throwable?.let { Sentry.captureException(it) } TODO: Uncomment when sentry is configured for madini project
        }
    }
}
