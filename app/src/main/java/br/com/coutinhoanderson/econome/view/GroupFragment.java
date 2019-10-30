package br.com.coutinhoanderson.econome.view;


import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.adapter.GroupsAdapter;
import br.com.coutinhoanderson.econome.model.Group;
import br.com.coutinhoanderson.econome.model.User;

public class GroupFragment extends Fragment {
    private ConstraintLayout addMember;
    private Button addMemberBtn;
    private EditText addMemberEdit;
    private FloatingActionButton addGroup;
    List<User> users;
    private GroupsAdapter fundsAdapter;
    private ChildEventListener dataListener;
    private DatabaseReference ref;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        FirebaseServer fs = new FirebaseServer();
        addMember = view.findViewById(R.id.add_member_layout);
        addGroup = view.findViewById(R.id.add_group);
        addMemberBtn = view.findViewById(R.id.add_member_btn);
        addMemberEdit = view.findViewById(R.id.add_member_text);
        addGroup.setOnClickListener(v -> fs.createGroup());
    }

    private class FirebaseServer {
        void createGroup() {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            final EditText edittext = new EditText(getContext());
            alert.setMessage("Enter the name or ID of the group");
            alert.setTitle("Confirmation");
            alert.setView(edittext);
            alert.setPositiveButton("Create", (dialog, whichButton) -> {
                String groupName = edittext.getText().toString();
                String id = FirebaseAuth.getInstance().getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/Users/" + id);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = (dataSnapshot.getValue(User.class));
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Groups/").push();
                        ref.getKey();
                        Map<String, Object> users = new HashMap<>();
                        users.put(id, user);
                        Map<String, Object> groupMap = new HashMap<>();
                        groupMap.put("name", groupName);
                        groupMap.put("totalBudget", user.getBudget());
                        groupMap.put("remainingBudget", user.getBudget());
                        groupMap.put("members", users);
                        ref.setValue(groupMap).addOnCompleteListener(task -> {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setMessage("Confirmation");
                            alert.setTitle("Group created");
                            alert.setView(edittext);
                            alert.setPositiveButton("Ok", null);
                        });
//                        ref.setValue(user);
//                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("/Users").child(FirebaseAuth.getInstance().getUid());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            });
            alert.setNegativeButton("Join", (dialog, whichButton) -> {
                String id = FirebaseAuth.getInstance().getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/Users/" + id);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = (dataSnapshot.getValue(User.class));
                        Map<String, Object> users = new HashMap<>();
                        users.put(id, user);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Groups/");
                        ref.orderByKey().equalTo(edittext.getText().toString())
                                .addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        Group group = (dataSnapshot.getValue(Group.class));
                                        group.getMembers().put(id, user);
                                        Map<String, Object> map = new HashMap<>();
                                            group.setRemainingFunds(String.valueOf(
                                                    Double.valueOf(group.getRemainingFunds()) + Double.valueOf(user.getBudget()))
                                            );
                                            group.setTotalBudget(String.valueOf(
                                                    Double.valueOf(group.getTotalBudget()) + Double.valueOf(user.getBudget()))
                                            );
                                        map.put(dataSnapshot.getKey(), group);
                                        ref.updateChildren(map);
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        Log.d("teste", dataSnapshot.getKey());
                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                        Log.d("teste", dataSnapshot.getKey());
                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        Log.d("teste", dataSnapshot.getKey());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            });
            alert.show();
        }
    }
}
