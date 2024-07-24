package la.shiro.logagent.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import la.shiro.logagent.LogAgentApplication
import la.shiro.logagent.utils.Logger

/**
 * Author: Wang RuiLong
 * Date: 2024/07/23 16:47
 * Description:
 */
class HeartBeatReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Logger.d("HeartBeatReceiver triggered")
        LogAgentApplication.getInstance().postHeartBeatData()
        LogAgentApplication.getInstance().scheduleNextHeartBeat()
    }
}
