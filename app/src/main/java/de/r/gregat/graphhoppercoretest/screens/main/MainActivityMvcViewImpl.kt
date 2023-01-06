package de.r.gregat.graphhoppercoretest.screens.main

import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import com.graphhopper.util.PointList
import de.r.gregat.graphhoppercoretest.R
import de.r.gregat.graphhoppercoretest.databinding.ActivityMainBinding
import de.r.gregat.graphhoppercoretest.screens.common.BaseObservableViewMvc
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainActivityMvcViewImpl(
    private val layoutInflater: LayoutInflater,
    private val container: ViewGroup?
) :
    BaseObservableViewMvc<MainActivityMvcView.EventListener>(), MainActivityMvcView {

    private val locationOverlay: MyLocationNewOverlay
    private val rotationGestureOverlay: RotationGestureOverlay

    private val binding: ActivityMainBinding = ActivityMainBinding
        .inflate(
            layoutInflater,
            container,
            false
        )


    init {
        Configuration.getInstance().load(binding.root.context, PreferenceManager.getDefaultSharedPreferences(binding.root.context))

        setRootView(binding.root)

        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.controller.setZoom(9.5)
        val startPoint = GeoPoint(52.0, 13.2)
        binding.map.controller.setCenter(startPoint)

        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(binding.root.context), binding.map)
        locationOverlay.enableMyLocation()
        binding.map.overlays.add(locationOverlay)

        rotationGestureOverlay = RotationGestureOverlay(binding.map)
        rotationGestureOverlay.isEnabled
        binding.map.setMultiTouchControls(true)
        binding.map.overlays.add(rotationGestureOverlay)
    }

    init {
        /*binding.tvStartLocation.text = "52.506532501639114, 13.416267775403348"
        binding.tvDestinationLocation.text = "52.544940065357245, 13.354310290455304"*/
    }

    init {
        binding.btnSelectPbf.setOnClickListener {
            getListeners().forEach {
                it.selectPbf()
            }
        }

        binding.btnCreateGraphhopperInstance.setOnClickListener {
            getListeners().forEach {
                it.createGraphhopperInstance()
            }
        }

        binding.btnStartRouting.setOnClickListener {
            getListeners().forEach {
                it.startRouting()
            }
        }
    }

    override fun startCopyProcess() {
        binding.tvFileCopyProcessAnnotation.text =
            "Copy selected PBF File to external App Storage..."
    }

    override fun copyProcessDone() {
        binding.tvFileCopyProcessAnnotation.text =
            "PBF File successfully copied to external App Storage."
    }

    override fun startCreateGraphhopperInstanceProcess() {
        binding.tvCreateGraphhopperInstanceAnnotation.text =
            "Creating a new Graphhopper instance..."
    }

    override fun createGraphhopperInstanceDone() {
        binding.tvCreateGraphhopperInstanceAnnotation.text =
            "Successfully created a new Graphhopper instance."
    }

    override fun startRoutingProcess() {
        binding.tvStartRoutingAnnotation.text =
            "Find for the given start and destination location a routing..."
    }

    override fun startRoutingProcessDone() {
        binding.tvStartRoutingAnnotation.text = "Successfully calculated a routing."
    }

    override fun setRoutingResult(distance: Double, time: Long) {
        /*binding.tvDistanceInM.text = String.format("%f m", distance)
        binding.tvTimeInMS.text = String.format("%d ms", time)*/
    }

    override fun setInstructionList(instructionList: List<String>) {
        val singleInstructionString = instructionList.joinToString {
            "$it\r\n"
        }

        /*binding.tvInstruction.text = singleInstructionString*/
    }

    override fun setGeoPoints(pointList: PointList) {
        val geoPointList: MutableList<GeoPoint> = mutableListOf()

        pointList.forEach {
            geoPointList.add(GeoPoint(it.lat, it.lon, it.ele))
        }

        val line = Polyline();   //see note below!
        line.setPoints(geoPointList);

        binding.map.overlays.add(line)
    }

    override fun onResume() {
        binding.map.onResume()
        locationOverlay.enableMyLocation()
    }

    override fun onPause() {
        binding.map.onPause()
        locationOverlay.disableMyLocation()
    }
}