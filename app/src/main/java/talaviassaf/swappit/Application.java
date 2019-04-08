package talaviassaf.swappit;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.leakcanary.LeakCanary;

import java.lang.reflect.Field;

public class Application extends android.app.Application {

    public static SharedPreferences sharedPreferences;
    public static DatabaseReference vouchersRef;

    @Override
    public void onCreate() {

        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this))
            return;

        LeakCanary.install(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        vouchersRef = FirebaseDatabase.getInstance().getReference("Vouchers");

        try {

            Typeface typeface = Typeface.createFromAsset(getAssets(), "open_sans.ttf");

            Field staticField = Typeface.class.getDeclaredField("MONOSPACE");

            staticField.setAccessible(true);

            staticField.set(null, typeface);

            typeface = Typeface.createFromAsset(getAssets(), "open_sans_bold.ttf");

            staticField = Typeface.class.getDeclaredField("SERIF");

            staticField.setAccessible(true);

            staticField.set(null, typeface);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
