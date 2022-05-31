package com.example.koffi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class StoreFragment extends Fragment {

    ListView listView;
    ArrayList<Store> storeArray;
    StoreAdapter adapter;
    EditText editText;
    String from="";

    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get argument
        if (getArguments()!=null) {
            from = getArguments().getString("from");
        }

        //Custom toolbar
        Toolbar toolbar = view.findViewById(R.id.store_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        listView = view.findViewById(R.id.storesListView);
        storeArray = new ArrayList<Store>();
        adapter = new StoreAdapter(getContext(),storeArray);
        listView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("stores").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            Store store = new Store(documentSnapshot.getId(), documentSnapshot.getString("address"),
                                    documentSnapshot.getString("image"), documentSnapshot.getString("PhoneNumber"));
                            storeArray.add(store);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

        //Search store
        editText = view.findViewById(R.id.addressEdit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        //Store Detail Bottom Sheet
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_storedetail,
                        (LinearLayout)view.findViewById(R.id.store_bottomsheet));
                bottomSheetDialog.setContentView(bottomSheetView);
                //Assign data
                Store store = (Store) listView.getItemAtPosition(i);

                String address = store.address;
                String shortenedAddress = address.substring(0,address.indexOf(","));

                TextView shortAddressText = bottomSheetView.findViewById(R.id.storedetail_shortaddress);
                shortAddressText.setText(shortenedAddress);

                TextView addressText = bottomSheetView.findViewById(R.id.storedetail_fulladdress);
                addressText.setText(address);

                TextView phoneText = bottomSheetView.findViewById(R.id.storedetail_contact);
                phoneText.setText("Liên hệ: "+store.phoneNumber);

                RoundedImageView storeImage = bottomSheetView.findViewById(R.id.bottomsheet_image);
                int drawableId = view.getResources().getIdentifier(store.image, "drawable", getContext().getPackageName());
                storeImage.setImageResource(drawableId);

                //Hide bottomsheet
                ImageButton closeBtn = bottomSheetView.findViewById(R.id.store_closeBtn);
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                //Handle bottom sheet
                Button takeawayBtn = bottomSheetView.findViewById(R.id.store_takeawayBtn);
                takeawayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager fragmentManager = getParentFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString("address",store.address);

                        if (from.equals("checkout")) {
                            fragmentManager.setFragmentResult("storeResult",bundle);
                            Navigation.findNavController(getView()).popBackStack();
                        }
                        else {
                            MenuFragment fragment = new MenuFragment();
                            fragment.setArguments(bundle);
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frameLayout, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }

                        bottomSheetDialog.dismiss();
                    }
                });

                //Show dialog
                bottomSheetDialog.show();

                //Share Address
                LinearLayout shareAddress = bottomSheetView.findViewById(R.id.store_shareaddress);
                shareAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Hẹn bạn tại Koffi, ");
                        intent.putExtra(Intent.EXTRA_TEXT,
                                "https://www.google.com/maps/place/Bitexco+Tower,+h%E1%BA%BBm+s%E1%BB%91+2+H%C3%A0m+Nghi,+B%E1%BA%BFn+Ngh%C3%A9,+Qu%E1%BA%ADn+1,+Th%C3%A0nh+ph%E1%BB%91+H%E1%BB%93+Ch%C3%AD+Minh,+Vi%E1%BB%87t+Nam/@10.7709693,106.7040252,16.48z/data=!4m5!3m4!1s0x31752f43fd97af5f:0x88ba5dd71b15433c!8m2!3d10.7719937!4d106.7057951");
                        startActivity(Intent.createChooser(intent, "Chia sẻ với thiết bị"));
                    }
                });

                //Store Contact
                LinearLayout storeContact = bottomSheetView.findViewById(R.id.store_contact);
                storeContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+store.phoneNumber));
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void filter (String text) {
        ArrayList<Store> filteredList = new ArrayList<>();

        for (Store store : storeArray) {
            if (store.address.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(store);
            }
        }
        adapter.filteredList(filteredList);
    }
}