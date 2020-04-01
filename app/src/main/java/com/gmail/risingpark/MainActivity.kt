package com.gmail.risingpark

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gmail.risingvoiceindicator.VoiceIndicator
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.floor


class MainActivity : AppCompatActivity() {

    private var mIsOn :Boolean = false
    var mRecorder :MediaRecorder? = null
    var thread :Thread? = null
    var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()

        initView()
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 100)
        }
    }

    private fun initView() {
        voice_indicator.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                return@setOnClickListener
            }

            mIsOn = !mIsOn
            if (mIsOn) {
                startRecorder()
            } else {
                stopRecorder()
            }
        }
    }


    private fun startRecorder() {
        if (mRecorder == null) {
            mRecorder = MediaRecorder()
            mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mRecorder!!.setOutputFile("/dev/null")
            try {
                mRecorder!!.prepare()
                mRecorder!!.start()
            } catch (e: Exception) {
                Log.e("cor.park", "IOException: " + Log.getStackTraceString(e))
            }
        }

        if (thread == null) {
            thread = Thread(Runnable {
                try {
                    while (true){
                        Thread.sleep(200)
                        mHandler.post(Runnable {
                            updateDb()
                            Log.d("risingpark", "[soundDb]"+soundDb())
                            voice_indicator.setDb(soundDb().toFloat())
                        })

                    }
                } catch (e :InterruptedException) {
                    thread = null
                }
            })
            thread?.start()
        }

        voice_indicator.startAnimation(VoiceIndicator.START_USER)
    }

    private fun stopRecorder() {
        if (mRecorder != null) {
            try {
                mRecorder!!.stop()
            } catch (e :Exception){}
            mRecorder!!.release()
            mRecorder = null
        }

        thread?.interrupt()
        voice_indicator.stopAnimation()
    }

    @SuppressLint("SetTextI18n")
    fun updateDb() {
        decibel_text.setText(soundDb().toInt().toString() + " dB")
    }

    fun soundDb(): Double {
        return 20 * Math.log10(getAmplitude())
    }


    fun getAmplitude(): Double {
        return if (mRecorder != null) mRecorder!!.maxAmplitude.toDouble() else 0.0
    }
}
