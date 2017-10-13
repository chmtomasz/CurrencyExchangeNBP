package org.tchm.currency;

import java.util.Date;

public class Currency {
    public double c_value;
    public int conv;
    public String c_name;
    public String c_code;
    public Date c_date;

    public Currency(double c_value, int conv, String c_name, String c_code, Date c_date){
        this.c_code = c_code;
        this.conv = conv;
        this.c_value = c_value;
        this. c_name = c_name;
        this.c_date = c_date;
    }
}
