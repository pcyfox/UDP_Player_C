//
// Created by LN on 2021/3/9.
//

#ifndef UDP_PLAYER_AVPACKET_H
#define UDP_PLAYER_AVPACKET_H


class AVPacket {
public:
    unsigned char *data;
    int size;
    int nalu_type;
    int cq;
    int pkt_interval;
    int pts = -1;

public:
    ~AVPacket();
};


#endif //UDP_PLAYER_AVPACKET_H
