package br.com.coutinhoanderson.econome.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleFormat {
    public static Double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
