package com.example.grocery;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.example.grocery.adapter.CategoryAdapter;
import com.example.grocery.adapter.DiscountedProductAdapter;
import com.example.grocery.adapter.RecentlyViewedAdapter;
import com.example.grocery.model.Category;
import com.example.grocery.model.DiscountedProducts;
import com.example.grocery.model.RecentlyViewed;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.rudderstack.android.sdk.core.RudderClient;
import com.rudderstack.android.sdk.core.RudderProperty;
import com.rudderstack.android.sdk.core.RudderTraits;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    RecyclerView discountedRecyclerView, categoryRecyclerView , recentlyViewedRecycler;
    DiscountedProductAdapter discountedProductAdapter;
    RecentlyViewedAdapter recentlyViewedAdapter;
    List<DiscountedProducts> discountedProductsList;
    List<RecentlyViewed> recentlyViewedList;
    CategoryAdapter categoryAdapter;
    List<Category> categoryList;
    ImageView allCategoryImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RudderTraits traits = new RudderTraits();
        String regisId = getRegisId();
        traits.put("regisId", regisId);
        traits.put("address", getAddress());
        traits.put("installedFrom", getInstalledFrom());

        RudderClient.with(this).identify(regisId, traits, null);



        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            PackageManager pm = getApplicationContext().getPackageManager();
                            try {
                                PackageInfo pi = pm.getPackageInfo("com.aixp.amazongrocery", PackageManager.GET_META_DATA);
                                Date d = new Date(pi.firstInstallTime);
                                Date c = Calendar.getInstance().getTime();
                                long diff = c.getTime() - d.getTime();
                                long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diff);

                                System.out.println("Current DATE => " + c);
                                System.out.println("DIFF DATE => " + diffInSec);
                                System.out.println("INSTALL DATE " + d.toString());
                                String applicationState = diffInSec > 180 ? "ALREADY INSTALLED" : "NEWLY INSTALLED";
                                deepLink = pendingDynamicLinkData.getLink();
                                System.out.println("PROMO DATE " + deepLink.toString());
                                System.out.println("STATE DATE " + applicationState);
                                System.out.println("SOURCE DATE " + deepLink.getQueryParameter("source"));
                                write("installedFrom", deepLink.getQueryParameter("source"));
                                RudderProperty p = new RudderProperty();
                                p.putValue("campaignDynamicLinkId", deepLink.getQueryParameter("source"));
                                p.putValue("campaignDynamicLinkMobileAppState", applicationState);
                                RudderClient.with(getApplicationContext()).track("campaignDynamicLink", p);
                            } catch (PackageManager.NameNotFoundException e) {
                                Toast.makeText(getApplicationContext(), "Your device has not installed " , Toast.LENGTH_SHORT).show();
                            }
                        }


                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("fail", "getDynamicLink:onFailure", e);
                    }
                });

        discountedRecyclerView=findViewById(R.id.discountedRecycler);
        //adding data to model
        discountedProductsList =new ArrayList<>();
        discountedProductsList.add(new DiscountedProducts(1,R.drawable.discountberry));
        discountedProductsList.add(new DiscountedProducts(2,R.drawable.discountbrocoli));
        discountedProductsList.add(new DiscountedProducts(3,R.drawable.discountmeat));
        discountedProductsList.add(new DiscountedProducts(4,R.drawable.discountberry));
        discountedProductsList.add(new DiscountedProducts(5,R.drawable.discountbrocoli));
        discountedProductsList.add(new DiscountedProducts(6,R.drawable.discountmeat));
        setDiscountedRecycler(discountedProductsList);

        categoryRecyclerView =findViewById(R.id.catagoryRecycler);
        //adding data to model
        categoryList =new ArrayList<>();
        categoryList.add(new Category(1,R.drawable.ic_veggies));
        categoryList.add(new Category(2,R.drawable.ic_fruits));
        categoryList.add(new Category(3,R.drawable.ic_juce));
        categoryList.add(new Category(4,R.drawable.ic_dairy));
        categoryList.add(new Category(5,R.drawable.ic_meat));
        categoryList.add(new Category(6,R.drawable.ic_fish));
        categoryList.add(new Category(7,R.drawable.ic_egg));
        categoryList.add(new Category(8,R.drawable.ic_drink));
        categoryList.add(new Category(9,R.drawable.ic_desert));
        categoryList.add(new Category(10,R.drawable.ic_salad));
        categoryList.add(new Category(11,R.drawable.ic_cookies));
        categoryList.add(new Category(12,R.drawable.ic_spices));
        setCategoryRecycler();

        allCategoryImageView =findViewById(R.id.allCategoryImage);
        allCategoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AllCategory.class);
                startActivity(intent);
            }
        });

        recentlyViewedRecycler =findViewById(R.id.recently_items);
        //adding data to model
        recentlyViewedList =new ArrayList<>();
        recentlyViewedList.add(new RecentlyViewed("Watermelon","Watermelon has high water content and also provide some fiber.","₹ 80","1","KG",R.drawable.card4,R.drawable.b4));
        recentlyViewedList.add(new RecentlyViewed("Papaya","Papaya has high water content and also provide some fiber.","₹ 30","1","KG",R.drawable.card3,R.drawable.b3));
        recentlyViewedList.add(new RecentlyViewed("strawberry","strawberry has high water content and also provide some fiber.","₹ 85","1","KG",R.drawable.card2,R.drawable.b1));
        recentlyViewedList.add(new RecentlyViewed("Kiwi","Kiwi has high water content and also provide some fiber.","₹ 40","1","Pcs",R.drawable.card1,R.drawable.b2));
        setRecentlyRecycler();
    }

    private void setRecentlyRecycler() {
        RecyclerView.LayoutManager  layoutManager=new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        recentlyViewedRecycler.setLayoutManager(layoutManager);
        recentlyViewedAdapter = new RecentlyViewedAdapter(this,recentlyViewedList);
        recentlyViewedRecycler.setAdapter(recentlyViewedAdapter);
    }

    private String getRegisId() {
        SharedPreferences myPrefs = getSharedPreferences("app_config_aixp", Context.MODE_PRIVATE);
        return myPrefs.getString("regisId", "");
    }

    private String getInstalledFrom() {
        SharedPreferences myPrefs = getSharedPreferences("app_config_aixp", Context.MODE_PRIVATE);
        return myPrefs.getString("installedFrom", "");
    }

    private void write(String key, String value) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("app_config_aixp", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPref.edit();

        //Indicate that the default shared prefs have been set
        ed.putString(key, value);
        ed.commit();
    }

    private String getAddress() {
        SharedPreferences myPrefs = getSharedPreferences("app_config_aixp", Context.MODE_PRIVATE);
        return myPrefs.getString("address", "");
    }

    private void setCategoryRecycler() {
        RecyclerView.LayoutManager layoutManager =new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        categoryRecyclerView.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(this,categoryList);
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setDiscountedRecycler(List<DiscountedProducts> dataList) {
        RecyclerView.LayoutManager layoutManager =new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        discountedRecyclerView.setLayoutManager(layoutManager);
        discountedProductAdapter =new DiscountedProductAdapter(this,dataList);
        discountedRecyclerView.setAdapter(discountedProductAdapter);
    }

}