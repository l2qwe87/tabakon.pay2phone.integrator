package ru.tabakon.integrator.http

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

class SimpleHttpClient(private val _uri : URI) {


    fun get(method : String, callback : (resp: String) -> Unit){
        //val mURL = URL("http://192.168.88.60:5148/PayToPhoneListener/GetlistenerStatus")
        val mURL = URL(_uri.toURL(), method)

        with(mURL.openConnection() as HttpURLConnection) {
            // optional default is GET
            this.requestMethod = "GET"
            this.connectTimeout = 1000;

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                callback(response.toString())
            }

        }
    }

    fun post(method: String, body: String, callback : (resp: String) -> Unit){
        val mURL = URL(_uri.toURL(), method)

        with(mURL.openConnection() as HttpURLConnection) {
            this.requestMethod = "POST"
            this.connectTimeout = 100;

            this.doOutput = true
            this.setRequestProperty("Content-Type", "application/json")
            this.setRequestProperty("Content-Length", body.length.toString())
            this.useCaches = false

            DataOutputStream(this.outputStream).use { it.writeBytes(body) }
            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()

                callback("$response")
            }
        }
    }
}