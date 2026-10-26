# R8 rules for building the SDK's release AAR.
# Only the merchant-facing API keeps its names; everything else is renamed
# and repackaged so a decompiled AAR shows the public surface, not the internals.

# Merchant-facing API
-keep public class com.grayquest.android.payment.sdk.GQPaymentSDK {
    public static void initiate(android.content.Context, org.json.JSONObject, org.json.JSONObject);
}
-keep public interface com.grayquest.android.payment.sdk.GQPaymentSDKListener { *; }

# WebView JS bridge: the web page calls these by name
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Gson models: fields are set reflectively; names come from @SerializedName
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

# Retrofit service interface and its generic return types (R8 full mode)
-keep,allowobfuscation interface com.grayquest.android.payment.sdk.ApiInterface
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault

# Hide internals; keep line numbers so crash reports can be decoded with mapping.txt.
# No -allowaccessmodification: it widens renamed internals to public, which would
# put them in merchants' autocomplete.
-repackageclasses 'com.grayquest.android.payment.sdk.a'
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
