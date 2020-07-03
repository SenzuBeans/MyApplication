package com.example.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.myapplication.R
import com.example.myapplication.databinding.W02httprequestBinding
import okhttp3.*
import okio.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class C02HttpRequest : AppCompatActivity() {

    private lateinit var oC_Binding: W02httprequestBinding
    private lateinit var oC_Request: Request
    private lateinit var oC_Client: OkHttpClient

    private var oC_Cal = Calendar.getInstance()
    private var oC_Url =
        "https://202.44.55.96:4433/ADAStatDose/Tester/API2PSMaster/V2/UserRol/Download?pdDate="


    override fun onCreate(poSavedInstanceState: Bundle?) {
        super.onCreate(poSavedInstanceState)
        oC_Binding = DataBindingUtil.setContentView(this, R.layout.w02httprequest)
        C_PGDxPlayground(poSavedInstanceState)
    }

    private fun C_PGDxPlayground(poSavedInstanceState: Bundle?) {
        var tCurrentTime = SimpleDateFormat("yyyy-MM-dd").format(oC_Cal.time)
        C_FDLxFatchData(tCurrentTime)
    }

    private fun C_FDLxFatchData(ptTime: String) {
        try {
            Log.d("TAGG", "C_FDLxFatchData: " + oC_Url + ptTime)
            oC_Request = Request.Builder()
                .url(oC_Url + ptTime)
                .header("X-Api-Key", "12345678-1111-1111-1111-123456789410")
                .build()

            oC_Client = C_HVFoHttpsVerify()
            oC_Client.newCall(oC_Request).enqueue(object : Callback {
                override fun onResponse(call: Call, oResponse: Response) {
                    Log.d("TAGG", "onResponse: " + oResponse.body.toString())
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

    private fun C_HVFoHttpsVerify(): OkHttpClient{
        val oNaiveTrustManager = object : X509TrustManager {
            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?){}
            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?){}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf<X509Certificate>()
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