package com.taike.lib_udp_player.udp;


import android.view.Surface;

public class NativeUDPPlayer {
    static {
        System.loadLibrary("udp_player");
    }


    //-------------for native-------------------------
    public native int init(boolean isDebug);

    public native int configPlayer(Surface surface, int w, int h);

    public native int changeSurface(Surface surface, int w, int h);

    public native int handleRTPPkt(byte[] rtpPkt, int pktLen, int maxFrameLen);

    public native int play();

    public native int stop();

    public native int pause();


}
