package sam.znv.kafka;


//{"result": "success", "data": {"age": 32.57, "attractive": 96.3, "smile": 1,
// "gender": 0, "eyeglass": 0, "sunglass": 0, "mask": 0, "race": 0, "eyeOpen": 1,
// "mouthOpen": 0, "beard": 1}, "time_used": 37}

public class PicAttr {
    Double age=25.0;


    Double attractive;
    int smile=0;
    int gender=0;
    int eyeglass=0;
    int sunglass=0;
    int mask=0;
    int race=0;
    int eyeOpen=0;
    int mouthOpen=0;
    int beard=1;



    public Double getAge() {
        return age;
    }

    public void setAge(Double age) {
        this.age = age;
    }

    public Double getAttractive() {
        return attractive;
    }

    public void setAttractive(Double attractive) {
        this.attractive = attractive;
    }

    public int getSmile() {
        return smile;
    }

    public void setSmile(int smile) {
        this.smile = smile;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getEyeglass() {
        return eyeglass;
    }

    public void setEyeglass(int eyeglass) {
        this.eyeglass = eyeglass;
    }

    public int getSunglass() {
        return sunglass;
    }

    public void setSunglass(int sunglass) {
        this.sunglass = sunglass;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public int getRace() {
        return race;
    }

    public void setRace(int race) {
        this.race = race;
    }

    public int getEyeOpen() {
        return eyeOpen;
    }

    public void setEyeOpen(int eyeOpen) {
        this.eyeOpen = eyeOpen;
    }

    public int getMouthOpen() {
        return mouthOpen;
    }

    public void setMouthOpen(int mouthOpen) {
        this.mouthOpen = mouthOpen;
    }

    public int getBeard() {
        return beard;
    }

    public void setBeard(int beard) {
        this.beard = beard;
    }
}
