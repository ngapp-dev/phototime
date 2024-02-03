package com.ngapps.phototime.sync.status

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.ngapps.phototime.sync.initializers.SYNC_TOPIC
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementation of [SyncSubscriber] that subscribes to the FCM [SYNC_TOPIC]
 */
class FirebaseSyncSubscriber @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging,
) : SyncSubscriber {
    override suspend fun subscribe() {
        firebaseMessaging
            .subscribeToTopic(SYNC_TOPIC)
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.e("firebaseMessaging", msg)
            }
            .await()
    }
}
