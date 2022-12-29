package de.r.gregat.graphhoppercoretest.screens.main

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.graphhopper.GraphHopper
import de.r.gregat.graphhoppercoretest.utils.BackgroundThreadHelper
import de.r.gregat.graphhoppercoretest.utils.UiThreadHelper
import de.r.gregat.graphhoppercoretest.utils.io.FileSelectionEntryPoint
import de.r.gregat.graphhoppercoretest.utils.io.SelectFileParams
import de.r.gregat.graphhoppercoretest.utils.io.StorageAccessFrameworkInteractor
import java.io.FileDescriptor
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class MainActivityController(
    private val fragmentActivity: FragmentActivity,
    private val backgroundThreadHelper: BackgroundThreadHelper,
    private val uiThreadHelper: UiThreadHelper
) : DefaultLifecycleObserver,
    MainActivityMvcView.EventListener,
    FileSelectionEntryPoint {

    lateinit var viewMvc: MainActivityMvcView
    lateinit var graphHopper: GraphHopper

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


    fun onSelectFileClick(selectFileParams: SelectFileParams) =
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


    fun createGraphhopperInstance() {
        graphHopper = GraphHopper()

    }
}