package com.example.android.treasurefactory.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.treasurefactory.LootReporter
import com.example.android.treasurefactory.TreasureHacktoryApplication
import java.io.IOException

val WORKER_HOARD_ID_KEY = "HOARD_ID"
val WORKER_HOARD_FILE_PATH_KEY = "FILE_PATH"
val WORKER_HOARD_FILE_NAME_KEY = "FILE_NAME"
val WORKER_PAGE_WIDTH_KEY = "PAGE_WIDTH"

val WORKER_DEFAULT_FILE_PATH = "" //TODO get a default filepath

class TxtWorker(val appContext: Context, workerParameters: WorkerParameters)
    : CoroutineWorker(appContext, workerParameters) {

    //TODO need to find a way to get repositiory from ViewModel on worker build. maybe just
    // un-private in signature

    val repository = (appContext.applicationContext as TreasureHacktoryApplication).repository

    override suspend fun doWork(): Result {

        try {

            val filename = inputData.getString(WORKER_HOARD_FILE_NAME_KEY)
            val filepath = inputData.getString(WORKER_HOARD_FILE_PATH_KEY)
            val hoardID = inputData.getInt(WORKER_HOARD_ID_KEY,0)
            val hoard = repository.getHoardOnce(hoardID)

            if (hoard != null) {

                Log.e("TxtWorker", "Hoard \"${hoard.name}\" with id $hoardID found.")

                val reporter = LootReporter(appContext, repository)



            } else {

                Log.e("TxtWorker", "Hoard with id $hoardID was not found.")
            }

            return Result.success()

        } catch (e: IOException) {
            e.printStackTrace()

            return Result.failure()
        }
    }
}