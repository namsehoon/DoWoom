package com.example.dowoom.retrofit

import android.R
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.bumptech.glide.load.engine.Resource
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory


class SllAuth {


    fun getPinnedCertSslSocketFactory(context: Context): SSLSocketFactory? {
        try {
            val cf: CertificateFactory = CertificateFactory.getInstance("X.509")

            val caInput: InputStream = context.resources.openRawResource(context.resources.getIdentifier("mycer","raw",context.packageName))

            var ca: Certificate? = null
            try {
                ca = cf.generateCertificate(caInput)
                println("ca=" + (ca as X509Certificate?)?.subjectDN!!)
            } catch (e: CertificateException) {
                e.printStackTrace()
            } finally {
                caInput.close()
            }
            val keyStoreType: String = KeyStore.getDefaultType()
            val keyStore: KeyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
            if (ca == null) {
                return null
            }
            keyStore.setCertificateEntry("ca", ca)
            val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
            val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm)
            tmf.init(keyStore)
            val sslContext: SSLContext = SSLContext.getInstance("TLS")
            sslContext.init(null, tmf.trustManagers, null)
            return sslContext.socketFactory
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}

