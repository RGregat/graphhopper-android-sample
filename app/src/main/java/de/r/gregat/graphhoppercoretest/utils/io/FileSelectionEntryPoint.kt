package de.r.gregat.graphhoppercoretest.utils.io

import androidx.fragment.app.FragmentActivity
import java.io.FileDescriptor

interface FileSelectionEntryPoint {

    val fileSelectionOwner: FragmentActivity

    fun onFileSelected(fileDescriptor: FileDescriptor?)
}