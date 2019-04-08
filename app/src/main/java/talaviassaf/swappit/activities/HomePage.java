package talaviassaf.swappit.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import talaviassaf.swappit.Application;
import talaviassaf.swappit.R;
import talaviassaf.swappit.fragments.BackgroundFragments.Container;
import talaviassaf.swappit.fragments.BackgroundFragments.Login;
import talaviassaf.swappit.fragments.BackgroundFragments.Registration;
import talaviassaf.swappit.fragments.MainFragments.Feed;
import talaviassaf.swappit.fragments.MainFragments.PreciousBrands;
import talaviassaf.swappit.fragments.MainFragments.PurchaseVouchers;
import talaviassaf.swappit.fragments.MainFragments.UploadVouchers;
import talaviassaf.swappit.fragments.TicketFragments.Delete;
import talaviassaf.swappit.fragments.UploadFragments.VoucherDetails;
import talaviassaf.swappit.models.Dialog;

public class HomePage extends GPSTracker {

    public static final ButterKnife.Action<View> Appear = new ButterKnife.Action<View>() {

        @Override
        public void apply(@NonNull View view, int index) {

            view.setVisibility(View.VISIBLE);
        }
    };

    public static final ButterKnife.Action<View> Disappear = new ButterKnife.Action<View>() {

        @Override
        public void apply(@NonNull View view, int index) {

            view.setVisibility(View.GONE);
        }
    };

    public static boolean isUnregisteredUser;
    public Fragment[] fragments;
    public Dialog dialog;
    @BindView(R.id.fragmentsViewPager)
    public ViewPager viewPager;
    @BindView(R.id.fragmentsBar)
    BottomNavigationView fragmentsBar;
    @BindView(R.id.badge)
    AppCompatTextView badge;

    private Intent intent;
    private Stack<Integer> fragmentsStack;
    private int fragmentId;

    public static void setupToolBar(final Activity activity, String titleText) {

        View arrow = activity.findViewById(R.id.arrow);

        AppCompatTextView title = activity.findViewById(R.id.mainFragmentTitle);

        arrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                activity.onBackPressed();
            }
        });

        arrow.setVisibility(titleText.startsWith(activity.getString(R.string.homepage_main_fragment_title_part1)) ? View.GONE : View.VISIBLE);

        title.setText(titleText);

        title.setTypeface(titleText.contains(activity.getString(R.string.homepage_main_fragment_title_part2)) ? Typeface.MONOSPACE : Typeface.SERIF);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case 0:
                if (resultCode == RESULT_OK)
                    fragmentsBar.setSelectedItemId(R.id.purchaseVouchers);
                break;
            case 1:
                if (resultCode == RESULT_OK)
                    ((Feed) fragments[0]).filterList(data.getStringExtra("Address"), data.getStringArrayListExtra("VouchersTypes"),
                            data.getIntegerArrayListExtra("Categories"));
                break;
            case 2:
                if (resultCode == RESULT_OK) {

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Uri uri = data.getData();

                    if (uri != null) {

                        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);

                        if (cursor != null) {

                            cursor.moveToFirst();

                            Application.sharedPreferences.edit().putString(intent.getBooleanExtra("IsVoucherCapture", false) ?
                                    "Image" : "ProfilePicture", cursor.getString(cursor.getColumnIndex(filePathColumn[0]))).apply();

                            cursor.close();
                        }
                    }
                }
            case 3:
                if (resultCode == RESULT_OK && intent.getBooleanExtra("IsVoucherCapture", false))
                    for (Fragment fragment : getSupportFragmentManager().getFragments())
                        if (fragment instanceof VoucherDetails)
                            ((VoucherDetails) fragment).voucherCapture();
                break;
            default:
                if (resultCode == RESULT_OK && isUnregisteredUser) {

                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                    if (result != null && result.isSuccess()) {

                        GoogleSignInAccount account = result.getSignInAccount();

                        if (account != null) {

                            LoadingPage.firebaseUser.updateProfile(new UserProfileChangeRequest.Builder().setPhotoUri(account.getPhotoUrl())
                                    .setDisplayName(account.getDisplayName()).build());

                            String email = account.getEmail();

                            if (email != null)
                                LoadingPage.firebaseUser.updateEmail(email);

                            LoadingPage.firebaseUser.linkWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null))
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            startActivity(new Intent(HomePage.this, HomePage.class));
                                        }
                                    });
                        }
                    } else
                        Login.callbackManager.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

        onTouchEvent(null);

        int currentItem = viewPager.getCurrentItem();

        if (currentItem == 0 || fragmentsStack.size() == 1)
            dialog = new Dialog(this, "Exit");
        else {
            switch (currentItem) {

                case 2:
                    if (fragments[2] instanceof UploadVouchers) {

                        UploadVouchers uploadVouchers = (UploadVouchers) fragments[2];

                        int uploadCurrentItem = uploadVouchers.viewPager.getCurrentItem();

                        if (uploadCurrentItem != 4) {

                            uploadVouchers.moveToPreviousFragment(uploadCurrentItem);

                            return;

                        } else {

                            if (!intent.getBooleanExtra("UploadSucceeded", true) &&
                                    (Application.sharedPreferences.getString("VoucherType", null) != null ||
                                            !Application.sharedPreferences.getString("Brand", "").isEmpty())) {

                                fragmentId = getFragmentIdByItem(fragmentsStack.get(fragmentsStack.size() - 2));

                                dialog = new Dialog(this, "SaveData");

                                return;
                            }
                        }
                    } else {

                        ViewPager containerViewPager = ((Container) fragments[2]).viewPager;

                        if (containerViewPager.getCurrentItem() != 0) {

                            containerViewPager.setCurrentItem(0);

                            return;
                        }
                    }
                    break;
                case 3:
                    PurchaseVouchers purchaseVouchers = (PurchaseVouchers) fragments[3];

                    int purchaseCurrentItem = purchaseVouchers.viewPager.getCurrentItem();

                    if (purchaseVouchers.fragments[1] instanceof Container) {

                        ViewPager containerViewPager = ((Container) purchaseVouchers.fragments[1]).viewPager;

                        if (containerViewPager.getCurrentItem() != 0) {

                            containerViewPager.setCurrentItem(0);

                            return;
                        }
                    }
                    if (purchaseCurrentItem != 3) {

                        purchaseVouchers.moveToPreviousFragment(purchaseCurrentItem);

                        return;
                    }
                    break;
                case 4:
                    ViewPager containerViewPager = ((Container) fragments[4]).viewPager;

                    if (containerViewPager.getCurrentItem() != 0) {

                        containerViewPager.setCurrentItem(0);

                        return;
                    }
                    break;
            }

            fragmentsStack.pop();

            fragmentsBar.setSelectedItemId(getFragmentIdByItem(fragmentsStack.lastElement()));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);

        ButterKnife.bind(this);

        fragments = new Fragment[5];

        intent = getIntent();

        String email = LoadingPage.firebaseUser.getEmail();

        isUnregisteredUser = email == null || email.isEmpty();

        fragmentsStack = new Stack<>();

        setupHomePage();
    }

    @Override
    public void onResume() {

        super.onResume();

        updateBadges();
    }

    public int getCurrentDisplayedFragment() {

        return viewPager.getCurrentItem();
    }

    private int getFragmentIdByItem(int item) {

        switch (item) {

            case 0:
                return R.id.feed;
            case 1:
                return R.id.preciousBrands;
            case 2:
                return R.id.uploadVouchers;
            case 3:
                return R.id.purchaseVouchers;
            case 4:
                return R.id.personalZone;
        }

        return -1;
    }

    private void setupFragmentBar(int checkedId) {

        int drawable, item, title;

        switch (checkedId) {

            case R.id.feed:
                drawable = R.drawable.feed;
                item = 0;
                title = R.string.homepage_main_fragment_title_feed;
                break;
            case R.id.preciousBrands:
                drawable = R.drawable.precious_brands;
                item = 1;
                title = R.string.homepage_main_fragment_title_precious_brands1;
                break;
            case R.id.uploadVouchers:
                drawable = R.drawable.upload_vouchers;
                item = 2;
                title = isUnregisteredUser ? R.string.homepage_main_fragment_title_registration : R.string.homepage_main_fragment_title_upload_vouchers;
                break;
            case R.id.purchaseVouchers:
                drawable = R.drawable.purchase_vouhers;
                item = 3;
                title = R.string.homepage_main_fragment_title_purchase_vouchers;
                break;
            case R.id.personalZone:
                drawable = R.drawable.personal_zone;
                item = 4;
                title = R.string.homepage_main_fragment_title_personal_zone;
                break;
            default:
                drawable = 0;
                item = 5;
                title = 0;
                break;
        }

        fragmentsBar.setBackgroundResource(drawable);

        String titleText = getString(title);

        setupToolBar(this, item == 1 ? titleText + (Application.sharedPreferences.getBoolean("IsFirstTime", true) ? "\n" +
                getString(R.string.homepage_main_fragment_title_precious_brands2) : "") : titleText);

        viewPager.setCurrentItem(item);

        if (fragmentsStack.contains(item))
            fragmentsStack.remove(fragmentsStack.indexOf(item));

        fragmentsStack.push(item);
    }

    private void setupHomePage() {

        if (!isUnregisteredUser)
            Registration.changeProfilePicture((View) viewPager.getParent(), R.id.profilePicture);

        fragmentsBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                fragmentId = menuItem.getItemId();

                if (fragmentId == fragmentsBar.getSelectedItemId() || !(viewPager.getCurrentItem() == 2 &&
                        (Application.sharedPreferences.getString("VoucherType", null) != null ||
                                !Application.sharedPreferences.getString("Brand", "").isEmpty()) &&
                        !intent.getBooleanExtra("UploadSucceeded", true))) {

                    setupFragmentBar(fragmentId);

                    return true;

                } else if (dialog == null || !dialog.isShowing())
                    dialog = new Dialog(HomePage.this, "SaveData");

                return false;
            }
        });

        viewPager.setOffscreenPageLimit(5);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {

                switch (position) {

                    case 0:
                        return fragments[0] = Feed.newInstance();
                    case 1:
                        return fragments[1] = PreciousBrands.newInstance();
                    case 2:
                        return fragments[2] = isUnregisteredUser ? Container.newInstance(2) : UploadVouchers.newInstance();
                    case 3:
                        return fragments[3] = PurchaseVouchers.newInstance();
                    default:
                        return fragments[4] = Container.newInstance(3);
                }
            }

            @Override
            public int getCount() {

                return 5;
            }
        });

        fragmentsBar.setSelectedItemId(Application.sharedPreferences.getBoolean("IsFirstTime", true) ? R.id.preciousBrands : R.id.feed);

        Application.sharedPreferences.edit().putBoolean("IsFirstTime", false).apply();
    }

    public void updateBadges() {

        int cartVouchersCount = LoadingPage.user.getCartVouchers().size();

        badge.setText(String.valueOf(cartVouchersCount));

        badge.setVisibility(cartVouchersCount == 0 ? View.GONE : View.VISIBLE);
    }

    public void camera(View view) {

        intent.putExtra("IsVoucherCapture", view.getId() == R.id.voucherCapture);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            if (!(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

                return;
            }

        dialog = new Dialog(this, "Camera");
    }

    public void cancel(View view) {

        String dialogType = dialog.getDialogType();

        if (dialogType.equalsIgnoreCase("Camera"))
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 2);
        else if (dialogType.equalsIgnoreCase("SaveData"))
            fragmentsBar.setSelected(false);

        dialog.dismiss();
    }

    public void confirm(View view) {

        switch (dialog.getDialogType()) {

            case "Camera":
                boolean isVoucher = intent.getBooleanExtra("IsVoucherCapture", false);

                File image = null;

                try {

                    image = File.createTempFile(isVoucher ? "voucher" : "profile",
                            ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));

                } catch (IOException e) {

                    e.printStackTrace();
                }

                if (image != null) {

                    Application.sharedPreferences.edit().putString(isVoucher ? "Image" : "ProfilePicture", image.getAbsolutePath()).apply();

                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT,
                            FileProvider.getUriForFile(this, "talaviassaf.swappit.FileProvider", image)), 3);
                }
                break;
            case "Delete":
                Delete.delete(this);
                break;
            case "Exit":
                ActivityCompat.finishAffinity(this);
                break;
            case "SaveData":
                setupFragmentBar(fragmentId);
                break;
        }

        dialog.dismiss();
    }
}
