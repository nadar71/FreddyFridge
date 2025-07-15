package eu.indiewalkabout.fridgemanager.core.reminder.withworkmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import javax.inject.Inject

// Custom factory to create the FoodReminderWorker
class FoodReminderWorkerFactory @Inject constructor(
    private val repository: FridgeManagerRepository,
    @ApplicationContext private val context: Context,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            FoodReminderWorker::class.java.name ->
                FoodReminderWorker(context, workerParameters, repository)
            else ->
                null
        }
    }
}