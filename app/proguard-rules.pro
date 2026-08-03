# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 1. Keep generic type signatures
# If this is removed, R8 will erase the <List<List<Set<String>>>> part,
# and Gson won't know what types to deserialize your JSON into.
-keepattributes Signature

# 2. Keep annotations and inner classes (Required for Gson's reflection to work)
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# 3. Protect Gson's TypeToken
# This prevents R8 from obfuscating the anonymous inner class you created: new TypeToken<...>(){}.
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Protect TexasHoldemExactCalc from having its JNI callback methods renamed or stripped
-keep class com.leslie.cjpokeroddscalculator.calculation.TexasHoldemExactCalc {
    boolean duringSimulations();
    void afterAllSimulations(double[][], boolean);
}

# Protect TexasHoldemMonteCarloCalc from having its JNI callback methods renamed or stripped
-keep class com.leslie.cjpokeroddscalculator.calculation.TexasHoldemMonteCarloCalc {
    boolean duringSimulations(double[][]);
    void afterAllSimulations(double[][]);
}