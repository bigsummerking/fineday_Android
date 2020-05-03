package com.example.finedayapp.DailyCount;

import java.io.Serializable;

public class CostBean implements Serializable {
    private String CostTitle;
    private String CostDate;
    private String CostMoney;

    public String getCostTitle() {
        return CostTitle;
    }

    public void setCostTitle(String costTitle) {
        CostTitle = costTitle;
    }

    public String getCostDate() {
        return CostDate;
    }

    public void setCostDate(String costDate) {
        CostDate = costDate;
    }

    public String getCostMoney() {
        return CostMoney;
    }

    public void setCostMoney(String costMoney) {
        CostMoney = costMoney;
    }

}
