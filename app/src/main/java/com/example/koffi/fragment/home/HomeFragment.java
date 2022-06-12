package com.example.koffi.fragment.home;
import static com.example.koffi.FunctionClass.setListViewHeight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.koffi.activity.LoginActivity;
import com.example.koffi.activity.StaffActivity;
import com.example.koffi.adapter.MenuItemAdapter;
import com.example.koffi.adapter.SliderAdapter;
import com.example.koffi.models.Item;
import com.example.koffi.models.Order;
import com.example.koffi.R;
import com.example.koffi.models.User;
import com.example.koffi.models.SliderItem;
import com.example.koffi.models.Store;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager2;
    private Handler sliderHandler;
    ListView listView;
    ArrayList<Item> itemArray;
    MenuItemAdapter adapter;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    String userName;
    FirebaseFirestore db;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
//        userName = null;
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        if (accessToken != null) {
//            GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
//                @Override
//                public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
//                    try {
//                        Log.d("Demo: ", jsonObject.toString());
//                        userName = jsonObject.getString("name");
//                        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Chào, " + userName + " \uD83D\uDC4B");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            Bundle bundle = new Bundle();
//            bundle.putString("fields", "id, name");
//            request.setParameters(bundle);
//            request.executeAsync();
//        }

        //Custom toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Chào bạn mới \uD83D\uDC4B");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setLogo(R.drawable.sun);

        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (!documentSnapshot.exists()) {
                                String email = user.getEmail();
                                String phone = user.getPhoneNumber();
                                String username = user.getDisplayName();
                                db.collection("users").document(user.getUid()).set(new User(username, email, phone));
                            }
                            if (documentSnapshot.getString("Ten") != null)
                            ((AppCompatActivity)getActivity()).getSupportActionBar()
                                    .setTitle("Chào " + documentSnapshot.getString("Ten") + " \uD83D\uDC4B");
                        }
            });
        }
        //Handle loginBtn
        Button loginBtn = view.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        //Create cart for user if not exists
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            db = FirebaseFirestore.getInstance();
            Query query = db.collection("order")
                    .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .whereEqualTo("status", 0);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult().size() == 0) {
                        System.out.println("Cart not exists");
                        Order order = new Order(
                                FirebaseAuth.getInstance().getCurrentUser().getUid(), 0
                        );
                        db.collection("order")
                                .add(order);
                    }
                }
            });
        }

        //Home frag main content
        //Init
        viewPager2 = view.findViewById(R.id.home_viewpager);
        listView = view.findViewById(R.id.featuredList);
        itemArray = new ArrayList<Item>();
        adapter = new MenuItemAdapter(getContext(),itemArray);
        LinearLayout delivery = view.findViewById(R.id.home_delivery);
        LinearLayout takeaway = view.findViewById(R.id.home_takeaway);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        //Banner Slider
        ArrayList<SliderItem> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItem(R.drawable.slide_6));
        sliderItems.add(new SliderItem(R.drawable.slide_2));
        sliderItems.add(new SliderItem(R.drawable.slide_3));
        sliderItems.add(new SliderItem(R.drawable.slide_4));
        sliderItems.add(new SliderItem(R.drawable.slide_5));
        sliderItems.add(new SliderItem(R.drawable.slide_6));
        sliderItems.add(new SliderItem(R.drawable.slide_7));
        sliderItems.add(new SliderItem(R.drawable.slide_8));
        viewPager2.setAdapter(new SliderAdapter(sliderItems, viewPager2));

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);

        sliderHandler = new Handler();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(slideRunnable);
                sliderHandler.postDelayed(slideRunnable,3000);
            }
        });

        //Delivery
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToMenu(0);
            }
        });

        //Take away
        takeaway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToMenu(1);
            }
        });

        // Featured list view
        listView.setAdapter(adapter);
        getFeaturedItems();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item item = (Item) listView.getItemAtPosition(i);
                Bundle bundle = new Bundle();
                bundle.putString("bottomsheet",item.id);
            }
        });
    }

    private Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
        }
    };

    private void getFeaturedItems() {
        db.collection("featured-menu").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            Item item = new Item(documentSnapshot.getId(),documentSnapshot.getString("name"),
                                    documentSnapshot.getString("image"),documentSnapshot.getLong("price"),documentSnapshot.getString("description"));
                            itemArray.add(item);

                            if (itemArray.size()==3) {
                                adapter.notifyDataSetChanged();
                                setListViewHeight(listView);
                                return;
                            }
                        }
                    }
                });
    }
    private void navigateToMenu(int method) {
        editor.putInt("orderMethod",method);
        editor.apply();

        Navigation.findNavController(getView()).navigate(R.id.menuFragment);
    }
}