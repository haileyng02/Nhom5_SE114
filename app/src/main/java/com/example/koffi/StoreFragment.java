package com.example.koffi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Locale;

public class StoreFragment extends Fragment {

    ListView listView;
    ArrayList<Store> storeArray;
    StoreAdapter adapter;

    EditText editText;

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
        //Search store
        editText = view.findViewById(R.id.addressEdit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });


        //Dữ liệu mẫu
        Store store1 = new Store("store-1","Tầng 50 Bitexco Tower, 2 Hải Triều, Phường Bến Nghé, Quận 1","store_1","0344242643");
        Store store2 = new Store("store-2","10/16 Đoàn Thị Điểm, phường 1, quận Phú Nhuận","store_2","0932708316");
        Store store3 = new Store("store-3","139/23 Đinh Bộ Lĩnh, phường 26, quận Bình Thạnh","store_3","0975305060");
        Store store4 = new Store("store-4","573/10 Sư Vạn Hạnh, phường 13, quận 10","store_4","0528325771");
        Store store5 = new Store("store-5","21/42 Giang Văn Minh, quận Ba Đình, Hà Nội","store_5","09374671294");
        Store store6 = new Store("store-6","37 Quang Trung, quận Hoàn Kiếm, Hà Nội","store_6","0543678129");
        Store store7 = new Store("store-7","21 Xuân Diệu, quận Tây Hồ, Hà Nội","store_7","0743768208");
        Store store8 = new Store("store-8","36 Ấu Triệu, quận Hoàn Kiếm, Hà Nội","store_8","0375638120");
        storeArray.add(store1);
        storeArray.add(store2);
        storeArray.add(store3);
        storeArray.add(store4);
        storeArray.add(store5);
        storeArray.add(store6);
        storeArray.add(store7);
        storeArray.add(store8);

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

    private void filter (String text) {
        ArrayList<Store> filteredList = new ArrayList<>();

        for (Store store : storeArray) {
            if (store.address.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(store);
            }
        }
        adapter.filteredList(filteredList);
    }
}