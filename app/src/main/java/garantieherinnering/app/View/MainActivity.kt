package garantieherinnering.app.View

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import garantieherinnering.app.Data.DB.AankoopBonDB
import garantieherinnering.app.R
import garantieherinnering.app.Data.DB.AankoopDB
import garantieherinnering.app.Data.DB.GarantieHerinneringDB
import Model.AankoopModel
import android.view.LayoutInflater
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ItemAdapter
    private lateinit var items: MutableList<AankoopModel>
    private lateinit var filteredItems: MutableList<AankoopModel>

    override fun onResume() {
        super.onResume()
        populateListView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Overzicht"

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val searchField: EditText = findViewById(R.id.textField)

        recyclerView.isNestedScrollingEnabled = true

        items = mutableListOf()
        filteredItems = mutableListOf()
        adapter = ItemAdapter(this, filteredItems)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterItems(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        populateListView()

        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val child = recyclerView.findChildViewUnder(e.x, e.y)
                if (child != null) {
                    val viewHolder = recyclerView.getChildViewHolder(child) as ItemAdapter.ViewHolder
                    val position = viewHolder.adapterPosition
                    val item = filteredItems[position]
                    val intent = Intent(this@MainActivity, AankoopDetailsActivity::class.java).apply {
                        putExtra("aankoopID", item.aankoopID)
                    }
                    startActivity(intent)
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child = recyclerView.findChildViewUnder(e.x, e.y)
                if (child != null) {
                    val viewHolder = recyclerView.getChildViewHolder(child) as ItemAdapter.ViewHolder
                    val position = viewHolder.adapterPosition
                    showDeleteDialog(position)
                }
            }
        })

        recyclerView.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            v.performClick()
            true
        }
    }

    private fun showDeleteDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Verwijder aankoop")
        builder.setMessage("Weet je zeker dat je deze aankoop wilt verwijderen?")
        builder.setPositiveButton("Ja") { dialog, _ ->
            val aankoopDB = AankoopDB(this)
            val aankoopBonDB = AankoopBonDB(this)
            val garantieHerinneringDB = GarantieHerinneringDB(this)
            val aankoop = filteredItems[position]
            aankoopDB.deleteAankoop(aankoop.aankoopID)
            aankoopBonDB.deleteAankoopBon(aankoop.aankoopID)
            garantieHerinneringDB.deleteGarantieHerinnering(aankoop.aankoopID)
            items.remove(aankoop)
            filteredItems.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, filteredItems.size)
            dialog.dismiss()
        }
        builder.setNegativeButton("Nee") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun filterItems(query: String) {
        val lowerCaseQuery = query.lowercase()
        filteredItems.clear()
        filteredItems.addAll(items.filter {
            it.productNaam.lowercase().startsWith(lowerCaseQuery) || it.categorie.lowercase().startsWith(lowerCaseQuery)
        })
        adapter.notifyDataSetChanged()
    }

    private fun populateListView() {
        val aankoopDB = AankoopDB(this)
        val aankoopList = aankoopDB.getAllAankopen()

        // Sort items by remaining days of warranty
        val today = Calendar.getInstance().time
        val sortedList = aankoopList.sortedBy { item ->
            val eindDatum = Calendar.getInstance().apply {
                time = item.aankoopDatum
                add(Calendar.DAY_OF_YEAR, item.dagenGarantie)
            }.time
            ((eindDatum.time - today.time) / (1000 * 60 * 60 * 24)).toInt()
        }

        items.clear()
        items.addAll(sortedList)
        filteredItems.clear()
        filteredItems.addAll(items)
        adapter.notifyDataSetChanged()
    }

    fun createNewAankoop(view: View) {
        val intent = Intent(this, NieuweAankoopActivity::class.java)
        startActivity(intent)
    }

    private class ItemAdapter(private val context: MainActivity, private val items: List<AankoopModel>) :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        private val inflater: LayoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.item_aankoop, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.text1.text = item.productNaam
            holder.text2.text = item.categorie

            // Calculate remaining days of warranty
            val today = Calendar.getInstance().time
            val eindDatum = Calendar.getInstance().apply {
                time = item.aankoopDatum
                add(Calendar.DAY_OF_YEAR, item.dagenGarantie)
            }.time
            val remainingDays = ((eindDatum.time - today.time) / (1000 * 60 * 60 * 24)).toInt()

            holder.text3.text = String.format("%d dagen garantie", remainingDays)

            // Set OnClickListener to open AankoopDetailsActivity
            holder.itemView.setOnClickListener {
                val intent = Intent(context, AankoopDetailsActivity::class.java).apply {
                    putExtra("aankoopID", item.aankoopID)
                }
                context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val text1: TextView = view.findViewById(R.id.text1)
            val text2: TextView = view.findViewById(R.id.text2)
            val text3: TextView = view.findViewById(R.id.text3)
        }
    }
}