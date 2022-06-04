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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderFragment extends Fragment {

    int orderMethod;
    TextView readyStatusText;
    String orderID;
    FirebaseFirestore db;
    ArrayList<CartItem> cart;
    long total, subtotal, number;
    String receiverName, receiverPhone, address;
    TextView tvName, tvPhone, tvAddress, tvAddressMethod, tvTotal, tvSubtotal, tvNumber, tvOrderID;
    TextView checkTime;

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
            cart = new ArrayList<CartItem>();
            cart = getArguments().getParcelableArrayList("orderItems");
            orderMethod = getArguments().getInt("method");
            orderID = getArguments().getString("orderID");
            total = getArguments().getLong("total");
            subtotal = getArguments().getLong("subtotal");
            number = getArguments().getLong("numberOfItems");
            address = getArguments().getString("address");
            receiverName = getArguments().getString("receiverName");
            receiverPhone = getArguments().getString("receiverPhone");
        }
        //Init
        readyStatusText = view.findViewById(R.id.order_readyText);
        tvName = view.findViewById(R.id.order_name);
        tvName.setText(receiverName);
        tvPhone = view.findViewById(R.id.order_phone);
        tvPhone.setText(receiverPhone);
        tvOrderID = view.findViewById(R.id.order_id);
        tvOrderID.setText(orderID);
        tvAddressMethod = view.findViewById(R.id.order_tvAddress);
        if (orderMethod == 1) tvAddressMethod.setText("Cửa hàng");
        tvAddress = view.findViewById(R.id.order_address);
        tvAddress.setText(address);
        tvNumber = view.findViewById(R.id.order_number);
        tvNumber.setText("(" + number + " món)");
        tvTotal = view.findViewById(R.id.order_total);
        tvTotal.setText(total + "đ");
        tvSubtotal = view.findViewById(R.id.order_subtotal);
        tvSubtotal.setText(subtotal + "đ");
        checkTime = view.findViewById(R.id.order_time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        checkTime.setText(sdf.format(new Date()));

        //Handle order method
        if (orderMethod == 1)
            handleTakeAway();

        //Toolbar
        Toolbar toolbar = view.findViewById(R.id.order_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Cart list


        //Sample data
//        ArrayList<Topping> toppings = new ArrayList<Topping>();
//        toppings.add(new Topping("123","Trân châu hoàng kim",6000L));
//        cart.add(new CartItem("123","Cà phê",2,new Long(35000),"Upsize",toppings,"ít đường"));
//        cart.add(new CartItem("123","Cà phê",2,new Long(35000),"Upsize",toppings,"ít đường"));
//        cart.add(new CartItem("123","Cà phê",2,new Long(35000),"Upsize",toppings,"ít đường"));

        ListView cartList = view.findViewById(R.id.order_cartList);
        CartItemAdapter cartAdapter = new CartItemAdapter(getContext(),cart,false);
        cartList.setAdapter(cartAdapter);
        setListViewHeight(cartList);

        db = FirebaseFirestore.getInstance();
        db.collection("cartItems").whereEqualTo("cartID", orderID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        cart.add(snapshot.toObject(CartItem.class));
                    }
                    cartAdapter.notifyDataSetChanged();
                }
            }
        });
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