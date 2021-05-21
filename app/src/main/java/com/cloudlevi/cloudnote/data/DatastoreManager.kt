package com.cloudlevi.cloudnote.data

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import com.cloudlevi.cloudnote.HOME_TYPE_GRIDVIEW
import com.cloudlevi.cloudnote.HOME_TYPE_LISTVIEW
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatastoreManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.createDataStore("user_settings")

    suspend fun setHomeViewPreference(viewType: Int) =
        dataStore.edit { preferences -> preferences[PreferencesKeys.HOME_VIEW_PREFERENCE] = viewType}

    suspend fun getHomeViewPreference(): Int =
        dataStore.data.first()[PreferencesKeys.HOME_VIEW_PREFERENCE] ?: HOME_TYPE_LISTVIEW
}

private object PreferencesKeys {
    val HOME_VIEW_PREFERENCE = preferencesKey<Int>("home_view_preference")
}