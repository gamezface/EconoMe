package br.com.coutinhoanderson.econome.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;

public class Group implements Parcelable {
    private String name;
    private HashMap<String,User> members;
    private List<Expense> expenses;
    private String totalBudget;
    private String remainingFunds;
    private String totalSpent;

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public String getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(String totalBudget) {
        this.totalBudget = totalBudget;
    }

    public String getRemainingFunds() {
        return remainingFunds;
    }

    public void setRemainingFunds(String remainingFunds) {
        this.remainingFunds = remainingFunds;
    }

    public HashMap<String,User> getMembers() {
        return members;
    }

    public Group() {

    }

    public Group(String name, HashMap<String,User> members) {
        this.name = name;
        this.members = members;
    }

    private Group(Parcel group) {
        this.name = group.readString();
        this.members = group.readHashMap(User.class.getClassLoader());
    }

    public String getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(String totalSpent) {
        this.totalSpent = totalSpent;
    }

    public void setMembers(HashMap<String,User> members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(members);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        public Group createFromParcel(Parcel group) {
            return new Group(group);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}
