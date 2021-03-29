package com.taike.lib_udp_player;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by Auser on 2018/5/28.
 */

public class MultiCastPlayerView extends RelativeLayout {
    private static final String TAG = "MultiCastPlayer";
    //MediaCodec variable
    private volatile boolean isPlaying = false;
    static String multiCastHost = "239.0.0.200";
    private int videoPort = 2021;
    private MulticastSocket multicastSocket;
    private Handler handler;
    private final static int MAX_UDP_PACKET_LEN = 65507;//UDP包大小限制
    private NativePlayer nativeUDPPlayer;
    private int maxFrameLen;
    private final SurfaceView surfaceView;

    public MultiCastPlayerView(Context context) {
        super(context);
    }

    public MultiCastPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiCastPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MultiCastPlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        surfaceView = new SurfaceView(getContext());

    }

    private void addSurfaceView() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(surfaceView, params);
    }

    public void config(String host, int port, int maxFrameLen) {
        multiCastHost = host;
        videoPort = port;
        this.maxFrameLen = maxFrameLen;
        HandlerThread handlerThread = new HandlerThread("Fuck Video Data Handler");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        post(() -> {
            initMultiBroadcast();
            addSurfaceView();

        });
        initNativePlayer(surfaceView);
    }

    private void initMultiBroadcast() {
        try {
            multicastSocket = new MulticastSocket(videoPort);
            InetAddress receiveAddress = InetAddress.getByName(multiCastHost);
            multicastSocket.joinGroup(receiveAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void initNativePlayer(SurfaceView surfaceView) {
        nativeUDPPlayer = new NativePlayer();
        nativeUDPPlayer.init(BuildConfig.DEBUG);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                int rootW = getWidth();
                int rootH = getHeight();
                int videoW = 1920;
                int videoH = 1080;
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
                lp.width = getWidth();
                lp.height = getHeight();
                if (rootH * videoW > videoH * rootH) {
                    lp.height = (int) (rootW * (videoH * 1.0 / videoW));
                } else {
                    lp.width = (int) (rootH * (videoW * 1.0 / videoH));
                }
                surfaceView.setLayoutParams(lp);
                holder.setFixedSize(lp.width, lp.height);
                nativeUDPPlayer.configPlayer(holder.getSurface(), lp.width, lp.height);
                if (nativeUDPPlayer.getState() == PlayState.PAUSE) {
                    nativeUDPPlayer.play();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, "surfaceChanged() called with: holder = [" + holder + "], format = [" + format + "], width = [" + width + "], height = [" + height + "]");
                nativeUDPPlayer.changeSurface(holder.getSurface(), width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "surfaceDestroyed() called with: holder = [" + holder + "]");
                isPlaying = false;
                nativeUDPPlayer.pause();
            }
        });
    }

    /*
    开始播放
     */
    public void startPlay() {
        if (isPlaying) {
            Log.e(TAG, "start play failed.  player is playing");
        } else {
            isPlaying = true;
            nativeUDPPlayer.play();
            handler.post(this::startReceiveData);
        }
    }

    private void startReceiveData() {
        byte[] receiveByte = new byte[MAX_UDP_PACKET_LEN];
        DatagramPacket dataPacket = new DatagramPacket(receiveByte, receiveByte.length);
        while (isPlaying) {
            try {
                multicastSocket.receive(dataPacket);
                nativeUDPPlayer.handlePkt(receiveByte, dataPacket.getLength(), maxFrameLen, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "startReceiveData:() over!");
    }

    /*
     *停止播放
     */
    public void stopPlay() {
        Log.d(TAG, "stopPlay() called");
        isPlaying = false;
    }


    public void pause() {
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv).append(" ");
        }
        return stringBuilder.toString();
    }

    public static String intToHex(int i) {
        int v = i & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.length() < 2) {
            return "0" + hv;
        } else {
            return hv;
        }
    }


    public boolean isPlaying() {
        return isPlaying;
    }


    public void release() {
        Log.e(TAG, "release() called");
        isPlaying = false;
    }


}
