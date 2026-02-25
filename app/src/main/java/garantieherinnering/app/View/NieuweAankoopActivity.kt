package garantieherinnering.app.View

import Model.AankoopBonModel
import Model.AankoopModel
import Model.GarantieHerinneringModel
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.view.View
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import garantieherinnering.app.Data.DB.AankoopBonDB
import garantieherinnering.app.Data.DB.AankoopDB
import garantieherinnering.app.Data.DB.GarantieHerinneringDB
import garantieherinnering.app.Enumeration.CategorieEnum
import garantieherinnering.app.Enumeration.DagenGarantieEnum
import garantieherinnering.app.NotificationReceiver
import garantieherinnering.app.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class NieuweAankoopActivity : AppCompatActivity() {

    private lateinit var imageContainer: LinearLayout
    private val imageUris = mutableListOf<Uri>()
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nieuwe_aankoop)
        checkPermissions()

        supportActionBar?.title = "Nieuwe aankoop"

        val inputAankoopdatum: EditText = findViewById(R.id.inputAankoopdatum)
        inputAankoopdatum.setOnClickListener {
            showDatePickerDialog(inputAankoopdatum)
        }

        val categorieSpinner: Spinner = findViewById(R.id.dropdownCategorie)
        val dagenGarantieSpinner: Spinner = findViewById(R.id.dropdownDagenGarantie)
        val inputExtra: EditText = findViewById(R.id.inputExtra)

        setupCategorieSpinner(categorieSpinner)
        setupDagenGarantieSpinner(dagenGarantieSpinner, inputExtra)

        imageContainer = findViewById(R.id.imageContainer)
        val buttonAanmaken: Button = findViewById(R.id.buttonAanmaken)
        buttonAanmaken.setOnClickListener {
            saveAankoop()
        }

        val buttonToevoegen: Button = findViewById(R.id.buttonFotoPicker)
        buttonToevoegen.setOnClickListener {
            showImageSourceDialog()
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(permissionsToRequest.toTypedArray(), 100)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted
            } else {
                Toast.makeText(this, "Camera and storage permissions are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editText.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun setupCategorieSpinner(categorieSpinner: Spinner) {
        val categorieList = listOf("Selecteer een categorie") + CategorieEnum.values().map { it.description }
        val categorieAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            categorieList
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextColor(android.graphics.Color.GRAY)
                } else {
                    view.setTextColor(android.graphics.Color.BLACK)
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextColor(android.graphics.Color.GRAY)
                } else {
                    view.setTextColor(android.graphics.Color.BLACK)
                }
                return view
            }
        }
        categorieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorieSpinner.adapter = categorieAdapter
    }

    private fun setupDagenGarantieSpinner(dagenGarantieSpinner: Spinner, inputExtra: EditText) {
        val dagenGarantieList = listOf("Selecteer een garantietermijn", "Aangepaste keuze") + DagenGarantieEnum.values().map { it.description }
        val dagenGarantieAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            dagenGarantieList
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextColor(android.graphics.Color.GRAY)
                } else {
                    view.setTextColor(android.graphics.Color.BLACK)
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextColor(android.graphics.Color.GRAY)
                } else {
                    view.setTextColor(android.graphics.Color.BLACK)
                }
                return view
            }
        }
        dagenGarantieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dagenGarantieSpinner.adapter = dagenGarantieAdapter

        dagenGarantieSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view != null) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    if (selectedItem == "Aangepaste keuze") {
                        inputExtra.visibility = View.VISIBLE
                    } else {
                        inputExtra.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                inputExtra.visibility = View.GONE
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Neem foto", "Kies uit gallerij")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecteer een optie")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> takePhoto()
                1 -> pickImageFromGallery()
            }
        }
        builder.show()
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: Exception) {
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "garantieherinnering.app.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (isFinishing || isDestroyed) {
            return
        }
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val file = File(currentPhotoPath)
                    if (file.exists()) {
                        val uri = Uri.fromFile(file)
                        imageUris.add(uri)
                        displayImage(uri)
                    }
                }
                REQUEST_IMAGE_PICK -> {
                    val uri = data?.data
                    if (uri != null) {
                        imageUris.add(uri)
                        displayImage(uri)
                    }
                }
            }
        }
    }

    private fun displayImage(uri: Uri) {
        val imageView = ImageView(this)
        imageView.setImageURI(uri)
        val layoutParams = LinearLayout.LayoutParams(
            200, // Set the width to 200 pixels
            200  // Set the height to 200 pixels
        )
        layoutParams.setMargins(8, 8, 8, 8)
        imageView.layoutParams = layoutParams
        imageView.setOnClickListener {
            val intent = Intent(this, AankoopBonActivity::class.java)
            intent.putParcelableArrayListExtra("IMAGE_URIS", ArrayList(imageUris))
            startActivity(intent)
        }
        imageContainer.addView(imageView)
    }

    private fun saveAankoop() {
        if (!validateFields()) {
            return
        }

        val productNaam: String = findViewById<EditText>(R.id.inputProduct).text.toString()
        val categorieSpinner: Spinner = findViewById(R.id.dropdownCategorie)
        val dagenGarantieSpinner: Spinner = findViewById(R.id.dropdownDagenGarantie)

        val categorie: String = categorieSpinner.selectedItem.toString()

        val dagenGarantie: Int = if (dagenGarantieSpinner.selectedItemPosition != 0) {
            if (dagenGarantieSpinner.selectedItem.toString() == "Aangepaste keuze") {
                findViewById<EditText>(R.id.inputExtra).text.toString().toInt()
            } else {
                DagenGarantieEnum.fromDescription(dagenGarantieSpinner.selectedItem.toString()).days
            }
        } else {
            throw IllegalArgumentException("Dagen garantie must be selected")
        }

        val aankoopBedrag: Double = findViewById<EditText>(R.id.inputAankoopbedrag).text.toString().toDouble()
        val aankoopDatum: Date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(findViewById<EditText>(R.id.inputAankoopdatum).text.toString())!!

        val aankoop = AankoopModel(
            aankoopID = 0,
            productNaam = productNaam,
            categorie = categorie,
            aankoopBedrag = aankoopBedrag,
            aankoopDatum = aankoopDatum,
            dagenGarantie = dagenGarantie
        )

        val context = this
        val aankoopDB = AankoopDB(context)
        val aankoopBonDB = AankoopBonDB(context)
        val garantieHerinneringDB = GarantieHerinneringDB(context)

        try {
            val aankoopID = aankoopDB.insertAankoop(aankoop)

            if (imageUris.isNotEmpty()) {
                for (imageUri in imageUris) {
                    val aankoopBon = AankoopBonModel(
                        aankoopID = aankoopID.toInt(),
                        imagePath = imageUri.toString()
                    )
                    aankoopBonDB.insertAankoopBon(aankoopBon)
                }
            }

            val calendar = Calendar.getInstance()
            calendar.time = aankoopDatum
            calendar.add(Calendar.DAY_OF_YEAR, dagenGarantie)
            val eindDatum = calendar.time

            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            val checkbox1: CheckBox = findViewById(R.id.checkbox1)
            val checkbox2: CheckBox = findViewById(R.id.checkbox2)
            val checkbox3: CheckBox = findViewById(R.id.checkbox3)

            if (checkbox1.isChecked) {
                calendar.time = eindDatum
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val herinneringsDatum1 = calendar.time
                val garantieHerinnering1 = GarantieHerinneringModel(
                    aankoopID = aankoopID.toInt(),
                    herinneringsDatum = herinneringsDatum1
                )
                garantieHerinneringDB.insertGarantieHerinnering(garantieHerinnering1)
                scheduleNotification(aankoopID.toInt(), herinneringsDatum1, productNaam, dateFormat.format(eindDatum))
            }

            if (checkbox2.isChecked) {
                calendar.time = eindDatum
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val herinneringsDatum2 = calendar.time
                val garantieHerinnering2 = GarantieHerinneringModel(
                    aankoopID = aankoopID.toInt(),
                    herinneringsDatum = herinneringsDatum2
                )
                garantieHerinneringDB.insertGarantieHerinnering(garantieHerinnering2)
                scheduleNotification(aankoopID.toInt(), herinneringsDatum2, productNaam, dateFormat.format(eindDatum))
            }

            if (checkbox3.isChecked) {
                calendar.time = eindDatum
                calendar.add(Calendar.DAY_OF_YEAR, -30)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val herinneringsDatum3 = calendar.time
                val garantieHerinnering3 = GarantieHerinneringModel(
                    aankoopID = aankoopID.toInt(),
                    herinneringsDatum = herinneringsDatum3
                )
                garantieHerinneringDB.insertGarantieHerinnering(garantieHerinnering3)
                scheduleNotification(aankoopID.toInt(), herinneringsDatum3, productNaam, dateFormat.format(eindDatum))
            }

            clearFields()
            Toast.makeText(this, "Aankoop succesvol aangemaakt!", Toast.LENGTH_SHORT).show()
        } finally {
            aankoopDB.close()
            aankoopBonDB.close()
            garantieHerinneringDB.close()
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

    private fun validateFields(): Boolean {
        val productNaam = findViewById<EditText>(R.id.inputProduct).text.toString()
        val aankoopBedrag = findViewById<EditText>(R.id.inputAankoopbedrag).text.toString()
        val aankoopDatum = findViewById<EditText>(R.id.inputAankoopdatum).text.toString()
        val dagenGarantieSpinner = findViewById<Spinner>(R.id.dropdownDagenGarantie)
        val inputExtra = findViewById<EditText>(R.id.inputExtra).text.toString()

        if (productNaam.isEmpty() || aankoopBedrag.isEmpty() || aankoopDatum.isEmpty() ||
            (dagenGarantieSpinner.selectedItemPosition == 0 && inputExtra.isEmpty())) {
            Toast.makeText(this, "Vul alle velden in", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun clearFields() {
        findViewById<EditText>(R.id.inputProduct).text.clear()
        findViewById<EditText>(R.id.inputAankoopbedrag).text.clear()
        findViewById<EditText>(R.id.inputAankoopdatum).text.clear()
        findViewById<EditText>(R.id.inputExtra).text.clear()
        findViewById<Spinner>(R.id.dropdownCategorie).setSelection(0)
        findViewById<Spinner>(R.id.dropdownDagenGarantie).setSelection(0)

        val imageContainer: LinearLayout = findViewById(R.id.imageContainer)
        imageContainer.removeAllViews()
        imageUris.clear()

        findViewById<CheckBox>(R.id.checkbox1).isChecked = false
        findViewById<CheckBox>(R.id.checkbox2).isChecked = false
        findViewById<CheckBox>(R.id.checkbox3).isChecked = false
    }
}