package br.com.coutinhoanderson.econome.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Expense implements Parcelable {
    private String expenseId = "";
    private String name = "";
    private String cost = "";
    private String category = "";

    public Expense(String name, String cost, String category, String expenseId) {
        this.name = name;
        this.expenseId = expenseId;
        this.cost = cost;
        this.category = category;
    }
    public Expense(String name, String cost, String category) {
        this.name = name;
        this.cost = cost;
        this.category = category;
    }

    private Expense(Parcel expense) {
        this.name = expense.readString();
        this.expenseId = expense.readString();
    }

    public Expense() {
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Creator<Expense> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.expenseId);
    }

    public static final Parcelable.Creator<Expense> CREATOR = new Parcelable.Creator<Expense>() {
        public Expense createFromParcel(Parcel expense) {
            return new Expense(expense);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };
}
