###Proguard configuration for Gson
-keep class * implements android.os.Parcelable { *; }
-keepclassmembers class * implements android.os.Parcelable { *; }