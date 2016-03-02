package xyz.maksimenko.smssearch;

/**
 * Created by smaksimenko on 16.02.2016.
 */

public class SMS {
    private String address;
    private String body;
    private long date;
    private String name;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public SMS(String address, String body, long date){
        this.address = address;
        this.body = body;
        this.date = date;
        this.name = (address + " " + body);
        if(name.length() > 24){
            name = name.substring(0, 24);
        }
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
