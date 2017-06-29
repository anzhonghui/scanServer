package entity;

/**
 * Created by vaf71 on 2017/5/9.
 */
public class Data {
    private int id;
    private String data;
    private int isSend;
    private int isInvalid;//是否作废

    @Override
    public String toString() {
        return "Data{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", isSend=" + isSend +
                ", isInvalid=" + isInvalid +
                '}';
    }

    public int getIsInvalid() {
        return isInvalid;
    }

    public void setIsInvalid(int isInvalid) {
        this.isInvalid = isInvalid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getIsSend() {
        return isSend;
    }

    public void setIsSend(int isSend) {
        this.isSend = isSend;
    }
}
