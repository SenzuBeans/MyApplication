package com.example.myapplication.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.W02httprequestBinding
import com.example.myapplication.model.CmlHttpAdapter
import com.example.myapplication.model.CmlJObj
import com.example.myapplication.model.CmlJUsrImg
import com.example.myapplication.model.CmlJUsrLng
import kotlinx.android.synthetic.main.w02httprequest.*
import okhttp3.*
import okio.IOException
import org.json.JSONObject
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.collections.ArrayList

class C02HttpRequest : AppCompatActivity() {

    private lateinit var oC_Binding: W02httprequestBinding
    private lateinit var oC_Request: Request
    private lateinit var oC_Client: OkHttpClient

    private var oC_Cal = Calendar.getInstance()
    private var tC_Url =
        "https://202.44.55.96:4433/ADAStatDose/Tester/API2PSMaster/V2/UserRol/Download?pdDate="
    private var aoC_JObjArray = ArrayList<CmlJObj>()
    private var aoC_JUserLng = ArrayList<CmlJUsrLng>()
    private var aoC_JUserImg = ArrayList<CmlJUsrImg>()

    override fun onCreate(poSavedInstanceState: Bundle?) {
        super.onCreate(poSavedInstanceState)
        oC_Binding = DataBindingUtil.setContentView(this, R.layout.w02httprequest)
        C_PGDxPlayground(poSavedInstanceState)
    }

    private fun C_PGDxPlayground(poSavedInstanceState: Bundle?) {
        var tCurrentTime = SimpleDateFormat("yyyy-MM-dd").format(oC_Cal.time)
        orv02Recycler.layoutManager = LinearLayoutManager(this)

        C_FDLxFatchData(tCurrentTime)

        ocm02Picker.setOnClickListener {

        }
        ocm02Request.setOnClickListener {
        }
    }

    private fun C_FDLxFatchData(ptTime: String) {
        try {
            Log.d("TAGG", "C_FDLxFatchData: " + tC_Url + ptTime)
            oC_Request = Request.Builder()
                .url(tC_Url + ptTime)
                .header("X-Api-Key", "12345678-1111-1111-1111-123456789410")
                .build()

            oC_Client = C_HVFoHttpsVerify()
            oC_Client.newCall(oC_Request).enqueue(object : Callback {

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call, oResponse: Response) {
                    val oJObj = JSONObject(oResponse.body!!.string()).getJSONObject("roItem")

                    val oJArrUserRoleLng = oJObj.getJSONArray("raUserRoleLng")
                    for (nPosition in 0..oJArrUserRoleLng!!.length() - 1) {
                        aoC_JUserLng.add(
                            CmlJUsrLng(
                                oJArrUserRoleLng.getJSONObject(nPosition).getString("rtRolCode"),
                                oJArrUserRoleLng.getJSONObject(nPosition).getString("rtRolName")
                            )
                        )
                    }

                    val oJArrUserRoleImg = oJObj.getJSONArray("raUserRoleImage")
                    for (nPosition in 0..oJArrUserRoleImg!!.length() - 1) {
                        val aTempImg = Base64.getDecoder()
                            .decode(oJArrUserRoleImg.getJSONObject(nPosition).getString("rtImgObj"))
                        aoC_JUserImg.add(
                            CmlJUsrImg(
                                oJArrUserRoleImg.getJSONObject(nPosition).getString("rtImgRefID"),
                                aTempImg
                            )
                        )
                    }

                    C_UPDxUpdateView()
                }

                override fun onFailure(call: Call, poE: java.io.IOException) {
                    Log.d("TAGG", "onFailure: " + poE.toString())
                }
            })

        } catch (poE: IOException) {
            Toast.makeText(this, poE.toString(), Toast.LENGTH_LONG).show()
            Log.d("TAGG", "C_FDLxFatchData: " + poE.toString())
        }
    }

    private fun C_UPDxUpdateView() {
        C_PRCxExecuteArray()
        Thread(Runnable {
            this.runOnUiThread {
                orv02Recycler.adapter = CmlHttpAdapter(aoC_JObjArray)
            }
        }).start()
    }

    private fun C_PRCxExecuteArray() {
        aoC_JUserLng.forEach { pItem ->
            aoC_JObjArray.add(
                CmlJObj(
                    pItem.tRolCode,
                    pItem.tRolName,
                    C_FIGaFindImg(pItem.tRolCode)
                )
            )
        }
    }

    private fun C_FIGaFindImg(ptRolCode: String): ByteArray {
        aoC_JUserImg.forEach { pItem ->
            if (pItem.tImgRefID.equals(ptRolCode))
                return pItem.aArrayImg
        }
        return ByteArray(0)
    }

    private fun C_HVFoHttpsVerify(): OkHttpClient {
        val oNaiveTrustManager = object : X509TrustManager {
            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
        val oInsecureSocketFactory = SSLContext.getInstance("SSL").apply {
            val aoTrustAllCerts = arrayOf<TrustManager>(oNaiveTrustManager)
            init(null, aoTrustAllCerts, SecureRandom())
        }.socketFactory


        return OkHttpClient.Builder()
            .sslSocketFactory(oInsecureSocketFactory, oNaiveTrustManager)
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .build()
    }
}