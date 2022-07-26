package com.technolyst.notification

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
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
                    Home(intent,this)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    intent: Intent,
    activity: MainActivity) {

    //Let's Print FCM token in LogCat
    //Read FCM from DataStore<Preference>
    // And show on screen.

    // in Composable function we have used below method to get
    // object of context.
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

    // Create two mutable state for remember radio button state.

    val topicAllSelected = remember { mutableStateOf(false) }
    val topicNewsSelected = remember { mutableStateOf(false) }





    Scaffold(topBar = { SmallTopAppBar(title = { Text(text = "Push Notification.") }) }) {
        Column(modifier = Modifier.padding(it)) {

            Text(text = "Topic messaging on Android ")
            // in this video i will create two topics one is ALL and second one is News
            // and we will register it one by one and send push notification via topics
            // first on Screen we will create two radio button for both two options.

            Row(modifier = Modifier.fillMaxWidth()) {

                // set mutableState on selected property.
                RadioButton(selected = topicAllSelected.value, onClick = {
                    topicAllSelected.value = !topicAllSelected.value
                    //Subscribe  the client app to topic
                    // Now Add add complete listener that listener will help
                    // that topic which we have subscribe it successful or failed.


                    if (topicAllSelected.value) {
                        //if it is true then subscribe it ALL topic.
                        Firebase.messaging.subscribeToTopic("ALL").addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "All topic subscribe successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "All topic subscribe failed.",
                                    Toast.LENGTH_LONG
                                ).show()
                                // if it failed then we have revert back state of radio button to its perious state.
                                // means unselected.
                                topicAllSelected.value = false
                            }
                        }
                    } else {
                        // if it false then unsubscribe it.
                        // like above we have add listener too for below method.
                        Firebase.messaging.unsubscribeFromTopic("ALL")
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "All topic unsubscribe successfully",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "All topic unsubscribe failed.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    topicAllSelected.value = true

                                }
                            }
                    }
                })

                // Fix Alignment for this text field.
                Text(
                    text = "Topic ALL", textAlign = TextAlign.Center,
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )

            }

            // Now we code like same as we do for All topics and listener too.

            Row(modifier = Modifier.fillMaxWidth()) {
                // set mutableState on selected property.
                RadioButton(selected = topicNewsSelected.value, onClick = {
                    topicNewsSelected.value = !topicNewsSelected.value
                    // Now when user select News topic then we have do same
                    //as we have do with ALL topic selection.

                    if (topicNewsSelected.value) {
                        Firebase.messaging.subscribeToTopic("News").addOnCompleteListener { task ->
                            // update toast message.
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "News topic subscribe successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "News topic subscribe failed.",
                                    Toast.LENGTH_LONG
                                ).show()
                                // if it failed then we have revert back state of radio button to its perious state.
                                // means unselected.
                                topicNewsSelected.value = false
                            }
                        }
                    } else {
                        Firebase.messaging.unsubscribeFromTopic("News")
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "News topic unsubscribe successfully",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "News News unsubscribe failed.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    topicNewsSelected.value = true

                                }
                            }
                    }

                    // lets run this code and check ui.
                    // You can download the source code from github link in video description.


                })

                // Now fix it same as we do for above text field.
                Text(
                    text = "Topic News",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )

            }



            Spacer(modifier = Modifier.height(200.dp))

            Text(text = "Notification Title: ${notificationTitle.value}")
            Text(text = "Notification Body : ${notificationBody.value}")

        }
    }
}


