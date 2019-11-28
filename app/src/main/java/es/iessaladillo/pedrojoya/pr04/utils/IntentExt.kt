package es.iessaladillo.pedrojoya.pr04.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import es.iessaladillo.pedrojoya.pr04.data.entity.Task

fun isActivityAvailable(ctx: Context, intent: Intent): Boolean {
    val packageManager = ctx.applicationContext.packageManager
    val appList = packageManager.queryIntentActivities(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    )
    return appList.size > 0
}


fun sendTasks(tasks: MutableLiveData<List<Task>>): Intent {
    val listToText = arrayListOf<String>()
    var element  = ""
    var finalElement = ""
    tasks.value?.forEach {
        element += it.concept
        if(it.completed){
            element+=" Completado"
        }else{
            element+=" Pendiente"
        }
        listToText.add(element)
        element=""
    }
    listToText.forEach {
        finalElement+=it+"\n"
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_SUBJECT,"Tareas")
        putExtra(Intent.EXTRA_TEXT, finalElement)
        type = "text/plain"
    }
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;

    return intent

}
