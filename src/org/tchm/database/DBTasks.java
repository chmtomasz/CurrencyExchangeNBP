package org.tchm.database;

import org.tchm.currency.Currency;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DBTasks {

    public void createTable(Connection c) {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS CURRENCY_TC " +
                    "(id serial PRIMARY KEY NOT NULL," +
                    " CURRENCY_CODE VARCHAR(4)    NOT NULL, " +
                    " CURRENCY_NAME            text     NOT NULL, " +
                    " C_DATE        DATE, " +
                    " CONV         NUMERIC, " +
                    " C_VALUE NUMERIC)";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }



    public void insertSingleCurrency(Connection c,String cCode, String cValue, String conv, String cName,String date  ) {
        PreparedStatement stmt = null;
        try {

            String sql = "INSERT INTO CURRENCY_TC (CURRENCY_CODE,CURRENCY_NAME,C_DATE,CONV,C_VALUE) "
                    + "VALUES (?,?,?,?,?);";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            stmt = c.prepareStatement(sql);

            stmt.setString(1,cCode);
            stmt.setString(2,cName);
            stmt.setDate(3, new Date(formatter.parse(date).getTime()));
            stmt.setInt(4, Integer.parseInt(conv));
            stmt.setDouble(5, Double.parseDouble(cValue.replace(',','.')));
            stmt.executeUpdate();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }

    }

    public List<Currency> selectCurrency(Connection c, String dateStart, String dateEnd, String valueCode){
        PreparedStatement stmt = null;

        String sql = "SELECT * from currency_tc where currency_code = ? AND c_date >= ?::date AND c_date <= ?::date";
        List<Currency> cList = new ArrayList<>();

        try {
            stmt = c.prepareStatement(sql);

            stmt.setString(1,valueCode);
            stmt.setString(2, dateStart);
            stmt.setString(3, dateEnd);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
            cList.add(new Currency(
                    rs.getDouble("c_value"),
                    rs.getInt("conv"),
                    rs.getString("currency_name"),
                    rs.getString("currency_code"),
                    rs.getDate("c_date")));
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cList;

    }

    public List<String> selectSingleCurrency(Connection c) {
        PreparedStatement stmt = null;

        String sql = "SELECT DISTINCT currency_code from currency_tc";
        List<String> cList = new ArrayList<>();
        try {
            stmt = c.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
            cList.add(rs.getString(1));
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cList;
    }

    public boolean checkIfTableEmpty(Connection c) {
        PreparedStatement stmt = null;
        Boolean isEmpty = true;
        String sql = "SELECT count(*) FROM (SELECT 1 FROM currency_tc LIMIT 1) AS tempValue";
        try {
            stmt = c.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
                if(rs.getInt(1) == 1)
                    isEmpty = false;
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isEmpty;
    }

    public void clearTable(Connection c) {
        PreparedStatement stmt = null;
        String sql = "DELETE FROM CURRENCY_TC";
        try {
            stmt = c.prepareStatement(sql);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getLastDay(Connection c){
        PreparedStatement stmt = null;
        String sql = "Select MAX(c_date) from currency_tc";
        String lastDate = null;
        try {
            stmt = c.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
                lastDate = rs.getString(1);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lastDate;
    }

    public void batchInsert(Connection c, String[] codes, String[] descriptions, List<String[]> allCurrenciesValue){

        String sql = "INSERT INTO CURRENCY_TC (CURRENCY_CODE,CURRENCY_NAME,C_DATE,CONV,C_VALUE) "
                + "VALUES (?,?,?,?,?);";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = c.prepareStatement(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        for(String[] c_values : allCurrenciesValue){

            for(int i = 0;i < codes.length; i++){
                if(codes[i].equals("nr tabeli")){
                    break;
                } else if(codes[i].equals("data")){
                    continue;
                }
                String currencyDate = c_values[0].substring(0,4)+'-'+c_values[0].substring(4,6)+"-"+c_values[0].substring(6, c_values[0].length());
                try {
                    preparedStatement.setString(1,codes[i].substring(codes[i].length()-3));
                    preparedStatement.setString(2,descriptions[i]);
                    preparedStatement.setDate(3, new Date(formatter.parse(currencyDate).getTime()));
                    preparedStatement.setInt(4, Integer.parseInt("1"));
                    preparedStatement.setDouble(5, Double.parseDouble(c_values[i].replace(',','.')));
                    preparedStatement.addBatch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
