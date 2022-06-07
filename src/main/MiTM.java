package main;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class MiTM extends X509ExtendedTrustManager implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {

    public static void trustAllHttpsCertificates() {
        try {
            javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
            javax.net.ssl.TrustManager tm = new MiTM();
            trustAllCerts[0] = tm;
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            SSLContext sc2 = SSLContext.getInstance("TLS");
            sc2.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc2.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    @SuppressWarnings("unused")
    public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
        return true;
    }

    @SuppressWarnings("unused")
    public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
        return true;
    }

    @Override
    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
            throws java.security.cert.CertificateException {
        return;
    }

    @Override
    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
            throws java.security.cert.CertificateException {
        return;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

    }
}
