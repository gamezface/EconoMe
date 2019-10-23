package br.com.coutinhoanderson.econome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.model.User;

public class FundsAdapter extends RecyclerView.Adapter<FundsAdapter.ViewHolder> {
    List<User> users;
    private Context mContext;

    public FundsAdapter(Context context, List<User> users) {
        this.mContext = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.budget.setText(user.getBudget());
        holder.name.setText(user.getName());
        holder.phone.setText(user.getPhone());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView budget;
        TextView name;
        TextView phone;

        ViewHolder(View itemView) {
            super(itemView);
            budget = itemView.findViewById(R.id.user_budget);
            name = itemView.findViewById(R.id.user_name);
            phone = itemView.findViewById(R.id.user_phone);
        }

    }
}
