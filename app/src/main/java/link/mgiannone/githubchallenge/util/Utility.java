package link.mgiannone.githubchallenge.util;

/**
 * Created by michelegiannone on 06/09/16.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;

/**
 * Class which has Utility methods
 *
 */
public class Utility {
    private static Pattern pattern;
    private static Matcher matcher;
    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Validate Email with regular expression
     *
     * @param email
     * @return true for Valid Email and false for Invalid Email
     */
    public static boolean validate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }
    /**
     * Checks for Null String object
     *
     * @param txt
     * @return true for not null and false for null String object
     */
    public static boolean isNotNull(String txt){
        return txt!=null && txt.trim().length()>0 ? true: false;
    }

    /**
     * Change date format to a more readable one
     *
     * @param date
     * @return newly formatted date as a String
     */
    public static String changeDateFormat(String date){

        String dateString = "";

        //this SimpleDateFormat is used to catch the expression as retrieved from MySql datetime field
        SimpleDateFormat simpleDateFormat  = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ITALIAN);
        //this SimpleDateFormat is used to convert the retrieved expression in a more comfortable one with the italian market
        SimpleDateFormat formatterDataFormat = new SimpleDateFormat("dd-MM-yyyy - HH:mm.ss", Locale.ITALIAN);

        try {
            dateString = formatterDataFormat.format(simpleDateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;

    }

    /**
     * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
     */
    public static boolean isNetworkConnected(@NonNull Context context) {

        boolean isConnected = false;

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            isConnected = networkInfo != null
                    && networkInfo.isAvailable()
                    && networkInfo.isConnected();

            return isConnected;

        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());

            return isConnected;
        }

    }

    public static String numberToCurrency(String args) {
        Double number = 0.0;
        NumberFormat format = NumberFormat.getInstance();
        String currency;

        try {
            number = Double.parseDouble(args);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        currency = format.format(number);
        return  currency;
    }

    public static String numberToCurrencyFromDB(Double args) {
        NumberFormat format = NumberFormat.getInstance();
        String currency = "";

        try {
            currency = format.format(args);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        return  currency;
    }

    public static String currencyToNumber(String args) {
        String number = "";

        try {
            String[] arrayString = args.split("\\,");
            for(String s: arrayString){
                number += s;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return number;
    }

}
