package jp.ac.jec.cm0199.cloudinaryquickstartkotlin

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Cloudinary configuration
        val config = HashMap<String, Any>()
        config["cloud_name"] = BuildConfig.CLOUD_NAME
        config["secure"] = true
        // initの呼び出しはアプリで一度のみ
        // 参考: https://cloudinary.com/documentation/android_integration#setup
        MediaManager.init(this, config)

        MediaManager.get().cloudinary.url().transformation().width(300).crop("scale")
    }
}