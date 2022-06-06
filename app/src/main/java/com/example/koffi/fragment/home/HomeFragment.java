package com.example.koffi.fragment.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.koffi.activity.LoginActivity;
import com.example.koffi.activity.StaffActivity;
import com.example.koffi.models.Order;
import com.example.koffi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
                            if (documentSnapshot.getString("Ten") != null)
                            ((AppCompatActivity)getActivity()).getSupportActionBar()
                                    .setTitle("Chào " + documentSnapshot.getString("Ten") + " \uD83D\uDC4B");
                        }
                    });
        }
//        if (account != null) {
//            userName = account.getDisplayName();
//            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Chào, " + userName + " \uD83D\uDC4B");
//        }
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
    }
}