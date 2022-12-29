package de.r.gregat.graphhoppercoretest.screens.main

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.graphhopper.GraphHopper
import com.graphhopper.config.CHProfile
import com.graphhopper.config.Profile
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
}