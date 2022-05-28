package com.example.koffi;

import static android.content.ContentValues.TAG;

import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CheckOutFragment extends Fragment {

    Button changeOrderMethodBtn;

    public CheckOutFragment() {
        // Required empty public constructor
    }
    public static CheckOutFragment newInstance(String param1, String param2) {
        CheckOutFragment fragment = new CheckOutFragment();
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
        return inflater.inflate(R.layout.fragment_check_out, container, false);
    }

    ArrayList<CartItem> cart;
    long total;
    long subtotal;
    long number;
    FirebaseFirestore db;
    TextView tvSubtotal;
    TextView tvTotal;
    TextView tvTotal2;
    TextView tvNumber;
    CartItemAdapter cartAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        //Back pressed
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                bundle.putString("back","menu");
                Navigation.findNavController(view).navigate(R.id.action_global_mainFragment,bundle);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);

        //Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.checkout_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        //Handle order method
        if (getArguments()!=null) {
            int method = getArguments().getInt("method");
            if (method==0)
                deliveryMethod();
            else if (method==1)
                takeAwayMethod();
        }

        cart = new ArrayList<CartItem>();
        cart = getArguments().getParcelableArrayList("cartItems");
        total = 0;
        subtotal = 0;
        for (CartItem item : cart) {
            subtotal += item.price;
        }
        total = subtotal;
        tvSubtotal = view.findViewById(R.id.cart_subtotal);
        tvSubtotal.setText(subtotal + "");
        tvTotal = view.findViewById(R.id.cart_total);
        tvTotal2 = view.findViewById(R.id.cart_total2);
        tvTotal.setText(total + "đ");
        tvTotal2.setText(total + "đ");
        tvNumber = view.findViewById(R.id.numberOfItems);
        number = getArguments().getLong("numberOfItems");
        tvNumber.setText(number + " sản phẩm");

        //Receiver information
        LinearLayout receiver = view.findViewById(R.id.checkout_receiver);
        receiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bottom sheet dialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_checkout_receiver,
                        (LinearLayout)view.findViewById(R.id.receiver_bottomsheet));
                bottomSheetDialog.setContentView(bottomSheetView);
                //Set bottom sheet height
                setBottomSheetHeight(bottomSheetView);

                //Handle bottom sheet
                ImageButton backBtn = bottomSheetView.findViewById(R.id.receiver_backBtn);
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                //Show dialog
                bottomSheetDialog.show();
            }
        });

        //Listen to data change
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Query query = db.collection("order")
                    .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .whereEqualTo("status", 0);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            db.collection("cartItems").whereEqualTo("cartID", doc.getId())
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                Log.w(TAG, "listen:error", error);
                                                return;
                                            }
                                            System.out.println("Db updated");
                                            reloadCart();
                                            for (DocumentChange dc : value.getDocumentChanges()) {
                                                switch (dc.getType()) {
                                                    case ADDED:
                                                        Log.d(TAG, "New cart item: " + dc.getDocument().getData());
                                                        break;
                                                    case MODIFIED:
                                                        Log.d(TAG, "Modified cart item: " + dc.getDocument().getData());
                                                        break;
                                                    case REMOVED:
                                                        Log.d(TAG, "Removed cart item: " + dc.getDocument().getData());
                                                        break;
                                                }
                                            }
                                        }
                                    });

                        }
                    }
                }
            });
        }
        //Cart list
        //Sample data
//        cart.add(new CartItem(new Item("123","Cà phê","hotcoffee_1",new Long(30000),""),2,new Long(35000),"Upsize"));
//        cart.add(new CartItem(new Item("123","Trà sữa","milktea_1",new Long(40000),""),1,new Long(45000),"Trân châu hoàng kim, Ít ngọt"));
//        cart.add(new CartItem(new Item("123","Nước ngọt","iceddrinks_1",new Long(50000),""),2,new Long(55000),"Upsize, Rau câu"));
//        ArrayList<Topping> toppings = new ArrayList<Topping>();
//        toppings.add(new Topping("1", "Trân châu", 6000));
//        toppings.add(new Topping("2", "Rau câu", 8000));
//        cart.add(new CartItem("1", "Trân châu", 1, Long.parseLong("30000"), "Vừa", toppings));

        ListView cartList = view.findViewById(R.id.cartList);
        cartAdapter = new CartItemAdapter(getContext(),cart);
        cartAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setListViewHeight(cartList);
            }
        });
        cartList.setAdapter(cartAdapter);
        setListViewHeight(cartList);
        cartList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Bottom Sheet Dialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_itemdetail,
                        (LinearLayout)view.findViewById(R.id.menu_bottomsheet));
                bottomSheetDialog.setContentView(bottomSheetView);

                //Show dialog
                bottomSheetDialog.show();
            }
        });

        //Add more item
        Button addBtn = view.findViewById(R.id.checkout_addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("back","menu");
                Navigation.findNavController(view).navigate(R.id.action_global_mainFragment,bundle);
            }
        });

        //Payment Method
        LinearLayout payment = view.findViewById(R.id.checkout_payment);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bottom sheet dialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_paymentmethod,
                        (LinearLayout)view.findViewById(R.id.paymentmethod_bottomsheet));
                bottomSheetDialog.setContentView(bottomSheetView);
                //Set bottom sheet height
                setBottomSheetHeight(bottomSheetView);
                //Handle bottom sheet

                //Show dialog
                bottomSheetDialog.show();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        LinearLayout deleteOrder = view.findViewById(R.id.checkout_deleteorder);
        deleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = db.collection("order")
                        .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .whereEqualTo("status", 0);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                db.collection("cartItems")
                                        .whereEqualTo("cartID", documentSnapshot.getId()).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                        db.collection("cartItems")
                                                                .document(snapshot.getId())
                                                                .delete()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        cart.clear();
                                                                        cartAdapter.notifyDataSetChanged();
                                                                        tvNumber.setText("0 sản phẩm");
                                                                        tvSubtotal.setText("0đ");
                                                                        tvTotal.setText("0đ");
                                                                        tvTotal2.setText("0đ");
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
            }
        });
    }

    private void reloadCart() {
        cart.clear();
        total = 0;
        subtotal = 0;
        number = 0;
        Query query = db.collection("order")
                .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("status", 0);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        db.collection("cartItems").whereEqualTo("cartID", doc.getId())
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() == 0) {
                                        tvNumber.setText("0 sản phẩm");
                                        tvSubtotal.setText("0đ");
                                        tvTotal.setText("0đ");
                                        tvTotal2.setText("0đ");
                                    }
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        CartItem cartItem = snapshot.toObject(CartItem.class);
                                        cart.add(cartItem);
                                        subtotal += cartItem.price;
                                        number += cartItem.quantity;
                                    }
                                    total = subtotal;
                                    tvNumber.setText(number + " sản phẩm");
                                    tvSubtotal.setText(subtotal + "đ");
                                    tvTotal.setText(total + "đ");
                                    tvTotal2.setText(total + "đ");
                                    cartAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void setListViewHeight(ListView listview) {
        ListAdapter listAdapter = listview.getAdapter();
        if (listAdapter != null) {
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listview);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listview.getLayoutParams();
            params.height = totalHeight + (listview.getDividerHeight() * (listAdapter.getCount() - 1));
            listview.setLayoutParams(params);
        }
    }
    public void setBottomSheetHeight(View bottomSheetView) {
        ViewGroup.LayoutParams lp =bottomSheetView.getLayoutParams();
        lp.height= Resources.getSystem().getDisplayMetrics().heightPixels;
        bottomSheetView.setLayoutParams(lp);
    }
    public void deliveryMethod(){
        LinearLayout delivery = getView().findViewById(R.id.checkout_delivery);
        LinearLayout takeaway = getView().findViewById(R.id.checkout_takeaway);

        delivery.setVisibility(View.VISIBLE);
        takeaway.setVisibility(View.GONE);

        //Change orderMethod
        changeOrderMethodBtn = getView().findViewById(R.id.checkout_delivery_changeBtn);
        changeOrderMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeOrderOnClick();
            }
        });

        //Navigate to Address Fragment
        LinearLayout chooseAddress = getView().findViewById(R.id.checkout_chooseAddress);
        chooseAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.action_checkOutFragment_to_addressFragment22);
            }
        });
    }
    public void takeAwayMethod() {
        LinearLayout delivery = getView().findViewById(R.id.checkout_delivery);
        LinearLayout takeaway = getView().findViewById(R.id.checkout_takeaway);

        delivery.setVisibility(View.GONE);
        takeaway.setVisibility(View.VISIBLE);

        //Change orderMethod
        changeOrderMethodBtn = getView().findViewById(R.id.checkout_takeaway_changeBtn);
        changeOrderMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeOrderOnClick();
            }
        });

        //Choose store
        LinearLayout chooseAddress = getView().findViewById(R.id.takeaway_store);
        chooseAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("back","store");
                Navigation.findNavController(getView()).navigate(R.id.action_global_mainFragment,bundle);
            }
        });
    }
    public void changeOrderOnClick() {
        //Bottom sheet dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_ordermethod,
                (LinearLayout)getView().findViewById(R.id.ordermethod_bottomsheet));
        bottomSheetDialog.setContentView(bottomSheetView);

        //Handle bottom sheet
        Button editDeliveryBtn = bottomSheetView.findViewById(R.id.delivery_editBtn);
        Button editTABtn = bottomSheetView.findViewById(R.id.takeaway_editBtn);

        editDeliveryBtn.setVisibility(View.GONE);
        editTABtn.setVisibility(View.GONE);

        //Delivery
        LinearLayout delivery = bottomSheetView.findViewById(R.id.ordermethod_delivery);
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deliveryMethod();
                bottomSheetDialog.dismiss();
            }
        });

        //Take away
        LinearLayout takeaway = bottomSheetView.findViewById(R.id.ordermethod_takeaway);
        takeaway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeAwayMethod();
                bottomSheetDialog.dismiss();
            }
        });

        //Show dialog
        bottomSheetDialog.show();
    }
}