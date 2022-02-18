package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import java.lang.IllegalArgumentException

private const val TAG = "BlurWorker"

class BlurWorker(private val context: Context, params: WorkerParameters): Worker(context,params) {

    override fun doWork(): Result {
        val appContext = applicationContext
        makeStatusNotification("blurring", appContext)
        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        return try {
            if(TextUtils.isEmpty(resourceUri)){
                Log.e(TAG,"invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }
            val resolver = appContext.contentResolver
            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri))
            )
            val blurPicture = blurBitmap(picture, appContext)
            val blurPictureUri =  writeBitmapToFile(context, blurPicture)

            makeStatusNotification("output is $blurPictureUri",appContext)
            Result.success(workDataOf(KEY_IMAGE_URI to blurPictureUri.toString()))
        } catch (a :Throwable) {
            Log.e(TAG, "Error applying blur")
            Result.failure()
        }
    }
}