package com.example.koffi.fragment.order;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.koffi.models.CartItem;
import com.example.koffi.adapter.CartItemAdapter;
import com.example.koffi.models.Item;
import com.example.koffi.R;
import com.example.koffi.models.Topping;
import com.example.koffi.adapter.ToppingAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CheckOutFragment extends Fragment {

    TextView orderMethodTxt;
    Button changeOrderMethodBtn;
    int method;
    TextView bottomMethodText;

    SharedPreferences sharedPref;

    String address;

    String storeID;
    String storeAddress;

    public CheckOutFragment() {
        // Required empty public constructor
    }
    public static CheckOutFragment newInstance(String param1, String param2) {
        CheckOutFragment fragment = new CheckOutFragment();
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
    Item item;

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

        //Get store
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        storeID = sharedPref.getString("store" ,null);
        storeAddress = sharedPref.getString("storeAddress",null);

        //Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.checkout_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        //Init
        bottomMethodText = view.findViewById(R.id.checkout_bottom_methodText);
        changeOrderMethodBtn = view.findViewById(R.id.checkout_changeBtn);
        orderMethodTxt = view.findViewById(R.id.checkout_ordermethod);

        //Handle order method
        method = sharedPref.getInt("orderMethod",0);
        if (method==0) {
            ship = 20000;
            deliveryMethod();
        }
        else if (method==1) {
            ship = 0;
            takeAwayMethod();
        }

        //Change orderMethod
        changeOrderMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeOrderOnClick();
            }
        });

        cart = new ArrayList<CartItem>();
        cart = getArguments().getParcelableArrayList("cartItems");
        total = 0;
        subtotal = 0;
        for (CartItem item : cart) {
            subtotal += item.price;
        }
        total = subtotal + ship;
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
        cartAdapter = new CartItemAdapter(getContext(),cart,true);
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

                TextView tvName = bottomSheetDialog.findViewById(R.id.tvName);
                tvName.setText(cart.get(i).item);

                RadioButton sizeM = bottomSheetDialog.findViewById(R.id.sizeM_radio);
                RadioButton sizeL = bottomSheetDialog.findViewById(R.id.sizeL_radio);

                ImageButton closeView = bottomSheetDialog.findViewById(R.id.itemdetail_closeBtn);
                closeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                if (cart.get(i).size.equals("Upsize")) {
                    sizeL.setChecked(true);
                    isL = true;
                    sizePrice = 6000;
                } else {
                    sizeM.setChecked(true);
                    isL = false;
                    sizePrice = 0;
                }

                TextView tvNumber = bottomSheetDialog.findViewById(R.id.tvNumber);
                tvNumber.setText(cart.get(i).quantity + "");
                Button totalBtn = bottomSheetDialog.findViewById(R.id.itemTotalPrice);
                totalBtn.setText("Thay đổi: " + cart.get(i).price + "đ");
                EditText edtNote = bottomSheetDialog.findViewById(R.id.edtNote);
                edtNote.setText(cart.get(i).note);
                numberUnit = cart.get(i).quantity;

                //Topping listview
                ListView toppingListView = bottomSheetDialog.findViewById(R.id.topping_listview);

                ArrayList<Topping> toppingArray = new ArrayList<Topping>();

                ToppingAdapter toppingAdapter = new ToppingAdapter(getContext(), toppingArray);
                toppingListView.setAdapter(toppingAdapter);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("toppings").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    Topping topping = new Topping(documentSnapshot.getId(), documentSnapshot.getString("name"),
                                            documentSnapshot.getLong("price"));
                                    toppingArray.add(topping);
                                }
                                toppingAdapter.notifyDataSetChanged();
                                setListViewHeight(toppingListView);
                                bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialogInterface) {
                                        for (int n = 0; n < toppingArray.size(); n++) {
                                            for (Topping cartTopping : cart.get(i).toppings) {
                                                if (cartTopping.id.equals(toppingArray.get(n).id)) {
                                                    CheckBox checkBox = toppingListView.getChildAt(n).findViewById(R.id.checkBox);
                                                    checkBox.setChecked(true);
                                                    System.out.println(cartTopping.name);
                                                }
                                            }
                                        }
                                    }
                                });
                                toppingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        CheckBox checkBox = toppingListView.getChildAt(i).findViewById(R.id.checkBox);
                                        checkBox.setChecked(!checkBox.isChecked());
                                        checkListViewCheckBox(toppingListView, toppingArray, bottomSheetDialog, numberUnit);
                                    }
                                });

                                ImageButton plusBtn = bottomSheetDialog.findViewById(R.id.plusButton);
                                plusBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        numberUnit = Integer.parseInt(tvNumber.getText().toString()) + 1;
                                        tvNumber.setText(Long.toString(numberUnit));
                                        checkListViewCheckBox(toppingListView, toppingArray, bottomSheetDialog, numberUnit);
                                    }
                                });

                                ImageButton minusBtn = bottomSheetDialog.findViewById(R.id.minusButton);
                                minusBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (Long.parseLong(tvNumber.getText().toString()) - 1 >= 0)
                                            numberUnit = Integer.parseInt(tvNumber.getText().toString()) - 1;
                                        if (numberUnit > 0) {
                                            tvNumber.setText(Long.toString(numberUnit));
                                            checkListViewCheckBox(toppingListView, toppingArray, bottomSheetDialog, numberUnit);
                                        } else if (numberUnit == 0) {
                                            tvNumber.setText(Long.toString(numberUnit));
                                            totalBtn.setText("Xóa khỏi giỏ hàng");
                                        }
                                    }
                                });

                                sizeM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                        if (b) {
                                            sizeL.setChecked(false);
                                            isL = false;
                                        }
                                        if (isL) {
                                            sizePrice = 6000;
                                        }
                                        else {
                                            sizePrice = 0;
                                        }
                                        checkListViewCheckBox(toppingListView, toppingArray, bottomSheetDialog, numberUnit);
                                    }
                                });
                                sizeL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                        if (b) {
                                            sizeM.setChecked(false);
                                            isL = true;
                                        }
                                        if (isL) {
                                            sizePrice = 6000;
                                        }
                                        else {
                                            sizePrice = 0;
                                        }
                                        checkListViewCheckBox(toppingListView, toppingArray, bottomSheetDialog, numberUnit);
                                    }
                                });

                                totalBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Query query = db.collection("cartItems")
                                                .whereEqualTo("cartID", cart.get(i).cartID)
                                                .whereEqualTo("item", cart.get(i).item)
                                                .whereEqualTo("size", cart.get(i).size)
                                                .whereEqualTo("toppings", cart.get(i).toppings);
                                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                        docID = snapshot.getId();
                                                        if (numberUnit != 0) {
                                                            long sum = 0;
                                                            for (int n = 0; n < 8; n++) {
                                                                CheckBox itemCheckBox = toppingListView.getChildAt(n).findViewById(R.id.checkBox);
                                                                if (itemCheckBox.isChecked()) {
                                                                    sum += toppingArray.get(n).price;
                                                                }
                                                            }
                                                            note = edtNote.getText().toString().trim();
                                                            size = sizeL.isChecked() ? "Upsize" : "Vừa";
                                                            ArrayList<Topping> toppingToCart = new ArrayList<>();
                                                            for (int i = 0; i < 8; i++) {
                                                                CheckBox checkBox = toppingListView.getChildAt(i).findViewById(R.id.checkBox);
                                                                if (checkBox.isChecked()) {
                                                                    toppingToCart.add(toppingArray.get(i));
                                                                }
                                                            }
                                                            totalUnit = (unit + sum + sizePrice) * numberUnit;
                                                            Query find = db.collection("cartItems")
                                                                    .whereEqualTo("cartID", cart.get(i).cartID)
                                                                    .whereEqualTo("item", cart.get(i).item)
                                                                    .whereEqualTo("size", size)
                                                                    .whereEqualTo("toppings", toppingToCart);
                                                            find.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        CartItem itemInCart = new CartItem();
                                                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                                            exist = documentSnapshot.getId();
                                                                            itemInCart = documentSnapshot.toObject(CartItem.class);
                                                                        }
                                                                        if (exist.equals(docID)) {
                                                                            db.collection("cartItems").document(docID)
                                                                                    .update("note", note, "size", size,
                                                                                            "quantity", numberUnit, "price", totalUnit,
                                                                                            "toppings", toppingToCart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Toast.makeText(getContext(), "Đã cập nhật sản phẩm trong giỏ hàng", Toast.LENGTH_SHORT).show();
                                                                                    bottomSheetDialog.dismiss();
                                                                                }
                                                                            });
                                                                        } else {
                                                                            System.out.println("Doc exist: " + exist);
                                                                            db.collection("cartItems").document(exist)
                                                                                    .update("note", note,
                                                                                            "quantity", numberUnit + itemInCart.quantity,
                                                                                            "price", totalUnit + itemInCart.price)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    db.collection("cartItems").document(docID).delete();
                                                                                    Toast.makeText(getContext(), "Đã cập nhật sản phẩm trong giỏ hàng", Toast.LENGTH_SHORT).show();
                                                                                    bottomSheetDialog.dismiss();
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                        else {
                                                            db.collection("cartItems").document(docID)
                                                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(getContext(), "Đã xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                                                                    bottomSheetDialog.dismiss();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                                //Show dialog
                                bottomSheetDialog.show();
                            }
                        });

                //Get cart item
                db.collection("menu").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("menu")
                                        .document(document.getId())
                                        .collection("items")
                                        .whereEqualTo("name", cart.get(i).item)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                            System.out.println("Found item");
                                            item = snapshot.toObject(Item.class);
                                            TextView tvPrice = bottomSheetDialog.findViewById(R.id.tvPrice);
                                            tvPrice.setText(item.price + "đ");
                                            TextView tvDes = bottomSheetDialog.findViewById(R.id.tvDescription);
                                            tvDes.setText(item.description);
                                            ImageView imageView = bottomSheetView.findViewById(R.id.itemdetail_image);
                                            int drawableId = view.getResources().getIdentifier(item.image, "drawable", getContext().getPackageName());
                                            imageView.setImageResource(drawableId);
                                            unit = item.price;
                                        }
                                    }
                                });
                                if (item != null) break;
                            }
                        }
                    }
                });
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
                                                                        tvTotal.setText("20000đ");
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
        //Navigate to OrderFragment
        Button orderBtn = view.findViewById(R.id.orderBtn);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("method", method);
                Navigation.findNavController(getView()).navigate(R.id.action_checkOutFragment_to_orderFragment, bundle);
            }
        });
    }

    long totalUnit;
    boolean isL = false;
    long unit;
    long sizePrice = 0;
    int numberUnit;
    String size;
    String note;
    EditText edtNote;
    int ship = 0;
    String docID;
    String exist = "";
    int orderMethod;

    private void checkListViewCheckBox(ListView toppingListView, ArrayList<Topping> toppingArray, BottomSheetDialog bottomSheetView, long number) {
        long sum = 0;
        for (int n = 0; n < 8; n++) {
            CheckBox itemCheckBox = toppingListView.getChildAt(n).findViewById(R.id.checkBox);
            if (itemCheckBox.isChecked()) {
                sum += toppingArray.get(n).price;
            }
        }
        Button totalBtn = bottomSheetView.findViewById(R.id.itemTotalPrice);
        totalBtn.setText("Thay đổi: " + (unit + sum + sizePrice) * number);
        System.out.println("Unit: " + unit + "\tSum: " + sum + "\tSize price: " + sizePrice + "\tNumber: " + number);
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
                                        tvTotal2.setText("20000đ");
                                    }
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        CartItem cartItem = snapshot.toObject(CartItem.class);
                                        cart.add(cartItem);
                                        subtotal += cartItem.price;
                                        number += cartItem.quantity;
                                    }
                                    total = subtotal + ship;
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
        LinearLayout takeaway = getView().findViewById(R.id.takeaway_store);
        LinearLayout ship = getView().findViewById(R.id.checkout_ship);

        delivery.setVisibility(View.VISIBLE);
        takeaway.setVisibility(View.GONE);
        ship.setVisibility(View.VISIBLE);

        orderMethodTxt.setText("Giao tận nơi");
        bottomMethodText.setText("Giao tận nơi • ");

        //Address
        TextView txtTendc= getView().findViewById(R.id.tendc_checkout);
        TextView txtdc=getView().findViewById(R.id.dc_checkout);

        address = sharedPref.getString("dc","Chọn địa chỉ");
        String addressName = sharedPref.getString("tendc","Chọn địa chỉ");
        txtTendc.setText(addressName);
        txtdc.setText(address);

        //Navigate to Address Fragment
        LinearLayout chooseAddress = getView().findViewById(R.id.checkout_chooseAddress);
        chooseAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("from","checkout");
                Navigation.findNavController(getView()).navigate(R.id.action_checkOutFragment_to_addressFragment22,bundle);
            }
        });
    }
    public void takeAwayMethod() {
        LinearLayout delivery = getView().findViewById(R.id.checkout_delivery);
        LinearLayout takeaway = getView().findViewById(R.id.takeaway_store);
        LinearLayout ship = getView().findViewById(R.id.checkout_ship);
        TextView addressNameTxt = getView().findViewById(R.id.checkout_takeaway_addressName);
        TextView addressTxt = getView().findViewById(R.id.checkout_takeaway_address);

        delivery.setVisibility(View.GONE);
        takeaway.setVisibility(View.VISIBLE);
        ship.setVisibility(View.GONE);

        //Address
        if (storeAddress!=null) {
            String addressName = storeAddress.substring(0,storeAddress.indexOf(","));
            addressNameTxt.setText(addressName);
            addressTxt.setText(storeAddress);
        }

        orderMethodTxt.setText("Tự đến lấy");
        bottomMethodText.setText("Tự đến lấy • ");

        //Choose store
        LinearLayout chooseAddress = getView().findViewById(R.id.takeaway_store);
        chooseAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("back","store");
                bundle.putString("from","checkout");
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
                ship = 20000;
                total = subtotal + ship;
                tvTotal.setText(total + "đ");
                tvTotal2.setText(total + "đ");
                method=0;
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("orderMethod",method);
                editor.apply();
                deliveryMethod();
                bottomSheetDialog.dismiss();
            }
        });

        //Take away
        LinearLayout takeaway = bottomSheetView.findViewById(R.id.ordermethod_takeaway);
        takeaway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ship = 0;
                total = subtotal + ship;
                tvTotal.setText(total + "đ");
                tvTotal2.setText(total + "đ");
                method=1;
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("orderMethod",method);
                editor.apply();
                takeAwayMethod();
                bottomSheetDialog.dismiss();
            }
        });

        //Show dialog
        bottomSheetDialog.show();
    }
}