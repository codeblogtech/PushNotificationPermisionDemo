package com.technolyst.notification

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

//Create Singleton instance of DataStore Preference
val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "LocalStore")
class MyApplication : Application() {

}