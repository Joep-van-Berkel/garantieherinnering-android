package garantieherinnering.app.View

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import garantieherinnering.app.Data.DB.AankoopBonDB
import garantieherinnering.app.Data.DB.AankoopDB
import garantieherinnering.app.Data.DB.GarantieHerinneringDB
import garantieherinnering.app.NotificationReceiver
import garantieherinnering.app.R
import java.text.SimpleDateFormat
import java.util.*

class AankoopDetailsActivity : AppCompatActivity() {

    private lateinit var productNaamData: TextView
    private lateinit var aankoopBedragData: TextView
    private lateinit var garantieEinddatumData: TextView
    private lateinit var resterendeDagenData: TextView
    private lateinit var viewPhotosButton: Button
    private lateinit var checkbox1Day: CheckBox
    private lateinit var checkbox1Week: CheckBox
    private lateinit var checkbox1Month: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aankoop_details)
        supportActionBar?.title = "Aankoop details"

        productNaamData = findViewById(R.id.productNaamData)
        aankoopBedragData = findViewById(R.id.aankoopBedragData)
        garantieEinddatumData = findViewById(R.id.garantieEinddatumData)
        resterendeDagenData = findViewById(R.id.resterendeDagenData)
        viewPhotosButton = findViewById(R.id.viewPhotosButton)
        checkbox1Day = findViewById(R.id.checkbox1Day)
        checkbox1Week = findViewById(R.id.checkbox1Week)
        checkbox1Month = findViewById(R.id.checkbox1Month)

        val aankoopID = intent.getIntExtra("aankoopID", -1)
        if (aankoopID != -1) {
            val aankoopDB = AankoopDB(this)
            val aankoop = aankoopDB.getAankoopById(aankoopID)

            aankoop?.let {
                productNaamData.text = it.productNaam
                aankoopBedragData.text = formatToCurrency(it.aankoopBedrag)

                val einddatum = Calendar.getInstance().apply {
                    time = it.aankoopDatum
                    add(Calendar.DAY_OF_YEAR, it.dagenGarantie)
                }.time

                val remainingDays = ((einddatum.time - Date().time) / (1000 * 60 * 60 * 24)).toInt()

                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                garantieEinddatumData.text = dateFormat.format(einddatum)
                resterendeDagenData.text = remainingDays.toString()

                val garantieHerinneringDB = GarantieHerinneringDB(this)
                val herinneringDates = garantieHerinneringDB.getHerinneringDatesByAankoopId(aankoopID)

// Calculate reminder dates
                val oneDayBefore = Calendar.getInstance().apply {
                    time = einddatum
                    add(Calendar.DAY_OF_YEAR, -1)
                    set(Calendar.HOUR_OF_DAY, 0) // Ensure time is set to midnight
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                val oneWeekBefore = Calendar.getInstance().apply {
                    time = einddatum
                    add(Calendar.DAY_OF_YEAR, -7)
                    set(Calendar.HOUR_OF_DAY, 0) // Ensure time is set to midnight
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                val oneMonthBefore = Calendar.getInstance().apply {
                    time = einddatum
                    add(Calendar.DAY_OF_YEAR, -30)
                    set(Calendar.HOUR_OF_DAY, 0) // Ensure time is set to midnight
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

// Check if the reminder dates exist in the database
                checkbox1Day.isChecked = herinneringDates.contains(oneDayBefore)
                checkbox1Week.isChecked = herinneringDates.contains(oneWeekBefore)
                checkbox1Month.isChecked = herinneringDates.contains(oneMonthBefore)

                checkbox1Day.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        garantieHerinneringDB.addHerinneringDate(aankoopID, oneDayBefore)
                        scheduleNotification(aankoopID, oneDayBefore, it.productNaam, dateFormat.format(einddatum))
                    } else {
                        garantieHerinneringDB.deleteHerinneringDate(aankoopID, oneDayBefore)
                        cancelNotification(aankoopID)
                    }
                }

                checkbox1Week.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        garantieHerinneringDB.addHerinneringDate(aankoopID, oneWeekBefore)
                        scheduleNotification(aankoopID, oneWeekBefore, it.productNaam, dateFormat.format(einddatum))
                    } else {
                        garantieHerinneringDB.deleteHerinneringDate(aankoopID, oneWeekBefore)
                        cancelNotification(aankoopID)
                    }
                }

                checkbox1Month.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        garantieHerinneringDB.addHerinneringDate(aankoopID, oneMonthBefore)
                        scheduleNotification(aankoopID, oneMonthBefore, it.productNaam, dateFormat.format(einddatum))
                    } else {
                        garantieHerinneringDB.deleteHerinneringDate(aankoopID, oneMonthBefore)
                        cancelNotification(aankoopID)
                    }
                }
            }

            val aankoopBonDB = AankoopBonDB(this)
            val imageUris = aankoopBonDB.getAankoopBonById(aankoopID).map { Uri.parse(it.imagePath) }

            viewPhotosButton.setOnClickListener {
                val intent = Intent(this, AankoopBonActivity::class.java)
                intent.putParcelableArrayListExtra("IMAGE_URIS", ArrayList(imageUris))
                startActivity(intent)
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(aankoopID: Int, herinneringsDatum: Date, productNaam: String, garantieEinddatum: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return
            }
        }

        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("aankoopID", aankoopID)
            putExtra("productNaam", productNaam)
            putExtra("garantieEinddatum", garantieEinddatum)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, aankoopID, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        try {
            // Use the exact timestamp from the herinneringsDatum
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, herinneringsDatum.time, pendingIntent)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun cancelNotification(aankoopID: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, aankoopID, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)
    }

    private fun formatToCurrency(amount: Double): String {
        return "€" + String.format(Locale.getDefault(), "%.2f", amount).replace('.', ',')
    }
}