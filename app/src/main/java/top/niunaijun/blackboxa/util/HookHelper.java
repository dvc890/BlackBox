package top.niunaijun.blackboxa.util;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * VirtualApp
 *
 * <p>Description: </p>
 * <br>
 *
 * <p>Copyright: Copyright (c) 2020</p>
 *
 * @author wb-xkc612827@alibaba-inc.com
 * @version 1.0
 * 2020/11/23 10:27 AM
 * @date 2020/11/23
 */
public class HookHelper {

    public static void findAndHookMethodNotRun(
        final ClassLoader loader,
        final String clazz,
        final String methodname) {
        findAndHookMethod(loader, clazz, methodname, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(null);
            }
        });
    }

    public static void findAndhookConstructors(
        final ClassLoader loader,
        final String clazz,
        final XC_MethodHook hookback) {
        Class aClass = XposedHelpers.findClass(clazz, loader);
        for(Constructor constructor : aClass.getDeclaredConstructors()) {
            ArrayList<Object> objects = new ArrayList<>();
            if(constructor.getParameterTypes() != null) {
                for(Object object : constructor.getParameterTypes()) {
                    objects.add(object);
                }
            }
            objects.add(hookback);
            Log.e("Pipedvc", aClass.getName() +"->"+ constructor.getName() +":"+ constructor.getModifiers());
            XposedHelpers.findAndHookConstructor(aClass, objects.toArray());
        }
    }

    public static void findAndHookMethod(
        final ClassLoader loader,
        final String clazz,
        final String methodname,
        final XC_MethodHook hookback) {
        Class aClass = XposedHelpers.findClass(clazz, loader);
        for (Method method : aClass.getDeclaredMethods()) {
            if(Modifier.isAbstract(method.getModifiers())
                || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if(!TextUtils.equals(method.getName(), methodname)) {
                continue;
            }
            ArrayList<Object> objects = new ArrayList<>();
            if(method.getParameterTypes() != null) {
                for(Object object : method.getParameterTypes()) {
                    objects.add(object);
                }
            }
            objects.add(hookback);
            Log.e("Pipedvc", aClass.getName() +"->"+ method.getName() +":"+ method.getModifiers());
            XposedHelpers.findAndHookMethod(aClass, method.getName(), objects.toArray());
        }
    }

    public static void hookMethodNotRun(
        final ClassLoader loader,
        final String clazz,
        final String method,
        final Object... params) {
        ArrayList<Object> objects = new ArrayList<>();
        if(params != null) {
            for(Object object : objects) {
                objects.add(object);
            }
        }
        objects.add(new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(null);
            }
        });
        XposedHelpers.findAndHookMethod(clazz, loader, method, objects.toArray());
    }

    public static void hookMethodforResult(
        final ClassLoader loader,
        final String clazz,
        final String method,
        final Object theReturn,
        final Object... params) {
        ArrayList<Object> objects = new ArrayList<>();
        if(params != null) {
            for(Object object : objects) {
                objects.add(object);
            }
        }
        objects.add(new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(theReturn);
            }
        });
        XposedHelpers.findAndHookMethod(clazz, loader, method, objects.toArray());
    }

    public static void hookClassAllMethods(
        final ClassLoader loader,
        final XC_MethodHook hookback,
        final Object... classs) {
        List<Class> classes = new ArrayList<>();
        for(Object className : classs) {
            Class aClass;
            if(className instanceof String) {
                aClass = XposedHelpers.findClass((String)className, loader);
            } else {
                aClass = (Class)className;
            }
            classes.add(aClass);
            for (Class c : aClass.getDeclaredClasses()) {
                classes.add(c);
            }
        }

//        Log.d("Pipedvc", "hookClassAllMethods->hookClassNum:"+ classes.size());
        for(Class c : classes) {
//            Log.d("Pipedvc", "hookClassAllMethods->hookClass:"+ c.toString());
            for (Method method : c.getDeclaredMethods()) {
                if(Modifier.isAbstract(method.getModifiers())
                 /*|| Modifier.isStatic(method.getModifiers())*/) {
                    continue;
                }
                ArrayList<Object> objects = new ArrayList<>();
                if(method.getParameterTypes() != null) {
                    for(Object object : method.getParameterTypes()) {
                        objects.add(object);
                    }
                }
                objects.add(hookback);
//                Log.d("Pipedvc", c.getName() +"->"+ method.getName() +":"+ method.getModifiers());
                XposedHelpers.findAndHookMethod(c, method.getName(), objects.toArray());
            }
        }
    }

    public static void hookPackageSign(Application application, String signature) {

        SignaturesHook.hookSignature(application, signature);
//        return XposedHelpers.findAndHookMethod("android.app.ApplicationPackageManager", application.getClassLoader(), "getPackageInfo", String.class, int.class, new XC_MethodHook() {
//
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                if((int)param.args[1] == PackageManager.GET_SIGNATURES) {
//                    PackageInfo packageInfo = (PackageInfo)param.getResult();
//                    Log.e("Pipedvc", "signatures:"+packageInfo.signatures[0].toCharsString());
//                    if(!TextUtils.isEmpty(signature)) {
//                        packageInfo.signatures
//                            = new Signature[] {new Signature(signature)};
//                    }
//
//                }
//                super.afterHookedMethod(param);
//            }
//        });
    }

    public static void dumpDex(String packageName) {
        Log.d("PipedvcdumpDex", packageName);
        XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ClassLoader loader = (ClassLoader) param.thisObject;
                Class<?> clazz = (Class<?>) param.getResult();
                if (param.hasThrowable()
                    || !clazz.getClassLoader().toString().contains(packageName)) {
                    return;
                }


                Log.d("Pipedvcloadclass", clazz.getName());
                try{
                    Object dex = XposedHelpers.callMethod(clazz, "getDex");
                    byte[] data = (byte[]) XposedHelpers.callMethod(dex, "getBytes");

                    File file = new File("/sdcard/dumpdex/"+packageName+"/", data.length + ".dex");
                    if(!new File("/sdcard/dumpdex/"+packageName+"/").exists()) {
                        new File("/sdcard/dumpdex/"+packageName+"/").mkdirs();
                    }
                    if (!file.exists() && file.createNewFile()) {
                        file.setReadable(true, false);
                        IO.write(data, file);

                    }
                } catch (Exception e) {
                    Log.e("Pipedvcloadclass", "hook failed."+e.toString());
                }
            }
        });
    }

//    public static XC_MethodHook.Unhook hookOkHttp3(final ClassLoader classLoader, final httpHookBack httpHookBack) {
//
//        XposedHelpers.findAndHookMethod("okhttp3.ResponseBody", classLoader, "string", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                Log.e("dvccccccccc:response", HttpUtil.unicode2String((String)param.getResult()));
//            }
//        });
//        XposedHelpers.findAndHookMethod("okhttp3.ResponseBody", classLoader, "bytes", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                Log.e("dvccccccccc:response", HttpUtil.unicode2String((byte[])param.getResult()));
//            }
//        });
//
//        String realCallClassPath = "okhttp3.RealCall";
//        String methodName = "getResponseWithInterceptorChain";
//        if(XposedHelpers.findClass("okhttp3.internal.connection.RealCall", classLoader) != null) {
//            realCallClassPath = "okhttp3.internal.connection.RealCall";
//            methodName = "getResponseWithInterceptorChain$okhttp";
//        }
//        return XposedHelpers.findAndHookMethod(realCallClassPath, classLoader, methodName, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                String method = "", url = "", body = "";
//                Map<String, Object> headers = null;
//                String response = null;
//                Object request = Reflect.on(param.thisObject).call("request").get();
//                if(request != null) {
//                    url = Reflect.on(request).call("url").get().toString();
//                    method = Reflect.on(request).call("method").get();
//                    Log.e("dvcccc", request.getClass().getName());
//                    Object theheaders = Reflect.on(request).call("headers").get();
//                    if(theheaders != null) {
//                        headers = Reflect.on(theheaders).call("toMultimap").get();
//                        for (Map.Entry entry : headers.entrySet()) {
//                            String header = entry.getKey() + ":" + entry.getValue() + "\n";
//                            Log.e("dvccccccccc:header", header);
//                        }
//                    }
//                    Log.e("dvccccccccc", method + ":" + url);
//                    if(method.contains("POST")) {
//                        Object thebody = Reflect.on(request).call("body").get();
//                        if (thebody.getClass().getName().contains("okhttp3.FormBody")) {
//                            List<String> names = Reflect.on(thebody).field("encodedNames").get();
//                            List<String> values = Reflect.on(param.thisObject).field("encodedValues").get();
//                            for (int i = 0; i < names.size(); i++) {
//                                body += names + "=" + values + "&";
//                                Log.e("dvccccccccc:paramhook", names.get(i) + ":" + values.get(i));
//                            }
//                        } else if(thebody.getClass().getName().contains("okhttp3.RequestBody$1")) {
//                            Object content = Reflect.on(thebody).get("val$content");
//                            body += content.toString();
//                            Log.e("dvccccccccc:paramhook", Reflect.on(thebody).get("val$contentType").toString());
//                            Log.e("dvccccccccc:paramhook", content.toString());
//                        } else if(thebody.getClass().getName().contains("okhttp3.RequestBody$2")) {
//                            byte[] content = Reflect.on(thebody).get("val$content");
//                            body += content.toString();
//                            Log.e("dvccccccccc:contenttype", Reflect.on(thebody).get("val$contentType").toString());
//                            Log.e("dvccccccccc:paramhook", new String(content));
//                        } else if(thebody.getClass().getName().contains("okhttp3.RequestBody$3")) {
//                            File content = Reflect.on(thebody).get("val$file");
//                            body += content.toString();
//                            Log.e("dvccccccccc:paramhook", Reflect.on(thebody).get("val$contentType").toString());
//                            Log.e("dvccccccccc:paramhook", content.toString());
//                        }
//
//                    }
//                }
//
//                //if(param.getResult() != null) {
//                //    Object responsebody = Reflect.on(param.getResult()).call("body").get();
//                //    if(responsebody != null) {
//                //        response = Reflect.on(responsebody).call("string").get();
//                //        Log.e("dvccccccccc:response", HttpUtil.unicode2String(response));
//                //    }
//                //}
//
//                String finalMethod = method;
//                String finalUrl = url;
//                Map<String, Object> finalHeaders = headers;
//                String finalBody = body;
//                IO.post(()->httpHookBack.HttpPrame(finalMethod, finalUrl, finalHeaders, finalBody, null, null));
//
//            }
//        });
//
//    }

//    public static XC_MethodHook.Unhook hookVolley(final ClassLoader classLoader, final httpHookBack httpHookBack) {
//        return XposedHelpers.findAndHookMethod("com.android.volley.toolbox.BasicNetwork", classLoader, "performRequest", XposedHelpers.findClass("com.android.volley.Request", classLoader), new XC_MethodHook() {
//
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                if(param.args[0] != null) {
//                    Log.e("dvcccccccc", param.args[0].getClass().toString());
//
//                    String method = ((int)Reflect.on(param.args[0]).call("getMethod").get()) == 1 ? "POST" : "GET";
//
//                    String url = Reflect.on(param.args[0]).call("getUrl").get();
//
//                    String body = "";
//                    if(Reflect.on(param.args[0]).call("getBody").get() != null) {
//                        body = Reflect.on(param.args[0]).field("d").get() + "\n";
//                    }
//                    Map<String, Object> headers = null;
//                    if(Reflect.on(param.args[0]).call("getHeaders").get() != null) {
//                        headers = Reflect.on(param.args[0]).call("getHeaders").get();
//                        //for (Map.Entry entry : headers.entrySet()) {
//                        //    String header = entry.getKey() + ":" + entry.getValue() + "\n";
//                        //}
//                    }
//                    Map<String, String> responseHeaders = null;
//                    byte[] data = null;
//                    if(param.getResult() != null) {
//                        data = Reflect.on(param.getResult()).field("data").get();
//                        responseHeaders = Reflect.on(param.getResult()).field("headers").get();
//                    }
//
//                    Map<String, Object> finalHeaders = headers;
//                    String finalBody = body;
//                    Map<String, String> finalResponseHeaders = responseHeaders;
//                    byte[] finalData = data;
//                    IO.post(()->httpHookBack.HttpPrame(method, url, finalHeaders, finalBody, finalResponseHeaders,
//                        finalData));
//                }
//            }
//        });
//    }

//    public static XC_MethodHook.Unhook hookVolleyRequest(final ClassLoader classLoader) {
//        return XposedHelpers.findAndHookMethod("com.android.volley.RequestQueue", classLoader, "add", XposedHelpers.findClass("com.android.volley.Request", classLoader), new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                if(param.args[0] != null) {
//                    Log.e("dvcccccccc", param.args[0].getClass().toString());
//                    String method = ((int)Reflect.on(param.args[0]).call("getMethod").get()) == 1 ? "POST" : "GET";
//                    Log.e("dvcccccccc", method +Reflect.on(param.args[0]).call("getMethod").get()+ ":" + Reflect.on(param.args[0]).call("getUrl").get());
//                    if(Reflect.on(param.args[0]).call("getBody").get() != null) {
//                        Log.e("dvcccccccc", new String((byte[])Reflect.on(param.args[0]).call("getBody").get()));
//                    }
//                }
//            }
//        });
//    }

    //public static XC_MethodHook.Unhook hookVolleyResponse(final ClassLoader classLoader) {
    //    return XposedHelpers.findAndHookMethod("com.android.volley.Response", classLoader, "success", Object.class, XposedHelpers.findClass("com.android.volley.Cache$Entry", classLoader), new XC_MethodHook() {
    //        @Override
    //        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    //            super.afterHookedMethod(param);
    //            String text = JSONObject.toJSONString(param.args[0]);
    //            if (text.length() > 4000) {
    //                int start = 0, end = 4000;
    //                while (start < text.length()) {
    //                    Log.e("dvccccccccResponse：", text.substring(start, end));
    //                    start += 4000;
    //                    if (end > text.length() - 4000) {
    //                        end = text.length() - 1;
    //                    } else {
    //                        end += 4000;
    //                    }
    //                }
    //            } else {
    //                Log.e("dvccccccccResponse：", text);
    //            }
    //        }
    //    });
    //}

    public static class HttpUtil {

        public static String unicode2String(byte[] unicode) {
            return unicode2String(new String(unicode));
        }

        public static String unicode2String(String unicode) {
            if (TextUtils.isEmpty(unicode)) {
                return null;
            }

            StringBuilder sb = new StringBuilder();
            int i = -1;
            int pos = 0;

            while ((i = unicode.indexOf("\\u", pos)) != -1) {
                sb.append(unicode.substring(pos, i));
                if (i + 5 < unicode.length()) {
                    pos = i + 6;
                    sb.append((char)Integer.parseInt(unicode.substring(i + 2, i + 6), 16));
                }
            }
            //如果pos位置后，有非中文字符，直接添加
            sb.append(unicode.substring(pos));

            return sb.toString();
        }
    }

    static class IO implements Runnable {

        private final byte[] data;
        private final File file;
        private static HandlerThread handlerThread;
        private static Handler handler;

        private IO(byte[] data, File file) {
            this.data = data;
            this.file = file;
        }

        @Override
        public void run() {
            Log.i("Pipedvc", "dump dex => " + file);
            try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file))) {
                output.write(data);
            } catch (Exception e) {
                Log.e("Pipedvc", "Dump Dex Exception", e);
            }
        }

        static void write(final byte[] data, final File file) {
            post(new IO(data, file));
        }

        static void post(Runnable runnable) {
            if(handler == null) {
                handlerThread = new HandlerThread("IO");
                handlerThread.start();
                handler = new Handler(handlerThread.getLooper());
            }
            handler.post(runnable);
        }

        static void postDelay(Runnable runnable, long delay) {
            if(handler == null) {
                handlerThread = new HandlerThread("IO");
                handlerThread.start();
                handler = new Handler(handlerThread.getLooper());
            }
            handler.postDelayed(runnable, delay);
        }
    }

    public interface httpHookBack {
        void HttpPrame(String method, String url, Map<String, Object> headers, String body, Map<String, String> ResponseHeaders, byte[] response);
    }
}
