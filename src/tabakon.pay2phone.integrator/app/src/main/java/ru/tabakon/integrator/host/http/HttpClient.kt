package ru.tabakon.integrator.host.http

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

class HttpClient(private val _uri : URI) : IHttpClient {
    override suspend fun get(method: String): IHttpResponse {
        return request("GET", method);
    }

    override suspend fun post(method: String, body: String):IHttpResponse {
        return request("POST", method, body);
    }

    private suspend fun request(requestMethod: String, method: String, body: String? = null):IHttpResponse{
        val mURL = URL(_uri.toURL(), method)

        with(withContext(Dispatchers.IO) {
            mURL.openConnection()
        } as HttpURLConnection) {
            this.requestMethod = requestMethod
            this.connectTimeout = 1000;

            if(requestMethod == "POST") {
                this.doOutput = true
                this.setRequestProperty("Content-Type", "application/json")
                this.setRequestProperty("Content-Length", body?.length.toString())
                this.useCaches = false
                DataOutputStream(this.outputStream).use { it.writeBytes(body) }
            }

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                return object:IHttpResponse {
                    override val body get() = response.toString()
                };
            }
        }
    }
}