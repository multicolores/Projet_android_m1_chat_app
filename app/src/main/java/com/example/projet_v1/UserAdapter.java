package com.example.projet_v1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private ArrayList<User> userList;
    private OnItemClickListener onItemClickListener;


    /**
     * Interface to manage clicks on list items.
     */
    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    /**
     * Constructor to initialize adapter with context, user list and click listener.
     * @param context
     * @param userList
     * @param onItemClickListener
     */
    public UserAdapter(Context context, ArrayList<User> userList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.userList = userList;
        this.onItemClickListener = onItemClickListener;
    }


    /**
     * Method called to create a new ViewHolder when there aren't enough.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }


    /**
     * Method used to link user data to a ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Retrieves the current user from the list
        User user = userList.get(position);

        // Set the user's name in the corresponding TextView
        holder.nameTextView.setText(user.getName());

        // Set the user's distance in the corresponding TextView
        holder.distanceTextView.setText(String.format("%.2f km", user.getDistance()));

        // Sets an OnClickListener for the list item
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(user));
    }


    /**
     * Returns the total number of elements in the list.
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }


    /**
     * Internal class to manage the views of each list item.
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView distanceTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find the TextViews in the layout of the list item
            nameTextView = itemView.findViewById(R.id.textViewUserName);
            distanceTextView = itemView.findViewById(R.id.textViewDistance);
        }
    }
}
