package com.example.koffi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class StoreFragment extends Fragment {

    ListView listView;
    ArrayList<Store> storeArray;
    StoreAdapter adapter;

    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Custom toolbar
        Toolbar toolbar = view.findViewById(R.id.store_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        listView = view.findViewById(R.id.storesListView);
        storeArray = new ArrayList<Store>();

        //Dữ liệu mẫu
        Store store1 = new Store("store-1","Tầng 50 Bitexco Tower, 2 Hải Triều, Phường Bến Nghé, Quận 1","store_1","0344242643");
        Store store2 = new Store("store-2","10/16 Đoàn Thị Điểm, phường 1, quận Phú Nhuận","store_2","0932708316");
        storeArray.add(store1);
        storeArray.add(store2);

        adapter = new StoreAdapter(getContext(),storeArray);
        listView.setAdapter(adapter);

        //Store Detail Bottom Sheet
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_storedetail,
                        (LinearLayout)view.findViewById(R.id.store_bottomsheet));
                bottomSheetDialog.setContentView(bottomSheetView);
                //Assign data

                //Show dialog
                bottomSheetDialog.show();
            }
        });
    }
}