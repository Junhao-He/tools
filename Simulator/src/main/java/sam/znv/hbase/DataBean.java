package sam.znv.hbase;

/**
 * Created by 86157 on 2019/8/20.
 */

/*
    ("LIB_ID","Int"),
    ("PERSON_ID","String"),
    ("CONTROL_START_TIME","String"),
    ("CONTROL_END_TIME","String"),
    ("FLAG","Int"),
    ("BIRTH","String"),
    ("PERSON_NAME","String"),
    ("CONTROL_EVENT_ID","String"),
    ("SEX","Int"),
    ("FEATURE","Bytes"),
    ("PERSONLIB_TYPE","Int"),
    ("IS_DEL","String"))
 */
public class DataBean {
    private int LIB_ID;
    private String PERSON_ID;
    private String CONTROL_START_TIME;
    private String CONTROL_END_TIME;
    private int FLAG;
    private String BIRTH;
    private String PERSON_NAME;
    private String CONTROL_EVENT_ID;
    private int SEX;
    private byte[] FEATURE;
    private int PERSONLIB_TYPE;
    private String IS_DEL;
    public DataBean(){

    }
    public DataBean(int LIB_ID,
                    String PERSON_ID,
                    String CONTROL_START_TIME,
                    String CONTROL_END_TIME,
                    int FLAG,
                    String BIRTH,
                    String PERSON_NAME,
                    String CONTROL_EVENT_ID,
                    int SEX,
                    byte[] FEATURE, int PERSONLIB_TYPE, String IS_DEL) {
        this.LIB_ID = LIB_ID;
        this.PERSON_ID = PERSON_ID;
        this.CONTROL_START_TIME = CONTROL_START_TIME;
        this.CONTROL_END_TIME = CONTROL_END_TIME;
        this.FLAG = FLAG;
        this.BIRTH = BIRTH;
        this.PERSON_NAME = PERSON_NAME;
        this.CONTROL_EVENT_ID = CONTROL_EVENT_ID;
        this.SEX = SEX;
        this.FEATURE = FEATURE;
        this.PERSONLIB_TYPE = PERSONLIB_TYPE;
        this.IS_DEL = IS_DEL;
    }

    public int getLIB_ID() {
        return LIB_ID;
    }

    public String getPERSON_ID() {
        return PERSON_ID;
    }

    public String getCONTROL_START_TIME() {
        return CONTROL_START_TIME;
    }

    public String getCONTROL_END_TIME() {
        return CONTROL_END_TIME;
    }

    public int getFLAG() {
        return FLAG;
    }

    public String getBIRTH() {
        return BIRTH;
    }

    public String getPERSON_NAME() {
        return PERSON_NAME;
    }

    public String getCONTROL_EVENT_ID() {
        return CONTROL_EVENT_ID;
    }

    public int getSEX() {
        return SEX;
    }

    public byte[] getFEATURE() {
        return FEATURE;
    }

    public int getPERSONLIB_TYPE() {
        return PERSONLIB_TYPE;
    }

    public String getIS_DEL() {
        return IS_DEL;
    }

    public void setLIB_ID(int LIB_ID) {
        this.LIB_ID = LIB_ID;
    }

    public void setPERSON_ID(String PERSON_ID) {
        this.PERSON_ID = PERSON_ID;
    }

    public void setCONTROL_START_TIME(String CONTROL_START_TIME) {
        this.CONTROL_START_TIME = CONTROL_START_TIME;
    }

    public void setCONTROL_END_TIME(String CONTROL_END_TIME) {
        this.CONTROL_END_TIME = CONTROL_END_TIME;
    }

    public void setFLAG(int FLAG) {
        this.FLAG = FLAG;
    }

    public void setBIRTH(String BIRTH) {
        this.BIRTH = BIRTH;
    }

    public void setPERSON_NAME(String PERSON_NAME) {
        this.PERSON_NAME = PERSON_NAME;
    }

    public void setCONTROL_EVENT_ID(String CONTROL_EVENT_ID) {
        this.CONTROL_EVENT_ID = CONTROL_EVENT_ID;
    }

    public void setSEX(int SEX) {
        this.SEX = SEX;
    }

    public void setFEATURE(byte[] FEATURE) {
        this.FEATURE = FEATURE;
    }

    public void setPERSONLIB_TYPE(int PERSONLIB_TYPE) {
        this.PERSONLIB_TYPE = PERSONLIB_TYPE;
    }

    public void setIS_DEL(String IS_DEL) {
        this.IS_DEL = IS_DEL;
    }
}
