package la.shiro.logagent.api

import la.shiro.logagent.data.HeartBeatDataClass
import la.shiro.logagent.data.ResponseDataClass
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Author: Wang RuiLong
 * Date: 2024/07/24 10:56
 * Description:
 */
interface ApiService {
    @POST("heartbeat")
    fun postHeartBeat(@Body data: HeartBeatDataClass): Call<ResponseDataClass>
}
