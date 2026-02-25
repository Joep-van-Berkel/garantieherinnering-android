package garantieherinnering.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import garantieherinnering.app.View.AankoopDetailsActivity

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "garantieherinnering_channel"
        val channelName = "Garantie Herinnering"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val aankoopID = intent.getIntExtra("aankoopID", -1)
        val productNaam = intent.getStringExtra("productNaam") ?: "Product"
        val garantieEinddatum = intent.getStringExtra("garantieEinddatum") ?: "datum onbekend"

        val notificationIntent = Intent(context, AankoopDetailsActivity::class.java).apply {
            putExtra("aankoopID", aankoopID)
        }
        val pendingIntent = PendingIntent.getActivity(context, aankoopID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.aankoopbon)
            .setContentTitle("Garantieherinnering")
            .setContentText("Garantie voor $productNaam verloopt op $garantieEinddatum.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(aankoopID, notification)
    }
}