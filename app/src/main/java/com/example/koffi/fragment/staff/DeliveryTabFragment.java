package com.example.koffi.fragment.staff;

import static android.content.ContentValues.TAG;
import static com.example.koffi.FunctionClass.setListViewHeight;

import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.koffi.R;
import com.example.koffi.adapter.OrderAdapter;
import com.example.koffi.models.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class DeliveryTabFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public DeliveryTabFragment() {
        // Required empty public constructor
    }


    public static DeliveryTabFragment newInstance(String param1, String param2) {
        DeliveryTabFragment fragment = new DeliveryTabFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delivery_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Date date = Calendar.getInstance().getTime();
        ArrayList<Order> orderArray = new ArrayList<Order>();
        ArrayList<String> idList = new ArrayList<String>();

        ListView listView = view.findViewById(R.id.deliveryLv);
        TabLayout tabLayout = getParentFragment().getView().findViewById(R.id.order_tabLayout);
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

        //getData from firebase
        db.collection("order").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    Log.d(TAG, "Modified Order: " + dc.getDocument().getData());
                }
                orderArray.clear();
                orderAdapter.notifyDataSetChanged();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user!=null) {
                    db.collection("staff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())
                                for (QueryDocumentSnapshot document1 : task.getResult()) {

                                    if (user.getEmail().toString().equals(document1.getString("email"))) {
                                        db.collection("order").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful())
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if (document1.getString("store").toString().equals(document.getString("storeID"))) {
                                                            if (document.getLong("method") == 0) {
                                                                if (document.getLong("status") != 5&&document.getLong("status")!=4) {
                                                                    Order order =new Order(document.getString("orderID"), document.getString("userID"), document.getString("name")
                                                                            , document.getString("storeID"), date, document.getLong("status").intValue()
                                                                            , document.getString("address"), document.getString("phoneNumber")
                                                                            , document.getLong("subtotal"), document.getLong("ship")
                                                                            , document.getLong("total"), document.getString("deliveryNote")
                                                                            , document.getLong("method").intValue());
                                                                    orderArray.add(order);
                                                                    idList.add(document.getId());
                                                                    orderAdapter.notifyDataSetChanged();
                                                                    tabLayout.getTabAt(0).getOrCreateBadge().setNumber(orderArray.size());
                                                                }
                                                            }
                                                        }
                                                    }
                                                else {
                                                    Log.w(TAG, "Error getting documents.", task.getException());

                                                }
                                            }
                                        });
                                    }
                                }
                        }
                    });
                }

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle=new Bundle();
                String docID=idList.get(i).toString();
                bundle.putString("documentID",docID);
                Navigation.findNavController(getView()).navigate(R.id.action_staffOrderFragment_to_orderDetailFragment,bundle);
            }
        });


    }
}