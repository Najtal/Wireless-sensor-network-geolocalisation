package ucc;


/**
 */
public interface AnchorDTO {

    public int getVersion();

    public int getPosx();

    public void setPosx(int posx);

    public int getPosy();

    public void setPosy(int posy);

    public int getBatteryLvl();

    public void setBatteryLvl(int batteryLvl);

    public int getId();

    public void setId(int id);

    public double getOffset();

    double getAlpha();
}
