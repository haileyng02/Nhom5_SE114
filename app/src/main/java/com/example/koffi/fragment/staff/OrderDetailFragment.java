package com.example.koffi.fragment.staff;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.koffi.R;
import com.example.koffi.dialog.CancelOrderDialog;
import com.example.koffi.dialog.DialogLogOut;

public class OrderDetailFragment extends Fragment {

    TextView title;

    public OrderDetailFragment() {
        // Required empty public constructor
    }

    public static OrderDetailFragment newInstance(String param1, String param2) {
        OrderDetailFragment fragment = new OrderDetailFragment();
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
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.orderdetail_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        //Init
        Button cancelBtn = view.findViewById(R.id.orderdetail_cancelBtn);
        title = view.findViewById(R.id.order_title);

        //Setting
        FragmentManager fm = getParentFragmentManager();
        int count = fm.getBackStackEntryCount();
        switch (Navigation.findNavController(getView()).getPreviousBackStackEntry().getDestination().getId()) {
            case R.id.orderHistoryFragment:
                System.out.println("ten ne");
        }

        //Cancel order
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelOrderDialog cancelDialog = new CancelOrderDialog(getContext());
                cancelDialog.show();
            }
        });
    }
    private void ClientSideSetting() {
        title.setText("Đơn hàng của bạn");
    }
}