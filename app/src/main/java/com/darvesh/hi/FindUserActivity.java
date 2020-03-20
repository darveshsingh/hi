package com.darvesh.hi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import com.darvesh.hi.user.UserListAdapter;
import com.darvesh.hi.user.UserObject;
import com.darvesh.hi.utils.CountryToPhonePrefix;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FindUserActivity extends AppCompatActivity {

    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;

    ArrayList<UserObject> contactList, userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        contactList = new ArrayList<>();
        userList= new ArrayList<>();

        Button mCreateGrp = findViewById(R.id.createGrp);
        mCreateGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChatGrp();
            }
        });

        initializeRecyclerView();
        getContactList();
    }

    private void createChatGrp() {

        String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");
        DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("info");

        HashMap newChatMap =  new HashMap();
        newChatMap.put("id", key);
        newChatMap.put("users/"+FirebaseAuth.getInstance().getUid(), true);

        boolean validChat=false;

        for(UserObject userObject: userList) {
            if (userObject.isSelected()) {
                validChat=true;
                newChatMap.put("users/"+userObject.getUid(), true);
                userDb.child(userObject.getUid()).child("chat").child(key).setValue(true);
            }
        }

        if(validChat){
            chatInfoDb.updateChildren(newChatMap);
            userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
        }
    }

    private String getCountryISO(){
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso()!=null){
            if(!telephonyManager.getNetworkCountryIso().equals("")){
                iso=telephonyManager.getNetworkCountryIso();
            }
        }

        return CountryToPhonePrefix.getPhone(iso);
    }

    private void getContactList(){
        String isoPrefix = getCountryISO();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
        while(phones.moveToNext()){
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            number=number.replace(" ","");
            number=number.replace("(","");
            number=number.replace(")","");
            number=number.replace("-","");

            if(!String.valueOf(number.charAt(0)).equals("+")){
                number=isoPrefix+number;
            }

            UserObject mContact = new UserObject(name, number, "");
            contactList.add(mContact);
            //mUserListAdapter.notifyDataSetChanged();
            getUserDetails(mContact);
        }
    }

    private void getUserDetails(UserObject mContact) {
        DatabaseReference mUserDB= FirebaseDatabase.getInstance().getReference().child("user");
        Query query = mUserDB.orderByChild("phone").equalTo(mContact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String phone = "", name = "";

                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        if(childSnapshot.child("phone").getValue() != null){
                            phone = childSnapshot.child("phone").getValue().toString();
                        }
                        if(childSnapshot.child("name").getValue() != null){
                            name = childSnapshot.child("name").getValue().toString();
                        }

                        UserObject mUser = new UserObject(name, phone, childSnapshot.getKey());
                        boolean exists =false;
                        if(name.equals(phone)){
                            for(UserObject mUserLoop:contactList){
                                if(mUserLoop.getPhone().equals(mUser.getPhone())){
                                    mUser.setName(mUserLoop.getName());
                                }
                            }
                            for(UserObject checkDupUser:userList){
                                if(checkDupUser.getName().equals(mUser.getName()))
                                    exists=true;
                            }
                        }
                        if(exists)
                            continue;
                        userList.add(mUser);
                        mUserListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeRecyclerView() {
        mUserList=findViewById(R.id.userList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);

        mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);

        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);
    }
}
