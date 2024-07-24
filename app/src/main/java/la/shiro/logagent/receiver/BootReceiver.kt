package la.shiro.logagent.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import la.shiro.logagent.LogAgentApplication
import la.shiro.logagent.utils.Logger

/**
 * Author: Wang RuiLong
 * Date: 2024/07/23 16:58
 * Description:
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Logger.d("onReceive: ACTION_BOOT_COMPLETED")

            Handler(Looper.getMainLooper()).postDelayed(
                { LogAgentApplication.getInstance().scheduleNextHeartBeat() }, 1000L * 15L
            )
        }
    }
}