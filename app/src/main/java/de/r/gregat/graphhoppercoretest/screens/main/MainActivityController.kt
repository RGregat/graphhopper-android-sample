package de.r.gregat.graphhoppercoretest.screens.main

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity

class MainActivityController(val fragmentActivity: FragmentActivity): MainActivityMvcView.EventListener {
    override fun selectPbf() {

    }

    val PICK_PBF_FILE = 2

    fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        fragmentActivity.startActivityForResult(intent, PICK_PBF_FILE)
    }
}