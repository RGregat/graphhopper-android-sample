package de.r.gregat.graphhoppercoretest.screens.main

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.graphhopper.GHRequest
import com.graphhopper.GHResponse
import com.graphhopper.GraphHopper
import com.graphhopper.config.CHProfile
import com.graphhopper.config.LMProfile
import com.graphhopper.config.Profile
import com.graphhopper.json.Statement
import com.graphhopper.routing.weighting.custom.CustomProfile
import com.graphhopper.util.CustomModel
import com.graphhopper.util.InstructionList
import com.graphhopper.util.Translation
import com.graphhopper.util.shapes.GHPoint
import de.r.gregat.graphhoppercoretest.utils.BackgroundThreadHelper
import de.r.gregat.graphhoppercoretest.utils.UiThreadHelper
import de.r.gregat.graphhoppercoretest.utils.io.FileSelectionEntryPoint
import de.r.gregat.graphhoppercoretest.utils.io.SelectFileParams
import de.r.gregat.graphhoppercoretest.utils.io.StorageAccessFrameworkInteractor
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.stream.Collectors
import kotlin.io.path.absolutePathString


class MainActivityController(
    private val fragmentActivity: FragmentActivity,
    private val backgroundThreadHelper: BackgroundThreadHelper,
    private val uiThreadHelper: UiThreadHelper
) : DefaultLifecycleObserver,
    MainActivityMvcView.EventListener,
    FileSelectionEntryPoint {

    private lateinit var viewMvc: MainActivityMvcView
    private lateinit var graphHopper: GraphHopper

    fun bindViewMvc(view: MainActivityMvcView) {
        this.viewMvc = view
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        viewMvc.registerListener(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        viewMvc.unregisterListener(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        viewMvc.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        viewMvc.onPause()
    }

    override val fileSelectionOwner: FragmentActivity
        get() = fragmentActivity

    private val fileSelectionInteractor: StorageAccessFrameworkInteractor =
        StorageAccessFrameworkInteractor(this)


    private fun onSelectFileClick(selectFileParams: SelectFileParams) =
        fileSelectionInteractor.beginSelectingFile(selectFileParams)

    override fun onFileSelected(fileDescriptor: FileDescriptor?) {
        viewMvc.startCopyProcess()

        backgroundThreadHelper.post {
            val fileInputStream = FileInputStream(fileDescriptor)

            val externalAppStorageRoot = fragmentActivity.getExternalFilesDir(null)

            val path = Paths.get(externalAppStorageRoot?.path, "osm.pbf")

            Files.copy(fileInputStream, path, StandardCopyOption.REPLACE_EXISTING);
            fileInputStream.close();

            uiThreadHelper.post {
                viewMvc.copyProcessDone()
            }
        }
    }

    override fun selectPbf() {
        onSelectFileClick(SelectFileParams("application/octet-stream"))
    }


    override fun createGraphhopperInstance() {
        viewMvc.startCreateGraphhopperInstanceProcess()

        backgroundThreadHelper.post {
            try {
                graphHopper = GraphHopper()

                val externalAppStorageRoot = fragmentActivity.getExternalFilesDir(null)
                val graphopperCacheFolder = File(externalAppStorageRoot, "graphhopper")
                val path = Paths.get(externalAppStorageRoot?.path, "osm.pbf")

                graphHopper.osmFile = path.absolutePathString();
                // specify where to store graphhopper files
                graphHopper.graphHopperLocation = graphopperCacheFolder.absolutePath

                // see docs/core/profiles.md to learn more about profiles
                graphHopper.setProfiles(
                    Profile("car").setVehicle("car").setWeighting("fastest").setTurnCosts(false)
                )

                // this enables speed mode for the profile we called car
                graphHopper.chPreparationHandler.setCHProfiles(CHProfile("car"))

                // now this can take minutes if it imports or a few seconds for loading of course this is dependent on the area you import
                graphHopper.importOrLoad()
            } catch (_: Exception) {

            } finally {
                uiThreadHelper.post {
                    viewMvc.createGraphhopperInstanceDone()
                }
            }
        }

    }

    override fun startRouting() {
        viewMvc.startRoutingProcess()

        backgroundThreadHelper.post {

            try {
                val req = GHRequest(
                    52.506532501639114,
                    13.416267775403348,
                    52.544940065357245,
                    13.354310290455304
                )
                    .setProfile("car")
                    .setLocale(Locale.GERMANY)

                val rsp: GHResponse = graphHopper.route(req)

                // handle errors

                // handle errors
                if (rsp.hasErrors()) throw RuntimeException(rsp.errors.toString())

                // use the best path, see the GHResponse class for more possibilities.

                // use the best path, see the GHResponse class for more possibilities.
                val path = rsp.best

                // points, distance in meters and time in millis of the full path

                // points, distance in meters and time in millis of the full path
                val pointList = path.points
                val distance = path.distance
                val timeInMs = path.time

                uiThreadHelper.post {
                    viewMvc.setRoutingResult(distance, timeInMs)
                    viewMvc.setGeoPoints(pointList)
                }


                val tr: Translation = graphHopper.translationMap.getWithFallBack(Locale.UK)
                val il: InstructionList = path.instructions
                // iterate over all turn instructions
                // iterate over all turn instructions
                val processInstructionList = il
                    .stream()
                    .map { "distance " + it.getDistance() + " for instruction: " + it.getTurnDescription(tr) }
                    .collect(Collectors.toList())
                viewMvc.setInstructionList(processInstructionList)
                for (instruction in il) {
                    System.out.println("distance " + instruction.getDistance() + " for instruction: " + instruction.getTurnDescription(tr));
                }
            } catch (_: java.lang.RuntimeException) {

            } finally {
                uiThreadHelper.post {
                    viewMvc.startRoutingProcessDone()
                }
            }
        }
    }
}