package es.guiguegon.cabify.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by guillermoguerrero on 13/09/16.
 */
public class Utils {

    private Utils() {
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int getScreenWidth(Activity activity) {
        try {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.x;
        } catch (Exception e) {
            Log.e("[Utils]", "[getScreenWidth]", e);
        }
        return 0;
    }

    public static int getScreenHeight(Activity activity) {
        try {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.y;
        } catch (Exception e) {
            Log.e("[Utils]", "[getScreenHeight]", e);
        }
        return 0;
    }
}
