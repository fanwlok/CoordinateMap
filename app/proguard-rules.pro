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
-keepattributes InnerClasses
-dontoptimize
# #  ######### baidu混淆  ##########

# # -------------------------------------------
#百度混淆
-keep class com.baidu.** {*;}
-keep class com.baidubce.** {*;}
-keep class mapsdkvi.com.** {*;}
-dontwarn com.baidu.**
-keep class com.baidu.autoupdatesdk.** {*;}
# # -------------------------------------------


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

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
# #  ######## retrofit2混淆  ##########

# # -------------------------------------------
# Retrofit

#-keepattributes Signature
 # Retain service method parameters.
 -keepclassmembernames,allowobfuscation interface * {
     @retrofit2.http.* <methods>;
 }
 # Ignore annotation used for build tooling.
 -dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**


-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}


# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.fanweilin.coordinatemap.DataModel.** { *; }
-keep class com.fanweilin.greendao.** { *; }
# 嵌入广点通sdk时必须添加
-keep class com.qq.e.** {
    public protected *;
}
# 嵌入广点通sdk时必须添加
-keep class android.support.v4.app.NotificationCompat**{
    public *;
}
-keepattributes *Annotation*
-keepclassmembers class ** {
    public void on*Event(...);
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-dontwarn  org.eclipse.jdt.annotation.**

-keepattributes Signature

-keep class sun.misc.Unsafe { *; }
#腾讯通信
-keep class com.tencent.**{*;}
-dontwarn com.tencent.**

-keep class tencent.**{*;}
-dontwarn tencent.**

-keep class qalsdk.**{*;}
-dontwarn qalsdk.**
#baidu

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-dontwarn com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
-dontwarn com.bumptech.glide.load.resource.bitmap.Downsampler
-dontwarn com.bumptech.glide.load.resource.bitmap.HardwareConfigState
# nineoldandroids
-keep interface com.nineoldandroids.view.** { *; }
-dontwarn com.nineoldandroids.**
-keep class com.nineoldandroids.** { *; }
# support-v7-appcompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
# support-design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
#DXF
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}
