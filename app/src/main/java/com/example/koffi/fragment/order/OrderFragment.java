package com.example.koffi.fragment.order;

import static com.example.koffi.FunctionClass.setListViewHeight;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.koffi.models.CartItem;
import com.example.koffi.adapter.CartItemAdapter;
import com.example.koffi.R;
import com.example.koffi.models.Topping;

import java.util.ArrayList;

public class OrderFragment extends Fragment {

    int orderMethod;
    TextView readyStatusText;
    String orderID;

    public OrderFragment() {
        // Required empty public constructor
    }

    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
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
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get arguments
        if (getArguments()!=null) {
            orderMethod = getArguments().getInt("method");
            orderID = getArguments().getString("orderID");
        }
        //Init
        readyStatusText = view.findViewById(R.id.order_readyText);

        //Handle order method
        if (orderMethod == 1)
            handleTakeAway();

        //Toolbar
        Toolbar toolbar = view.findViewById(R.id.order_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Cart list
        ArrayList<CartItem> cart = new ArrayList<CartItem>();
        //Sample data
        ArrayList<Topping> toppings = new ArrayList<Topping>();
        toppings.add(new Topping("123","Trân châu hoàng kim",6000L));
        cart.add(new CartItem("123","Cà phê",2,new Long(35000),"Upsize",toppings,"ít đường"));
        cart.add(new CartItem("123","Cà phê",2,new Long(35000),"Upsize",toppings,"ít đường"));
        cart.add(new CartItem("123","Cà phê",2,new Long(35000),"Upsize",toppings,"ít đường"));

        ListView cartList = view.findViewById(R.id.order_cartList);
        CartItemAdapter cartAdapter = new CartItemAdapter(getContext(),cart,false);
        cartList.setAdapter(cartAdapter);
        setListViewHeight(cartList);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuInflater optionInflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.order_option_menu,menu);
        menu.removeItem(R.id.action_search);
        menu.removeItem(R.id.action_favorite);
    }
    public void handleTakeAway(){
        readyStatusText.setText("Đã sẵn sàng");
    }
}