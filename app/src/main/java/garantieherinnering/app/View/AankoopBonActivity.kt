package garantieherinnering.app.View

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import garantieherinnering.app.R

class AankoopBonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aankoopbon)
        supportActionBar?.title = "Aankoopbon"

        val imageUris = intent.getParcelableArrayListExtra<Uri>("IMAGE_URIS")
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        if (imageUris != null) {
            val adapter = ImagePagerAdapter(imageUris)
            viewPager.adapter = adapter
        }
    }
}