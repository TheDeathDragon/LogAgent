package la.shiro.logagent

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import la.shiro.logagent.data.HeartBeatDataClass
import la.shiro.logagent.data.ResponseDataClass
import la.shiro.logagent.receiver.HeartBeatReceiver
import la.shiro.logagent.utils.Logger
import la.shiro.logagent.utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Author: Wang RuiLong
 * Date: 2024/07/23 16:47
 * Description:
 */
class LogAgentApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: LogAgentApplication
        private lateinit var telephonyManager: TelephonyManager
        private lateinit var alarmManager: AlarmManager
        private lateinit var wifiManager: WifiManager
        private lateinit var heartBeatData: HeartBeatDataClass
        private lateinit var heartBeatReceiverIntent: Intent
        private lateinit var heartBeatPendingIntent: PendingIntent
        private const val INTERVAL: Long = 3000

        fun getInstance(): LogAgentApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        wifiManager = getSystemService(WIFI_SERVICE) as WifiManager

        heartBeatData = HeartBeatDataClass(
            Build.DEVICE, Build.MODEL, telephonyManager.imei ?: "123456789012345", Build.getSerial()
        )
        heartBeatReceiverIntent = Intent(
            this, HeartBeatReceiver::class.java
        )
        heartBeatPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            heartBeatReceiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun postHeartBeatData() {
        val hasNetwork = isNetworkConnected()
        if (!hasNetwork) {
            Logger.w("No network, skip postHeartBeat")
            return
        }
        val data = heartBeatData
        RetrofitInstance.api.postHeartBeat(data).enqueue(object : Callback<ResponseDataClass> {
            override fun onResponse(
                call: Call<ResponseDataClass>, response: Response<ResponseDataClass>
            ) {
                if (response.isSuccessful) {
                    Logger.d("postHeartBeat success")
                } else {
                    Logger.w("postHeartBeat failed")
                }
            }

            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                Logger.e("postHeartBeat failed: ${t.message}")
            }
        })
    }

    fun scheduleNextHeartBeat() {
        cancelAlarm()
        val triggerAtMillis = System.currentTimeMillis() + INTERVAL
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, heartBeatPendingIntent)
    }


    private fun cancelAlarm() {
        alarmManager.cancel(heartBeatPendingIntent)
    }

    private fun buildWifiConfig(ssid:String, password : String): WifiConfiguration {
        val wifiConfig = WifiConfiguration()
        wifiConfig.allowedAuthAlgorithms.clear()
        wifiConfig.allowedGroupCiphers.clear()
        wifiConfig.allowedKeyManagement.clear()
        wifiConfig.allowedPairwiseCiphers.clear()
        wifiConfig.allowedProtocols.clear()
        wifiConfig.SSID = "\"$ssid\""
        wifiConfig.preSharedKey = "\"$password\""
        wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
        // wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA2_PSK)

        // wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
        // wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
        // wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)

        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
        wifiConfig.status = WifiConfiguration.Status.ENABLED
        return wifiConfig
    }

    private fun isWifiExist(ssid: String): WifiConfiguration {
        val wifiList = wifiManager.configuredNetworks
        for (wifi in wifiList) {
            if (wifi.SSID == "\"$ssid\"") {
                return wifi
            }
        }
        return WifiConfiguration()
    }

    fun connectWifi(ssid: String, password: String) {
        // check wifi is enabled
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }

        // check wifi config is exist
        val wifiConfig = isWifiExist(ssid)
        if (wifiConfig.networkId == -1) {
            // add wifi config
            val newWifiConfig = buildWifiConfig(ssid, password)
            val networkId = wifiManager.addNetwork(newWifiConfig)
            wifiManager.enableNetwork(networkId, true)
            wifiManager.saveConfiguration()
        } else {
            wifiManager.enableNetwork(wifiConfig.networkId, true)
        }
    }
}