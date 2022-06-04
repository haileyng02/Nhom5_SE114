package com.example.koffi.fragment.other;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.koffi.models.Address;
import com.example.koffi.adapter.AddressAdapter;
import com.example.koffi.activity.LoginActivity;
import com.example.koffi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AddressFragment extends Fragment {

    String from = "";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    AddressAdapter adapter;
    ArrayList<Address> addressList ;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public AddressFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AddressFragment newInstance(String param1, String param2) {
        AddressFragment fragment = new AddressFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_address, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //define
        ListView listView = view.findViewById(R.id.listviewAddress);
        LinearLayout addAddress = view.findViewById(R.id.address_addaddress);
        LinearLayout addCompany = view.findViewById(R.id.address_addcompany);
        LinearLayout addHome = view.findViewById(R.id.address_addhome);
        TextView addressHome = view.findViewById(R.id.ViewDCNha);
        TextView addressCompany = view.findViewById(R.id.ViewDCCTy);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        if (getArguments() != null)
            from = getArguments().getString("from");

        //backPress
        if (from.equals("Other")) {
            /*OnBackPressedCallback callback = new OnBackPressedCallback(true *//* enabled by default *//*) {
                @Override
                public void handleOnBackPressed() {
                    Bundle bundle = new Bundle();
                    bundle.putString("back","other");
                    Navigation.findNavController(view).navigate(R.id.action_global_mainFragment,bundle);
                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);*/
        }

        //Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.address_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        //Navigate to add address
        addHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addressHome.getText().toString().equals("Thêm địa chỉ nhà"))
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "Nhà");
                    Navigation.findNavController(getView()).navigate(R.id.action_addressFragment2_to_addAddressFragment, bundle);
                }
                else {
                    editor.putString("tendc","Nhà");
                    editor.putString("dc",addressHome.getText().toString());
                    editor.apply();

                    Navigation.findNavController(getView()).popBackStack();
                }
            }

        });
        addCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addressCompany.getText().toString().equals("Thêm địa chỉ công ty"))
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "Công ty");
                    Navigation.findNavController(getView()).navigate(R.id.action_addressFragment2_to_addAddressFragment, bundle);
                }
                else {
                    editor.putString("tendc", "Công ty");
                    editor.putString("dc", addressCompany.getText().toString());
                    editor.apply();

                    Navigation.findNavController(getView()).popBackStack();
                }
            }
        });
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("type","Normal");
                if (from.equals("checkout"))
                    bundle.putString("from","checkoutNew");
                Navigation.findNavController(getView()).navigate(R.id.action_addressFragment2_to_addAddressFragment,bundle);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listView.invalidateViews();
                Address a=(Address) listView.getItemAtPosition(i);

                editor.putString("tendc",a.getName());
                editor.putString("dc",a.getAddress());
                editor.putInt("orderMethod",0);
                editor.apply();

                if (from.equals("checkout")) {
                    Navigation.findNavController(getView()).popBackStack();
                }
                else if (from.equals("checkoutNew")) {
                    Navigation.findNavController(getView()).popBackStack();
                    Navigation.findNavController(getView()).popBackStack();
                    Navigation.findNavController(getView()).popBackStack();
                }
                /*else if (from.equals("Other")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "Normal");
                    Navigation.findNavController(getView()).navigate(R.id.action_addressFragment2_to_addAddressFragment, bundle);
                }*/
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString("back","menu");
                    Navigation.findNavController(getView()).navigate(R.id.action_global_mainFragment,bundle);
                }
            }
        });

        //Show address
        if (user == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            Toast.makeText(getContext(), "Bạn chưa đăng nhâp. Mời bạn đăng nhập!", Toast.LENGTH_LONG).show();
        } else {
            db.collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    if (document.exists()) {

                                        if (document.get("Nhà.Địa chỉ") != null)
                                            addressHome.setText((CharSequence) document.get("Nhà.Địa chỉ"));
                                        if (document.get("Công ty.Địa chỉ") != null)
                                            addressCompany.setText((CharSequence) document.get("Công ty.Địa chỉ"));

                                    }
                                }
                            }
                        }
                    });
            addressList=new ArrayList<Address>();
            adapter=new AddressAdapter(getContext(),addressList);
            listView.setAdapter(adapter);
            db.collection("users").document(user.getUid()).collection("SaveAddress")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            addressList.add(new Address(document.getString("name"), document.getString("address")));
                            adapter=new AddressAdapter(getContext(),addressList);
                            listView.setAdapter(adapter);
                            setListViewHeight(listView);
                            adapter.notifyDataSetChanged();

                        }
                    }
                }
            });

        }
    }
    public void setListViewHeight(ListView listview) {
        ListAdapter listadp = listview.getAdapter();
        if (listadp != null) {
            int totalHeight = 0;
            for (int i = 0; i < listadp.getCount(); i++) {
                View listItem = listadp.getView(i, null, listview);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listview.getLayoutParams();
            params.height = totalHeight + (listview.getDividerHeight() * (listadp.getCount() - 1));
            listview.setLayoutParams(params);
        }
    }

}
