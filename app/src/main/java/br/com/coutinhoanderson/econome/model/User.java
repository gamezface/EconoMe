package br.com.coutinhoanderson.econome.model;

import android.os.Parcel;
import android.os.Parcelable;


public class User implements Parcelable {
    private String name;
    private String budget;
    private String phone;
    private String userId;

    public String getUserId() {
        return userId;
    }
    
    public User(String name, Object budget, String phone, String userId) {
        this.name = name;
        this.budget = budget.toString();
        this.phone = phone;
        this.userId = userId;
    }
    public User() {}
    
    private User(Parcel user) {
        this.name = user.readString();
        this.budget = user.readString();
        this.phone = user.readString();
        this.userId = user.readString();
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(Object budget) {
        this.budget = budget.toString();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.budget);
        dest.writeString(this.phone);
        dest.writeString(this.userId);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel user) {
            return new User(user);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
