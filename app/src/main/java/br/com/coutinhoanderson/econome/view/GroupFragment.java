package br.com.coutinhoanderson.econome.view;


import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.adapter.FundsAdapter;
import br.com.coutinhoanderson.econome.model.Group;
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
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            final EditText edittext = new EditText(getContext());
            alert.setMessage("Enter the name of the group");
            alert.setTitle("Creating");
            alert.setView(edittext);
            alert.setPositiveButton("Confirm", (dialog, whichButton) -> {
                String groupName = edittext.getText().toString();
                String id = FirebaseAuth.getInstance().getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/Users/" + id);
                reference.getDatabase();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = (dataSnapshot.getValue(User.class));
                        Group group = new Group();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Groups").push();
                        ref.setValue(group);
                        ref.setValue(user);
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("/Users").child(FirebaseAuth.getInstance().getUid());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            });
            alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
            });
            alert.show();
        }
    }
}
