package br.com.coutinhoanderson.econome.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.adapter.FundsAdapter;
import br.com.coutinhoanderson.econome.model.User;

public class FundsFragment extends Fragment {
    List<User> users;
    private FundsAdapter fundsAdapter;
    private ChildEventListener dataListener;
    private DatabaseReference ref;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_funds, container, false);
        recyclerView = view.findViewById(R.id.people_list);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        users = new ArrayList<>();
        fundsAdapter = new FundsAdapter(getContext(), users);
        recyclerView.setAdapter(fundsAdapter);
        FirebaseServer fs = new FirebaseServer();
        fs.fetchDataFromFirebase();
    }

    private class FirebaseServer {
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
