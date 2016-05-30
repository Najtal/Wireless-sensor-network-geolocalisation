package ucc;


import constant.RssiType;

/**
 */
public interface RssiDTO {

    int getVersion();

    int getFrom();

    void setFrom(int from);

    int getTo();

    void setTo(int to);

    int getRssi();

    void setRssi(int rssi);

    int getSequenceNo();

    void setSequenceNo(int sequenceNo);

    public RssiType getType();

    public void setType(RssiType type);

    public double getDistanceMeters();

}
