package com.example.koffi;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class AddressFragment extends Fragment {

    String from= "";

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

        if (getArguments()!=null)
            from = getArguments().getString("from");

        //backPress
        if (from.equals("Other")) {
            OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
                @Override
                public void handleOnBackPressed() {
                    Bundle bundle = new Bundle();
                    bundle.putString("back","other");
                    Navigation.findNavController(view).navigate(R.id.action_global_mainFragment,bundle);
                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
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
        LinearLayout addHome = view.findViewById(R.id.address_addhome);
        addHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("type","Nhà");
                Navigation.findNavController(getView()).navigate(R.id.action_addressFragment2_to_addAddressFragment,bundle);
            }
        });

        LinearLayout addCompany = view.findViewById(R.id.address_addcompany);
        addCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("type","Công ty");
                Navigation.findNavController(getView()).navigate(R.id.action_addressFragment2_to_addAddressFragment,bundle);
            }
        });
        LinearLayout addAddress = view.findViewById(R.id.address_addaddress);
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("type","Normal");
                Navigation.findNavController(getView()).navigate(R.id.action_addressFragment2_to_addAddressFragment,bundle);
            }
        });
    }
}