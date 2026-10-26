# Rules applied to merchant apps that minify with R8/ProGuard.

# Public API
-keep public class com.grayquest.android.payment.sdk.GQPaymentSDK {
    public static void initiate(android.content.Context, org.json.JSONObject, org.json.JSONObject);
}
-keep public interface com.grayquest.android.payment.sdk.GQPaymentSDKListener { *; }

# WebView JS bridge: the web page calls these by name
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface

# Gson models inside the SDK
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
# Gson creates these reflectively; without this R8 sees no `new`, deletes the
# classes and turns every successful response into a ClassCastException.
-if class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation class <1> {
    <init>();
}

# Retrofit generic return types (R8 full mode)
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keepattributes Signature, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# OkHttp 4.9.x references optional TLS providers that most apps don't ship.
# OkHttp 4.10+ bundles these rules; without them, merchant release builds on
# AGP 8 fail with "Missing class org.conscrypt.Conscrypt" and similar.
-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
