package com.taike.udpplayer

import android.os.Bundle
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.taike.lib_udp_player.MultiCastPlayer

class MainActivity : AppCompatActivity() {
    private val MAX_FRAME_LEN = 4 * 1024 * 1024 //视频帧大小限制
    val multiCastHost = "239.0.0.200"
    private val videoPort = 2021
    private var player: MultiCastPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sv = findViewById<SurfaceView>(R.id.udp_surface_view)
        player = MultiCastPlayer(
            multiCastHost,
            videoPort,
            MAX_FRAME_LEN,
            sv
        )
        window.decorView.postDelayed({
            player?.startPlay()
        }, 100)
    }
}