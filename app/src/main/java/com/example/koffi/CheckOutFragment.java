package com.example.koffi;

import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CheckOutFragment extends Fragment {

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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Navigate to Address Fragment
        LinearLayout chooseAddress = view.findViewById(R.id.checkout_chooseAddress);
        chooseAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.action_checkOutFragment_to_addressFragment22);
            }
        });

        cart = new ArrayList<CartItem>();
        cart = getArguments().getParcelableArrayList("cartItems");
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
        CartItemAdapter cartAdapter = new CartItemAdapter(getContext(),cart);
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
}