package com.example.koffi.fragment.order;

import static com.example.koffi.FunctionClass.setListViewHeight;

import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.koffi.dialog.CancelOrderDialog;
import com.example.koffi.models.CartItem;
import com.example.koffi.adapter.CartItemAdapter;
import com.example.koffi.R;
import com.example.koffi.models.Order;
import com.example.koffi.models.Topping;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderFragment extends Fragment {

    int orderMethod;
    TextView readyStatusText, tvChecking, tvPrepare, tvComplete;
    String orderID;
    FirebaseFirestore db;
    ArrayList<CartItem> cart;
    long total, subtotal, number;
    String receiverName, receiverPhone, address, time;
    TextView tvName, tvPhone, tvAddress, tvAddressMethod, tvTotal, tvSubtotal, tvNumber, tvOrderID;
    TextView checkTime, prepareTime, readyTime, completeTime;
    ImageView imageView;
    TextView state;
    Button cancelBtn;
    String confirm, ready, completed;

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
            if (getArguments().getParcelableArrayList("orderItems") != null)
//            cart = getArguments().getParcelableArrayList("orderItems");
            orderMethod = getArguments().getInt("method");
            orderID = getArguments().getString("orderID");
            total = getArguments().getLong("total");
            subtotal = getArguments().getLong("subtotal");
            number = getArguments().getLong("numberOfItems");
            address = getArguments().getString("address");
            receiverName = getArguments().getString("receiverName");
            receiverPhone = getArguments().getString("receiverPhone");
            time = getArguments().getString("time");
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
        checkTime.setText(time);
        tvChecking = view.findViewById(R.id.order_checking);
        prepareTime = view.findViewById(R.id.order_prepareTime);
        tvPrepare = view.findViewById(R.id.order_prepare);
        readyTime = view.findViewById(R.id.order_readyTime);
        tvComplete = view.findViewById(R.id.order_complete);
        completeTime = view.findViewById(R.id.order_completeTime);
        imageView = view.findViewById(R.id.order_img);
        state = view.findViewById(R.id.order_tvState);
        cancelBtn = view.findViewById(R.id.order_cancelBtn);

        //Cancel order
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelOrderDialog cancelOrderDialog = new CancelOrderDialog(getContext());

                cancelOrderDialog.show();
            }
        });

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

        cartAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setListViewHeight(cartList);
            }
        });

        db = FirebaseFirestore.getInstance();
        if (cart.isEmpty())
        db.collection("cartItems").whereEqualTo("cartID", orderID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    number = 0;
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        CartItem cartItem = snapshot.toObject(CartItem.class);
                        cart.add(cartItem);
                        number += cartItem.quantity;
                    }
                    tvNumber.setText("(" + number + " món)");
                    cartAdapter.notifyDataSetChanged();
                }
            }
        });

        //Listen to data change
        db.collection("order").document(orderID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                        orderMethod = value.get("method", Integer.class);

                        //Handle order method
                        if (orderMethod == 1)
                            handleTakeAway();

                        switch (value.get("status", Integer.class)) {
                            case 1:
//                                String date = value.getString("date");
//                                if (date != null) {
//                                    try {
//                                        Date date1 = df.parse(date);
//                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//                                        String dateStr = sdf.format(date1);
//                                        System.out.println("Ordered " + dateStr);
//                                    } catch (ParseException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
                                break;
                            case 2:
                                imageView.setImageResource(R.drawable.image_status_prepare);
                                confirm = value.getString("confirmTime");
                                if (confirm != null && !confirm.isEmpty()) {
                                    try {
                                        Date date1 = df.parse(confirm);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        String dateStr = sdf.format(date1);
                                        prepareTime.setText(dateStr);
                                        statePrepare();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case 3:
                                if (orderMethod == 0) {
                                    state.setText("Đang giao");
                                    imageView.setImageResource(R.drawable.image_status_delivering);
                                } else {
                                    state.setText("Đã sẵn sàng");
                                    imageView.setImageResource(R.drawable.image_status_ready);
                                }
                                confirm = value.getString("confirmTime");
                                if (confirm != null && !confirm.isEmpty()) {
                                    try {
                                        Date date1 = df.parse(confirm);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        String dateStr = sdf.format(date1);
                                        prepareTime.setText(dateStr);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                ready = value.getString("serveTime");
                                if (ready != null && !ready.isEmpty()) {
                                    try {
                                        Date date1 = df.parse(ready);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        String dateStr = sdf.format(date1);
                                        readyTime.setText(dateStr);
                                        stateReady();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case 4:
                                state.setText("Đã hoàn thành");
                                imageView.setImageResource(R.drawable.image_status_completed);
                                confirm = value.getString("confirmTime");
                                if (confirm != null && !confirm.isEmpty()) {
                                    try {
                                        Date date1 = df.parse(confirm);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        String dateStr = sdf.format(date1);
                                        prepareTime.setText(dateStr);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                ready = value.getString("serveTime");
                                if (ready != null && !ready.isEmpty()) {
                                    try {
                                        Date date1 = df.parse(ready);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        String dateStr = sdf.format(date1);
                                        readyTime.setText(dateStr);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                completed = value.getString("deliTime");
                                if (completed != null && !completed.isEmpty()) {
                                    try {
                                        Date date1 = df.parse(completed);
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        String dateStr = sdf.format(date1);
                                        completeTime.setText(dateStr);
                                        stateComplete();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
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

    public void statePrepare() {
        cancelBtn.setVisibility(View.GONE);
        checkTime.setTypeface(checkTime.getTypeface(), Typeface.NORMAL);
        checkTime.setTextColor(Color.GRAY);
        tvChecking.setTypeface(tvChecking.getTypeface(), Typeface.NORMAL);
        tvChecking.setTextColor(Color.GRAY);
        prepareTime.setTypeface(prepareTime.getTypeface(), Typeface.BOLD);
        prepareTime.setTextColor(Color.BLACK);
        tvPrepare.setTypeface(tvPrepare.getTypeface(), Typeface.BOLD);
        tvPrepare.setTextColor(Color.BLACK);
        state.setText("Đang chuẩn bị");
    }

    public void stateReady() {
        cancelBtn.setVisibility(View.GONE);
        checkTime.setTypeface(checkTime.getTypeface(), Typeface.NORMAL);
        checkTime.setTextColor(Color.GRAY);
        tvChecking.setTypeface(tvChecking.getTypeface(), Typeface.NORMAL);
        tvChecking.setTextColor(Color.GRAY);
        prepareTime.setTypeface(prepareTime.getTypeface(), Typeface.NORMAL);
        prepareTime.setTextColor(Color.GRAY);
        tvPrepare.setTypeface(tvPrepare.getTypeface(), Typeface.NORMAL);
        tvPrepare.setTextColor(Color.GRAY);
        readyStatusText.setTypeface(prepareTime.getTypeface(), Typeface.BOLD);
        readyStatusText.setTextColor(Color.BLACK);
        readyTime.setTypeface(tvPrepare.getTypeface(), Typeface.BOLD);
        readyTime.setTextColor(Color.BLACK);
    }
    public void stateComplete() {
        cancelBtn.setVisibility(View.GONE);
        checkTime.setTypeface(checkTime.getTypeface(), Typeface.NORMAL);
        checkTime.setTextColor(Color.GRAY);
        tvChecking.setTypeface(tvChecking.getTypeface(), Typeface.NORMAL);
        tvChecking.setTextColor(Color.GRAY);
        prepareTime.setTypeface(prepareTime.getTypeface(), Typeface.NORMAL);
        prepareTime.setTextColor(Color.GRAY);
        tvPrepare.setTypeface(tvPrepare.getTypeface(), Typeface.NORMAL);
        tvPrepare.setTextColor(Color.GRAY);
        readyStatusText.setTypeface(prepareTime.getTypeface(), Typeface.NORMAL);
        readyStatusText.setTextColor(Color.GRAY);
        readyTime.setTypeface(tvPrepare.getTypeface(), Typeface.NORMAL);
        readyTime.setTextColor(Color.GRAY);
        tvComplete.setTypeface(prepareTime.getTypeface(), Typeface.BOLD);
        tvComplete.setTextColor(Color.BLACK);
        completeTime.setTypeface(tvPrepare.getTypeface(), Typeface.BOLD);
        completeTime.setTextColor(Color.BLACK);
    }
}