package talaviassaf.swappit.models;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.view.Window;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import talaviassaf.swappit.R;

public class Dialog extends AppCompatDialog {

    private final Context context;
    private final String dialogType;
    @BindView(R.id.dialogTitle)
    AppCompatTextView dialogTitle;
    @BindView(R.id.confirm)
    AppCompatTextView confirm;
    @BindView(R.id.cancel)
    AppCompatTextView cancel;
    @BindView(R.id.chooseZone)
    View chooseZone;
    @BindView(R.id.submit)
    AppCompatTextView submit;

    public Dialog(Context context, String dialogType) {

        super(context);

        this.context = context;

        this.dialogType = dialogType;

        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.model_dialog);

        ButterKnife.bind(this);

        Window window = getWindow();

        if (window != null)
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        switch (dialogType) {

            case "Camera":
                setDialogTexts(R.string.dialog_camera_picker, R.string.dialog_camera_from_camera, R.string.dialog_camera_from_gallery);
                break;
            case "ContactUs":
                setDialogTexts(R.string.dialog_contact_us_message);
                break;
            case "Delete":
                setDialogTexts(R.string.dialog_delete_message, R.string.dialog_delete_confirm, R.string.dialog_delete_cancel);
                break;
            case "Exit":
                setDialogTexts(R.string.dialog_exit_message, R.string.dialog_confirm, R.string.dialog_cancel);
                break;
            case "MyVouchers":
                setDialogTexts(R.string.dialog_my_vouchers_message);
                break;
            case "PersonalDetails":
                setDialogTexts(R.string.dialog_personal_details_message);
                break;
            case "PreciousBrands":
                setDialogTexts(R.string.dialog_precious_brands_message);
                break;
            case "PurchaseVouchers":
                setDialogTexts(R.string.dialog_purchase_vouchers_message);
                break;
            case "SaveData":
                setDialogTexts(R.string.dialog_save_data_message, R.string.dialog_confirm, R.string.dialog_cancel);
                break;
            case "Soon":
                setDialogTexts(R.string.dialog_soon);
                break;
            case "UploadVouchers":
                setDialogTexts(R.string.dialog_upload_vouchers_message);
                break;
        }
    }

    @OnClick(R.id.submit)
    public void submit() {

        dismiss();
    }

    public String getDialogType() {

        return dialogType;
    }

    private void setDialogTexts(int dialogTitle) {

        this.dialogTitle.setText(context.getString(dialogTitle));

        chooseZone.setVisibility(View.GONE);

        submit.setText(context.getString(R.string.dialog_confirm));
    }

    private void setDialogTexts(int dialogTitle, int confirm, int cancel) {

        this.dialogTitle.setText(context.getString(dialogTitle));

        this.confirm.setText(context.getString(confirm));

        this.cancel.setText(context.getString(cancel));

        submit.setVisibility(View.GONE);
    }
}
