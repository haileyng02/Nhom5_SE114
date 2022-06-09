package com.example.koffi.fragment.other;

import static com.example.koffi.FunctionClass.setListViewHeight;

import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.koffi.R;
import com.example.koffi.adapter.OrderAdapter;
import com.example.koffi.models.Order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OrderHistoryFragment extends Fragment {

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    public static OrderHistoryFragment newInstance(String param1, String param2) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.history_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        ArrayList<Order> orderArray = new ArrayList<Order>();

        //Sample data
        Date date = Calendar.getInstance().getTime();
        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,5,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));
        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,4,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));
        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,5,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));
        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,2,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));

        ListView listView = view.findViewById(R.id.historyLv);
        OrderAdapter orderAdapter = new OrderAdapter(getContext(),orderArray);
        listView.setAdapter(orderAdapter);
        setListViewHeight(listView);
        orderAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setListViewHeight(listView);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Order order = (Order) listView.getItemAtPosition(i);
                if (order.status==4 || order.status==5)
                    Navigation.findNavController(getView()).navigate(R.id.action_orderHistoryFragment_to_orderDetailFragment2);
                else {
                    Navigation.findNavController(getView()).navigate(R.id.action_orderHistoryFragment_to_orderFragment);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater inflater1 = getActivity().getMenuInflater();
        inflater1.inflate(R.menu.order_history_option_menu,menu);
    }
}