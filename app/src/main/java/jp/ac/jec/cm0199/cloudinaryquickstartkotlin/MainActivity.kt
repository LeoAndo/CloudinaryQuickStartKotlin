package jp.ac.jec.cm0199.cloudinaryquickstartkotlin

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel

class MainActivity : AppCompatActivity() {
    /**
     * ActivityResultLauncher to handle the result of the image selection.
     */
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) {
                Log.d("Cloudinary Quickstart", "ファイルを選択せずにアプリに戻りました")
                return@registerForActivityResult
            }
            uploadImage(this, uri)
        }

    private lateinit var uploadedImageview: ImageView
    private lateinit var avatarImageView: ShapeableImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 画像表示用のImageViewを取得する.
        uploadedImageview = findViewById(R.id.uploaded_imageview)
        avatarImageView = findViewById(R.id.avatar_imageview)

        // アバター表示用に完全な円形に設定する.(これ以外にもstyleで設定する方法などある.)
        val shapeAppearanceModel = ShapeAppearanceModel.builder()
            .setAllCornerSizes(ShapeAppearanceModel.PILL)
            .build()
        avatarImageView.setShapeAppearanceModel(shapeAppearanceModel)

        // 画像選択ボタンのクリックイベントを設定する.
        findViewById<View>(R.id.upload_button).setOnClickListener {
            getContent.launch(IMAGE_MIMETYPE)
        }
    }

    /**
     * Uploads the selected image to Cloudinary.
     *
     * @param context The context of the application.
     * @param uri     The URI of the selected image.
     */
    private fun uploadImage(context: Context, uri: Uri) {
        MediaManager.get().upload(uri).unsigned(BuildConfig.UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    Log.d("Cloudinary Quickstart", "Upload start requestId: $requestId")
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    Log.d("Cloudinary Quickstart", "Upload progress")
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as String?
                    Log.d("Cloudinary Quickstart", "Upload success url: $url")
                    // 画像を表示する.
                    Glide.with(context).load(url).into(uploadedImageview)
                    Glide.with(context).load(url).into(avatarImageView)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    Log.d("Cloudinary Quickstart", "Upload failed: " + error.description)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    Log.d("Cloudinary Quickstart", "Upload rescheduled: " + error.description)
                }
            }).dispatch()
    }

    companion object {
        private const val IMAGE_MIMETYPE = "image/*"
    }
}