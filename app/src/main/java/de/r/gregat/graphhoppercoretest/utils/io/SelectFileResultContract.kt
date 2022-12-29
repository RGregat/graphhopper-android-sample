package de.r.gregat.graphhoppercoretest.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

data class SelectFileParams(
    val fileMimeType: String
)

class SelectFileResultContract: ActivityResultContract<SelectFileParams, Uri?>() {
    override fun createIntent(context: Context, data: SelectFileParams): Intent {
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setTypeAndNormalize(data.fileMimeType)
        }
    }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? = when (resultCode) {
            Activity.RESULT_OK -> intent?.data
            else -> null
        }
}