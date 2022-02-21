package com.example.background.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.OUTPUT_PATH
import java.io.File

private const val TAG = "CleanUpWorker"

class CleanUpWorker(ctx: Context, workerParameters: WorkerParameters) : Worker(ctx, workerParameters) {

    override fun doWork(): Result {

        makeStatusNotification("Cleaning up", applicationContext)
        sleep()

        return try {
            val outputDirectory = File(applicationContext.filesDir, OUTPUT_PATH)
            if(outputDirectory.exists()){
                val entries = outputDirectory.listFiles()
                if(entries != null) {
                    for(entry in entries){
                        val name = entry.name
                        if(name.isNotEmpty() && name.endsWith(".png")){
                            val deleted = entry.delete()
                            Log.i(TAG, "Deleted $name - $deleted")
                        }
                    }
                }
            }
            Result.success()
        } catch (e : Throwable) {
            e.printStackTrace()
            Result.failure()
        }
    }
}