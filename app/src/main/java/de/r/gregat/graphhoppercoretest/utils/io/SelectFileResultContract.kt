package de.r.gregat.graphhoppercoretest.utils.io

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract


class SelectFileResultContract : ActivityResultContract<SelectFileParams, Uri?>() {
    override fun createIntent(context: Context, input: SelectFileParams): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setTypeAndNormalize(input.fileMimeType)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? = when (resultCode) {
        Activity.RESULT_OK -> intent?.data
        else -> null
    }
}