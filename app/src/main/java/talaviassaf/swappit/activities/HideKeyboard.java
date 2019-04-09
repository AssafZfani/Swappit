package talaviassaf.swappit.activities;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

@SuppressLint("Registered")

public class HideKeyboard extends AppCompatActivity {

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);

        View view = getCurrentFocus();

        if (inputMethodManager != null && view != null)
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        return true;
    }
}
