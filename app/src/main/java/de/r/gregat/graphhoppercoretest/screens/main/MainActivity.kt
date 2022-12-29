package de.r.gregat.graphhoppercoretest.screens.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.graphhopper.GraphHopper
import de.r.gregat.graphhoppercoretest.R
import de.r.gregat.graphhoppercoretest.screens.common.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val graphHopper: GraphHopper = GraphHopper()

    }
}