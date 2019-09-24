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
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            Log.d("ANDROID", "URI : " + imageUri.toString())

            next_button.setOnClickListener {
                cursor.moveToNext()
                imageView.setImageURI(imageUri)
            }

            back_button.setOnClickListener {
                cursor.moveToPrevious()
                imageView.setImageURI(imageUri)
            }

            stop_button.setOnClickListener {
                if (mTimer == null) {
                    //        タイマーの作成
                    mTimer = Timer()
                    stop_button.text = "停止"

//        タイマーの始動
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            while (mTimerSec <= 2) {
                                mTimerSec += 0.1
                                mHandler.post {

                                }
                            }
                            mTimerSec = 0.0
                            cursor.moveToNext()

                            run()


                            }
                    }, 100, 100) //最初に始動させるまでの100ミリ秒、ループの感覚を100ミリ秒
                }
            }
            imageView.setImageURI(imageUri)
        }
        cursor.close()



    }
}
