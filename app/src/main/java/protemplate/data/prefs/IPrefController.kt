package protemplate.data.prefs

/**
 * Created by promasterguru on 03/05/2022.
 */
interface IPrefController {
    fun logout()
    fun getString(key: String): String?
    fun getBoolean(key: String): Boolean
    fun save(key: String, data: Any)
}
