package talaviassaf.swappit.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import talaviassaf.swappit.Application;

@SuppressWarnings("unused")

public class User {

    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final DatabaseReference reference = user == null ? null :
            FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
    private ArrayList<String> cartVouchers, deletedVouchers, myVouchers, preciousBrands;

    User() {

    }

    public User(ArrayList<String> cartVouchers, ArrayList<String> deletedVouchers,
                ArrayList<String> myVouchers, ArrayList<String> preciousBrands) {

        this.cartVouchers = cartVouchers;
        this.deletedVouchers = deletedVouchers;
        this.myVouchers = myVouchers;
        this.preciousBrands = preciousBrands;
    }

    public void addVoucherToCart(String voucherId) {

        cartVouchers = getCartVouchers();

        cartVouchers.add(voucherId);

        reference.setValue(this);
    }

    public boolean areNotThereDeletedVouchers() {

        return deletedVouchers == null || deletedVouchers.isEmpty();
    }

    private void cleanIds(final int type, final ArrayList<String> vouchers) {

        if (vouchers != null) {

            for (int i = 0; i < vouchers.size(); i++) {

                final String voucherId = vouchers.get(i);

                if (voucherId != null) {

                    Application.vouchersRef.child(voucherId).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (!dataSnapshot.exists())
                                vouchers.remove(voucherId);

                            switch (type) {

                                case 0:
                                    cartVouchers = vouchers;
                                    break;
                                case 1:
                                    deletedVouchers = vouchers;
                                    break;
                                default:
                                    myVouchers = vouchers;
                                    break;
                            }

                            reference.setValue(User.this);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }
    }

    public void cleanListsFromIrrelevantIds() {

        cleanIds(0, cartVouchers);
        cleanIds(1, deletedVouchers);
        cleanIds(2, myVouchers);
    }

    public void deleteVoucher(String voucherId) {

        deletedVouchers = getDeletedVouchers();

        deletedVouchers.add(voucherId);

        if (isVoucherExistsInCart(voucherId))
            cartVouchers.remove(voucherId);

        reference.setValue(this);
    }

    public void deleteVoucherFromCart(String voucherId) {

        cartVouchers = getCartVouchers();

        cartVouchers.remove(voucherId);

        reference.setValue(this);
    }

    public void deleteVoucherFromMyVouchers(String voucherId) {

        myVouchers = getMyVouchers();

        myVouchers.add(voucherId);

        reference.setValue(this);
    }

    public void emptyCart() {

        cartVouchers = null;

        reference.setValue(this);
    }

    public ArrayList<String> getCartVouchers() {

        return cartVouchers == null ? new ArrayList<String>() : cartVouchers;
    }

    public ArrayList<String> getDeletedVouchers() {

        return deletedVouchers == null ? new ArrayList<String>() : deletedVouchers;
    }

    public ArrayList<String> getMyVouchers() {

        return myVouchers == null ? new ArrayList<String>() : myVouchers;
    }

    public void setMyVouchers(ArrayList<String> myVouchers) {

        this.myVouchers = myVouchers;
    }

    public ArrayList<String> getPreciousBrands() {

        return preciousBrands == null ? new ArrayList<String>() : preciousBrands;
    }

    public void setPreciousBrands(ArrayList<String> preciousBrands) {

        this.preciousBrands = preciousBrands;

        reference.setValue(this);
    }

    public boolean isVoucherExistsInCart(String voucherId) {

        return cartVouchers != null && cartVouchers.contains(voucherId);
    }
}
