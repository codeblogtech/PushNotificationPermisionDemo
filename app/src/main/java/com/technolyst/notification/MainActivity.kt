package com.technolyst.notification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.stringPreferencesKey
import com.technolyst.notification.ui.theme.PushNotificationPermisionDemoTheme
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {

            PushNotificationPermisionDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home(intent)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(intent: Intent) {

    //Let's Print FCM token in LogCat
    //Read FCM from DataStore<Preference>
    // And show on screen.

    val context = LocalContext.current
    val gckTokenKey = stringPreferencesKey("gcm_token")

    val fcmToken = flow<String> {

        context.dataStore.data.map {
            it[gckTokenKey]
        }.collect(collector = {
            if (it != null) {
                Log.d("FCM", it)
                this.emit(it)
            }
        })

    }.collectAsState(initial = "")


    // Read notification payload when click on notification

    var notificationTitle = remember {
        mutableStateOf(
            if (intent.hasExtra("title")) {
                intent.getStringExtra("title")
            } else {
                ""
            }
        )
    }


    var notificationBody = remember {
        mutableStateOf(
            if (intent.hasExtra("body")) {
                intent.getStringExtra("body")
            } else {
                ""
            }
        )
    }


    Scaffold(topBar = { SmallTopAppBar(title = { Text(text = "Push Notification.") }) }) {
        Column(modifier = Modifier.padding(it)) {

            Text(text = "FCM Token")
            Text(text = fcmToken.value)


            Text(text = "Notification Title: ${notificationTitle.value}")
            Text(text = "Notification Body : ${notificationBody.value}")

        }
    }
}

