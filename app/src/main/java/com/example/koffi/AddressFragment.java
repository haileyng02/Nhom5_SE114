package com.example.koffi;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddressFragment extends Fragment {

    String from = "";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    AddressAdapter adapter;
    ArrayList<Address> addressList ;

    public AddressFragment() {

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
        if (getArguments() != null)
            from = getArguments().getString("from");
        //backPress
        if (from.equals("Other")) {
            OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
                @Override
                public void handleOnBackPressed() {
                    Bundle bundle = new Bundle();
                    bundle.putString("back", "other");
                    Navigation.findNavController(view).navigate(R.id.action_global_mainFragment, bundle);
                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
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
        if (from.equals("Other"))
        {
            addHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "Nhà");
                    Navigation.findNavController(getView()).navigate(R.id.action_addressFragment2_to_addAddressFragment, bundle);
                }
            });


            addCompany.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "Công ty");
                        Navigation.findNavController(getView()).navigate(R.id.action_addressFragment2_to_addAddressFragment, bundle);
                    }

            });


        }
        else
        {

            addHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(addressHome.getText().toString().equals("Thêm địa chỉ nhà"))
                    {
                        Bundle bundle = new Bundle();
                        Navigation.findNavController(getView()).navigate(R.id.action_addressFragment2_to_addAddressFragment);
                    }
                    else {
                        FragmentManager fragmentManager = getParentFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString("tendc", "Nhà");
                        bundle.putString("dc", addressHome.getText().toString());
                        fragmentManager.setFragmentResult("addressResult", bundle);
                        Navigation.findNavController(getView()).popBackStack();
                    }
                }

            });
            addCompany.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager=getParentFragmentManager();
                    Bundle bundle=new Bundle();
                    bundle.putString("tendc","Công ty");
                    bundle.putString("dc",addressCompany.getText().toString());
                    fragmentManager.setFragmentResult("addressResult",bundle);
                    Navigation.findNavController(getView()).popBackStack();
                }
            });


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    listView.invalidateViews();
                    FragmentManager fragmentManager=getParentFragmentManager();
                    Bundle bundle=new Bundle();
                    Address a=(Address) listView.getItemAtPosition(i);
                    bundle.putString("tendc",a.getName());
                    bundle.putString("dc",a.getAddress());
                    fragmentManager.setFragmentResult("addressResult",bundle);
                    Navigation.findNavController(getView()).popBackStack();
                    //
                }
            });
        }
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "Normal");
                Navigation.findNavController(getView()).navigate(R.id.action_addressFragment2_to_addAddressFragment, bundle);
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
