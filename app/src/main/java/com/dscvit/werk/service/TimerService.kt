package com.dscvit.werk.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dscvit.werk.R
import java.util.*
import kotlin.collections.HashMap

class TimerService : Service() {
    companion object {
        const val CHANNEL_ID = "Werk_Timer_Notifications"
        const val START = "Start"
        const val PAUSE = "Pause"
        const val DONE = "Done"
        const val GET_STATUS = "Get_Status"
        const val SET_ELAPSED_TIME = "Set_Elapsed_Time"
    }

    private val timerMap = HashMap<Int, Int>()
    private val timers = HashMap<Int, Timer>()
    private val taskNames = HashMap<Int, String>()
    private val isTimerRunning = HashMap<Int, Boolean>().withDefault { false }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("Timer", "On Bind Called")
        return null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val taskID = intent?.getIntExtra("TaskID", -1)!!
        val taskName = intent.getStringExtra("TaskName")!!

        taskNames[taskID] = taskName
        val elapsedTime = intent.getIntExtra("TaskElapsedTime", -1)

        when (intent.getStringExtra("Action")!!) {
            START -> startTimer(taskID)
            PAUSE -> pauseTimer(taskID)
            DONE -> finishTask(taskID)
            GET_STATUS -> sendStatus(taskID)
            SET_ELAPSED_TIME -> setElapsedTime(taskID, elapsedTime)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun setElapsedTime(taskID: Int, elapsedTime: Int) {
        if (elapsedTime != -1) {
            timerMap[taskID] = elapsedTime
            sendStatus(taskID)
        }
    }

    private fun startTimer(taskID: Int) {
        isTimerRunning[taskID] = true
        sendStatus(taskID)

        timers[taskID] = Timer()
        timers[taskID]!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val timerIntent = Intent()
                timerIntent.action = "TaskTimer$taskID"

                timerMap[taskID] = timerMap[taskID]!!.plus(1)

                showNotification(taskID, false)

                timerIntent.putExtra("TimeElapsed", timerMap[taskID]!!)
                sendBroadcast(timerIntent)
            }
        }, 0, 1000)

        startForeground(taskID, getNotification(taskID))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun pauseTimer(taskID: Int) {
        timers[taskID]!!.cancel()
        isTimerRunning[taskID] = false
        sendStatus(taskID)
        showNotification(taskID, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun finishTask(taskID: Int) {
        Log.d("Timer", "Done called")
        if (isTimerRunning[taskID] == true) {
            timers[taskID]!!.cancel()
            isTimerRunning[taskID] = false
            sendStatus(taskID)
        }
        showNotification(taskID, true)
    }

    private fun sendStatus(taskID: Int) {
        val statusIntent = Intent()
        statusIntent.action = "TaskStatus$taskID"
        statusIntent.putExtra("TaskStatus", isTimerRunning[taskID] ?: false)
        statusIntent.putExtra("TaskTimeElapsed", timerMap[taskID] ?: 0)
        sendBroadcast(statusIntent)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Werk Tasks",
                NotificationManager.IMPORTANCE_MIN
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotification(taskID: Int): Notification {
        val title = if (isTimerRunning[taskID] == true) {
            "${taskNames[taskID]} is in progress!"
        } else {
            "${taskNames[taskID]} is paused!"
        }

        val minutes: Int = timerMap[taskID]?.div(60) ?: 0
        val seconds: Int = timerMap[taskID]?.rem(60) ?: 0

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setContentTitle(title)
            .setContentText("Time you have been working on it: $minutes mins and $seconds secs")
            .setSmallIcon(R.drawable.logo)
            .build()
    }

    private fun getCompletedNotification(taskID: Int): Notification {
        val title = "${taskNames[taskID]} is done 🎉"

        val minutes: Int = timerMap[taskID]?.div(60) ?: 0
        val seconds: Int = timerMap[taskID]?.rem(60) ?: 0

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setContentTitle(title)
            .setContentText("Time you have been working on it: $minutes mins and $seconds secs")
            .setSmallIcon(R.drawable.logo)
            .build()
    }

    private fun showNotification(taskID: Int, isTaskSubmitted: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
            if (!isTaskSubmitted) {
                getSystemService(NotificationManager::class.java)?.notify(
                    taskID,
                    getNotification(taskID)
                )
            } else {
                getSystemService(NotificationManager::class.java)?.notify(
                    taskID,
                    getCompletedNotification(taskID)
                )
            }
        }
    }
}