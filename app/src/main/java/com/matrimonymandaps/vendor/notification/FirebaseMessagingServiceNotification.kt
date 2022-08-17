package com.matrimonymandaps.vendor.notification


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.matrimonymandaps.vendor.BuildConfig
import com.matrimonymandaps.vendor.R
import com.matrimonymandaps.vendor.activity.SplashActivity
import com.matrimonymandaps.vendor.helper.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class FirebaseMessagingServiceNotification : FirebaseMessagingService() {
    var sharePrefStorage: SharePrefStorage = SharePrefStorage()
    var context: Context? = null


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        context = this
        val notificationData: Map<String, String> = remoteMessage.getData()
        if (notificationData == null || notificationData.isEmpty()) {
            return
        }
        val title: String?
        val msgBody: String?
        val imageUrl: String?
        title = notificationData[Constants.NOTIFICATION_TITLE]
        msgBody = notificationData[Constants.NOTIFICATION_BODY]
        imageUrl = notificationData[Constants.NOTIFICATION_IMAGE_URL]
        val notificationType = notificationData[Constants.NOTIFICATION_TYPE]!!.toInt()

        if (isAnyStringNullOrEmpty(title, msgBody)) return

        when (notificationType) {
            Constants.NOTIFICATION_TAG_OPEN_APP -> {
                val intentOpenApp = Intent(context, SplashActivity::class.java)
                intentOpenApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                showNotification(context!!, intentOpenApp, title, msgBody, imageUrl!!)
            }
            Constants.NOTIFICATION_TAG_LOAD_URL -> try {
                val notificationUrl = notificationData[Constants.NOTIFICATION_DETAIL_URL]
                if (notificationUrl != null && notificationUrl != "") {
                    val intentLoadUrl = Intent(context, SplashActivity::class.java)
                    intentLoadUrl.putExtra(Constants.INTENT_LOAD_NOTIFICATION_URL, notificationUrl)
                    intentLoadUrl.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    showNotification(context!!, intentLoadUrl, title, msgBody, imageUrl!!)
                }
            } catch (ignored: Exception) {
            }
            Constants.NOTIFICATION_TAG_UPDATE_APP -> {
                try {
                    val newVersion = notificationData[Constants.NOTIFICATION_NEW_APPVERSION]!!.toInt()
                    val appVersion = getAppVersionCode(context!!)
                    sharePrefStorage.updateVersion = newVersion
                    if (newVersion >appVersion ) {
                        sendPlaystoreNotification(title, msgBody, getAppPackageName(context!!)!!, imageUrl!!)
                    }else{
                        Log.v("version_update","not updated")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            Constants.NOTIFICATION_TAG_GOTO_PLAYSTORE -> {
                val packageName = notificationData[Constants.NOTIFICATION_PLAYSTORE_PACKAGE_NAME]
                if (packageName != null && packageName.contains(".")) {
                    sendPlaystoreNotification(title, msgBody, packageName, imageUrl!!)
                }
            }
            else -> {
            }
        }
    }

    /***
     * Playstore notification
     */

    fun sendPlaystoreNotification(title: String?, msgBody: String?, playstorePackageName: String, imageUrl: String) {
        var intent: Intent
        var pendingIntent: PendingIntent?
        try {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$playstorePackageName"))
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        } catch (anfe: ActivityNotFoundException) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$playstorePackageName"))
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }
        showNotification(context!!, pendingIntent, title, msgBody, imageUrl)
    }

    fun showNotification(context: Context, intent: Intent?, title: String?, message: String?, notifcationImage: String) {
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        showNotification(context, pendingIntent, title, message, notifcationImage)
    }

    /**
     * Notification
     */
    fun showNotification(context: Context, pendingIntent: PendingIntent?,
                         title: String?, message: String?, notifcationImage: String) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_DEFAULT_ID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_DEFAULT_ID,
                    Constants.NOTIFICATION_CHANNEL_DEFAULT_TITLE, importance)
            mChannel.description = Constants.NOTIFICATION_CHANNEL_DEFAULT_DESCRIPTION
            notificationManager.createNotificationChannel(mChannel)
        } else {
            notificationBuilder.priority = Notification.PRIORITY_MAX
        }
        notificationBuilder.setContentTitle(title)
                .setContentText(message)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification_mm)
        if (notifcationImage.isEmpty()) {
            notificationBuilder
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setContentIntent(pendingIntent)
        } else {
            val bigPictureStyle = NotificationCompat.BigPictureStyle().bigPicture(getBitmapfromUrl(notifcationImage))
            bigPictureStyle.setSummaryText(message)
                    .setBigContentTitle(title)
            notificationBuilder
                    .setStyle(bigPictureStyle)
                    .setContentIntent(pendingIntent)
        }
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
        notificationManager.notify((Date().time / 1000L % Int.MAX_VALUE).toInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        PrefDelegate.init(this)
        if (BuildConfig.DEBUG) {
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_MMV_INSTALLED_USER_TEST)
        }
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_MMV_INSTALLED_USER)
        sharePrefStorage.fireabseToken = token
        showLog("token", sharePrefStorage.fireabseToken!!)
    }

    /*
     *To get a Bitmap image from the URL received
     * */
    fun getBitmapfromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: java.lang.Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            null
        }
    }

}




