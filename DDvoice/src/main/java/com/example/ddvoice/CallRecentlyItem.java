package com.example.ddvoice;

/**
 * Created by owen_ on 2016-03-29.
 */
public class CallRecentlyItem {

    private String name;
    private String phoneNumber;
    private String callTime;

    public CallRecentlyItem(String name,String phoneNumber,String callTime){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.callTime = callTime;
    }

    public CallRecentlyItem(){


    }

    public String getName(){
        return name;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public String getCallTime(){
        return callTime;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void setCallTime(String callTime){
        this.callTime = callTime;
    }


}
