package br.com.coutinhoanderson.econome.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.adapter.GroupsAdapter;
import br.com.coutinhoanderson.econome.model.Group;
import br.com.coutinhoanderson.econome.model.User;

public class FundsFragment extends Fragment {
    private List<User> users;
    private GroupsAdapter groupsAdapter;
    private DatabaseReference ref;
    private TextView groupName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_funds, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.people_list);
        groupName = view.findViewById(R.id.group_name);
        users = new ArrayList<>();
        groupsAdapter = new GroupsAdapter(getContext(),users);
        recyclerView.setAdapter(groupsAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseServer fs = new FirebaseServer();
        fs.fetchDataFromFirebase();
    }

    private class FirebaseServer {
        void fetchDataFromFirebase() {
            ref = FirebaseDatabase.getInstance().getReference("Groups");
            ChildEventListener listener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Group currentGroup = dataSnapshot.getValue(Group.class);
                    Query groupsByUser = ref
                            .child(dataSnapshot.getKey())
                            .child("members").orderByKey()
                            .equalTo(FirebaseAuth.getInstance().getUid());
                    groupsByUser.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                groupName.setText(currentGroup.getName());
                                for(Map.Entry<String, User> user: currentGroup.getMembers().entrySet())
                                users.add(user.getValue());
                                groupsAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            ref.addChildEventListener(listener);
        }

    }
    }

