package com.example.resiliencemap.functional.utils;

public class SmsUtil {

    public static String toLatin(String text) {
        if (text == null) return "";
        String[] cyr = {"а","б","в","г","ґ","д","е","є", "ж", "з","и","і","ї","й","к","л","м","н","о","п","р","с","т","у","ф","х","ц","ч","ш","щ","ь","ю","я"};
        String[] lat = {"a","b","v","h","g","d","e","ie","zh","z","y","i","i","i","k","l","m","n","o","p","r","s","t","u","f","kh","ts","ch","sh","shch","","iu","ia"};

        String s = text.toLowerCase();
        for (int i = 0; i < cyr.length; i++)
            s = s.replace(cyr[i], lat[i]);
        return s.toUpperCase();
    }
}
