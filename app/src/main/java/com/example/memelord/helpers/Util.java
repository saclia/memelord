package com.example.memelord.helpers;

import android.os.Bundle;
import android.text.format.DateUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
    private static String[] suffix = new String[]{"","k", "m", "b", "t"};
    private static int MAX_LENGTH = 4;

    public interface FragmentLoader {
        void loadFragment(Fragment fragment, @Nullable Bundle bundle);
        void showProgressBar();
        void hideProgressBar();
    }

    public static String getRelativeTimeAgo(Date createdAt) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createdAt.toString()).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static String formatNumber(double number) {
        String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        while(r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")){
            r = r.substring(0, r.length()-2) + r.substring(r.length() - 1);
        }
        return r;
    }
}
