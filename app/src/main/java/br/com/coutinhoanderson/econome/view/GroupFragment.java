package br.com.coutinhoanderson.econome.view;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
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

import java.util.List;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.adapter.FundsAdapter;
import br.com.coutinhoanderson.econome.model.User;

public class GroupFragment extends Fragment {
    private ConstraintLayout addMember;
    private Button addMemberBtn;
    private EditText addMemberEdit;
    private FloatingActionButton addGroup;
    List<User> users;
    private FundsAdapter fundsAdapter;
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
        addGroup.setOnClickListener(v -> {
            fs.createGroup();
        });
    }

    private class FirebaseServer {
        void createGroup() {
            ref = FirebaseDatabase.getInstance().getReference("Groups/"+ FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
//            ref = FirebaseDatabase.getInstance().getReference("Users").child("")
        }
        void fetchDataFromFirebase() {
            ref = FirebaseDatabase.getInstance().getReference("Users");
            dataListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildKey) {
                    int index = 0;
                    if (previousChildKey != null)
                        index = getIndexForKey(previousChildKey, users) + 1;
                    users.add(index, snapshot.getValue(User.class));
                    fundsAdapter.notifyItemInserted(users.size());
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildKey) {
                    int index = getIndexForKey(snapshot.getKey(), users);
                    users.set(index, snapshot.getValue(User.class));
                    fundsAdapter.notifyItemChanged(index);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    int index = getIndexForKey(snapshot.getKey(), users);
                    users.remove(index);
                    fundsAdapter.notifyItemRemoved(index);
                    fundsAdapter.notifyItemRangeChanged(0, users.size());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildKey) {
                    int oldIndex = getIndexForKey(snapshot.getKey(), users);
                    users.remove(oldIndex);
                    int newIndex = previousChildKey == null ? 0 : getIndexForKey(previousChildKey, users) + 1;
                    users.add(newIndex, snapshot.getValue(User.class));
                    fundsAdapter.notifyItemMoved(oldIndex, newIndex);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseReadError", "The read failed: " + error.getCode());
                }
            };
            ref.addChildEventListener(dataListener);
        }

        private int getIndexForKey(String key, List<User> users) {
            int index = 0;
            for (User user : users) {
                if (user.getPhone().equals(key)) {
                    return index;
                } else {
                    index++;
                }
            }
            throw new IllegalArgumentException("Key not found");
        }

    }
}
