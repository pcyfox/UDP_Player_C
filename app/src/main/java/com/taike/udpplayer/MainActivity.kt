package com.taike.udpplayer

import android.os.Bundle
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.taike.lib_udp_player.MultiCastPlayer
import com.taike.lib_udp_player.MultiCastPlayerView

class MainActivity : AppCompatActivity() {
    private val maxFrameLen = 4 * 1024 * 1024 //视频帧大小限制
    private val multiCastHost = "239.0.0.200"
    private val videoPort = 2021
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val playerView: MultiCastPlayerView = findViewById(R.id.view_mcpv)
        playerView.config(multiCastHost, videoPort, maxFrameLen)
        playerView.postDelayed({
            playerView.startPlay()
        }, 200)
    }
}