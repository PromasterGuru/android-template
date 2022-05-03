package protemplate.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import protemplate.util.AppConstants.ENCRYPTED_APP_PREFS


/**
 * Created by promasterguru on 03/05/2022.
 */
class AppPreferences(private val context: Context) : IPrefController {

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        ENCRYPTED_APP_PREFS,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val sharedPrefEditor = sharedPreferences.edit()

    override fun logout() {
        sharedPrefEditor.clear().apply()
    }

    override fun getString(key: String): String? {
        return sharedPreferences.getString(key, "")
    }

    override fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    override fun save(key: String, data: Any) {
        sharedPrefEditor.apply {
            when (data) {
                is Int -> putInt(key, data).apply()
                is Long -> putLong(key, data).apply()
                is Boolean -> putBoolean(key, data).apply()
                is String -> {
                    val jsonString = Gson().toJson(data)
                    putString(key, jsonString).apply()
                }
            }
        }
    }


    companion object {
        private var prefInstance: AppPreferences? = null

        @Synchronized
        fun initializeInstance(context: Context) {
            if (prefInstance == null) prefInstance = AppPreferences(context)
        }

        val instance: AppPreferences
            @Synchronized get() {
                checkNotNull(prefInstance) { "${AppPreferences::class.java} has not been initialized!" }
                return prefInstance!!
            }


    }
}
