package com.example.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100

    private var mTimer: Timer? = null

    //    タイマー用の時間のための変数
    private var mTimerSec = 0.0

    private var mHandler = Handler()
    //    タイマーがストップしているときtrue
    private var flag = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }


    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )

        if (cursor.moveToFirst()) {

            // indexからIDを取得し、そのIDから画像のURIを取得する
            var fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            var id = cursor.getLong(fieldIndex)
            var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            Log.d("ANDROID", "URI : " + imageUri.toString())
            imageView.setImageURI(imageUri)

//            進むボタンを押して画像が最後なら最初の画像表示
            next_button.setOnClickListener {
                if (cursor.moveToNext() == false) {
                    cursor.moveToFirst()
                }
                var fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                var id = cursor.getLong(fieldIndex)
                var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                Log.d("ANDROID", "URI : " + imageUri.toString())
                imageView.setImageURI(imageUri)
            }

//                戻るボタンを押して画像が最初なら最後の画像表示
            back_button.setOnClickListener {
                if (cursor.moveToPrevious() == false) {
                    cursor.moveToLast()
                }
                var fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                var id = cursor.getLong(fieldIndex)
                var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                Log.d("ANDROID", "URI : " + imageUri.toString())
                imageView.setImageURI(imageUri)
            }
        }

        stop_button.setOnClickListener {
            if (flag) {


                next_button.isEnabled = false   //進むボタンの無効化
                back_button.isEnabled = false   //戻るボタンの無効化
                flag = false
                if (mTimer == null) {
                    //        タイマーの作成
                    mTimer = Timer()
                    stop_button.text = "停止"
                    cursor.moveToFirst()
                    var fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    var id = cursor.getLong(fieldIndex)
                    var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    Log.d("ANDROID", "URI : " + imageUri.toString())
                    imageView.setImageURI(imageUri)


//        タイマーの始動
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mHandler.post {
//                                最後の画像なら最初に戻る
                                if (cursor.moveToNext() == false) {
                                    cursor.moveToFirst()
                                }
                                //       以下、画像を切り替える

                                var fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                var id = cursor.getLong(fieldIndex)
                                var imageUri =
                                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                Log.d("ANDROID", "URI : " + imageUri.toString())
                                imageView.setImageURI(imageUri)
                            }


                        }


                    }, 100, 2000) //最初に始動させるまでの100ミリ秒、ループの間隔を2000ミリ秒
                }
            } else {

                    stop_button.text = "再生"
                    next_button.isEnabled = true   //進むボタンの有効化
                    back_button.isEnabled = true   //戻るボタンの有効化
                    mTimer!!.cancel()
                    mTimer = null
                    flag = true

            }
//            imageView.setImageURI(imageUri)


        }


    }
}




