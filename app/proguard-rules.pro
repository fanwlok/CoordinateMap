# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in e:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# # -------------------------------------------

# #  ######### baidu混淆  ##########

# # -------------------------------------------

-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**
-keep class com.baidu.kirin.** {*;}
-keep class com.baidu.mobstat.** {*;}
-keep class com.baidu.**{public protected *;}
# # -------------------------------------------

# #  ######### v7混淆  ##########
# # -------------------------------------------

-keep class !android.support.v7.internal.view.menu.**,android.support.** {*;}
# # -------------------------------------------

# #  ######## event混淆  ##########

# # -------------------------------------------

-keepclassmembers class ** {
    public void onEvent*(**);
}
# # -------------------------------------------

# #  ######## greenDao混淆  ##########

# # -------------------------------------------

-keep class org.greenrobot.greendao.** {*;}

-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties
-dontwarn org.greenrobot.greendao.database.**
# # -------------------------------------------


# # -------------------------------------------


# #  ######## retrofit2混淆  ##########

# # -------------------------------------------
# Retrofit

# Retrofit 2.X
## https://square.github.io/retrofit/ ##

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
#-keep class com.unity3d.player.**{*;}
-dontwarn com.unity3d.player.**
-dontwarn okio.**


## GSON 2.2.4 specific rules ##

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }