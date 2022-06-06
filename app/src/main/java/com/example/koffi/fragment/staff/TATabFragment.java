package com.example.koffi.fragment.staff;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.koffi.R;
import com.example.koffi.adapter.OrderAdapter;
import com.example.koffi.models.Order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TATabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TATabFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TATabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TATabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TATabFragment newInstance(String param1, String param2) {
        TATabFragment fragment = new TATabFragment();
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
        return inflater.inflate(R.layout.fragment_t_a_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Order> orderArray = new ArrayList<Order>();

        //Sample data
        Date date = Calendar.getInstance().getTime();
        orderArray.add(new Order("123","user123","store123", date,2,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",1));
        orderArray.add(new Order("123","user123","store123", date,3,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",1));
        orderArray.add(new Order("123","user123","store123", date,1,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",1));

        ListView listView = view.findViewById(R.id.deliveryLv);
        OrderAdapter orderAdapter = new OrderAdapter(getContext(),orderArray);
        listView.setAdapter(orderAdapter);
    }
}