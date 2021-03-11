//
// Created by LN on 2021/3/1.
//

#ifndef UDP_PLAYER_RTPUNPACKET_H
#define UDP_PLAYER_RTPUNPACKET_H


struct RtpUnpackResult {
    unsigned int length;
    unsigned int pkt_interval;
    unsigned int curr_Sq;
    unsigned char packet_NAL_unit_type;
    unsigned char *data;
} typedef *UnpackResult;


typedef void (*Callback)(UnpackResult
result);

int UnPacket(unsigned char *rtpPacket, const unsigned int length, const unsigned int maxFrameLen,
             Callback callback);

#endif //UDP_PLAYER_RTPUNPACKET_H
