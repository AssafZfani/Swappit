package talaviassaf.swappit.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import talaviassaf.swappit.R;
import talaviassaf.swappit.activities.ShowBy;

public class CleanEditText extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    private final int id;

    public CleanEditText(Context context) {

        super(context);

        this.id = getId();

        init();
    }

    public CleanEditText(Context context, AttributeSet attrs) {

        super(context, attrs);

        this.id = getId();

        init();
    }

    private void init() {

        boolean isSearchId = id == R.id.feedSearchBrands || id == R.id.brandsSearchBrands || id == R.id.locationSearch || id == R.id.brandsList;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            setCompoundDrawablesRelativeWithIntrinsicBounds(isSearchId ? R.drawable.search : 0, 0, 0, 0);
        else
            setCompoundDrawablesWithIntrinsicBounds(isSearchId ? R.drawable.search : 0, 0, 0, 0);

        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Drawable drawable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? getCompoundDrawablesRelative()[2] : getCompoundDrawables()[2];

                    if (drawable != null) {

                        float density = getResources().getDisplayMetrics().density;

                        View parentView = ((View) getParent().getParent().getParent());

                        float padding = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? parentView.getPaddingStart() : parentView.getPaddingLeft();

                        if (id == R.id.value)
                            padding = 180 * density - getWidth() / 2 - padding;

                        if (motionEvent.getRawX() - padding <= drawable.getBounds().width() * density) {

                            setText("");

                            if (id == R.id.locationSearch)
                                ((ShowBy) getContext()).clean();
                            else if (id == R.id.brandsList)
                                setText("");

                            if (id != R.id.expDate)
                                performClick();

                            return true;
                        }
                    }
                }

                return false;
            }
        });
    }

    public static class CleanTextWatcher implements TextWatcher {

        private final EditText editText;
        private final boolean isSearchId;

        protected CleanTextWatcher(CleanEditText editText) {

            this.editText = editText;

            int id = editText.getId();

            this.isSearchId = id == R.id.feedSearchBrands || id == R.id.brandsSearchBrands || id == R.id.locationSearch || id == R.id.brandsList;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                editText.setCompoundDrawablesRelativeWithIntrinsicBounds(isSearchId ? R.drawable.search : 0, 0, R.drawable.clean, 0);
            else
                editText.setCompoundDrawablesWithIntrinsicBounds(isSearchId ? R.drawable.search : 0, 0, R.drawable.clean, 0);
        }

        @Override
        public void afterTextChanged(Editable s) {

            if (s.length() == 0) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(isSearchId ? R.drawable.search : 0, 0, 0, 0);
                else
                    editText.setCompoundDrawablesWithIntrinsicBounds(isSearchId ? R.drawable.search : 0, 0, 0, 0);
            }
        }
    }
}
