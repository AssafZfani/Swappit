package talaviassaf.swappit.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

@SuppressWarnings("unused")

public class InitVouchers extends Service {

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    /*

    private DatabaseReference brandsRef, categoriesRef, vouchersRef;
    private ValueEventListener brandsListener, categoriesListener, vouchersListener;

    @Override
    public void onCreate() {

        super.onCreate();

        brandsRef = Application.brandsRef;

        categoriesRef = Application.categoriesRef;

        vouchersRef = Application.vouchersRef;

        brandsRef.addValueEventListener(brandsListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChildren()) {

                    InputStream is = null;

                    try {

                        is = getBaseContext().getAssets().open("Brands.json");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {

                        JSONObject obj = new JSONObject(loadJSONFromAsset(is));

                        JSONArray jsonArray = obj.getJSONArray("Brands");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);

                            JSONArray typesArray = object.getJSONArray("types");

                            ArrayList<Integer> types = new ArrayList<>();

                            for (int j = 0; j < typesArray.length(); j++)
                                types.add(typesArray.getInt(j));

                            brandsRef.push().setValue(new Brand(

                                    object.getString("brand"),
                                    object.getString("hebrew"),
                                    object.getString("image"),
                                    object.getString("strip"),
                                    i + 1,
                                    types
                            ));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        categoriesRef.addValueEventListener(categoriesListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChildren()) {

                    InputStream is = null;

                    try {

                        is = getBaseContext().getAssets().open("Categories.json");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {

                        JSONObject obj = new JSONObject(loadJSONFromAsset(is));

                        JSONArray jsonArray = obj.getJSONArray("Categories");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);

                            categoriesRef.push().setValue(new Category(

                                    object.getString("hebrew"),
                                    i + 1,
                                    object.getString("type")
                            ));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        vouchersRef.addListenerForSingleValueEvent(vouchersListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChildren()) {

                    InputStream is = null;

                    try {

                        is = getBaseContext().getAssets().open("Vouchers.json");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {

                        JSONObject obj = new JSONObject(loadJSONFromAsset(is));

                        JSONArray jsonArray = obj.getJSONArray("Vouchers");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);

                            DatabaseReference voucher = vouchersRef.push();

                            JSONObject brandObject = object.getJSONObject("brand");

                            ArrayList<Integer> types = new ArrayList<>();

                            JSONArray typesArray = brandObject.getJSONArray("Types");

                            for (int j = 0; j < typesArray.length(); j++)
                                types.add(typesArray.getInt(j));

                            voucher.setValue(new Voucher(

                                    object.getString("address"),
                                    object.getString("barcode"),
                                    new Brand(brandObject.getString("brand"), brandObject.getString("hebrew"),
                                            brandObject.getString("image"), brandObject.getString("strip"), brandObject.getInt("ID"), types),
                                    object.getString("cvv"),
                                    object.getInt("discount"),
                                    object.getString("expDate"),
                                    object.getBoolean("firmProperty"),
                                    voucher.getKey(),
                                    object.getInt("price"),
                                    object.getString("status"),
                                    object.getString("type"),
                                    object.getInt("value")
                            ));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        brandsRef.removeEventListener(brandsListener);

        categoriesRef.removeEventListener(categoriesListener);

        vouchersRef.removeEventListener(vouchersListener);
    }

    private String loadJSONFromAsset(InputStream is) {

        String json = null;

        try {

            int size = is.available();

            byte[] buffer = new byte[size];

            int num = is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return json;
    }

    */
}
