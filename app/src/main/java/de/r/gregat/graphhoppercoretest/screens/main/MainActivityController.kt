package de.r.gregat.graphhoppercoretest.screens.main

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import de.r.gregat.graphhoppercoretest.utils.io.FileSelectionEntryPoint
import de.r.gregat.graphhoppercoretest.utils.io.SelectFileParams
import de.r.gregat.graphhoppercoretest.utils.io.StorageAccessFrameworkInteractor
import java.io.FileDescriptor

class MainActivityController(
    private val fragmentActivity: FragmentActivity
) : DefaultLifecycleObserver,
    MainActivityMvcView.EventListener,
    FileSelectionEntryPoint {

    lateinit var viewMvc: MainActivityMvcView

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

    }

    override fun selectPbf() {
        onSelectFileClick(SelectFileParams("application/octet-stream"))
    }

}