package com.example.campusquest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<Contact> dataset;
    final private ContactItemClickListener mOnClickListener;
    private static final String TAG = ContactAdapter.class.getSimpleName();

    public interface ContactItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }
    public class ContactViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        TextView contactName;
        TextView contactNumber;

        public ContactViewHolder(View itemView){
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            contactNumber = itemView.findViewById(R.id.contact_number);
            itemView.setOnClickListener(this);
        }

        public void bind(int itemIndex){
            Contact contact = dataset.get(itemIndex);
            contactName.setText(contact.getName());
            contactNumber.setText(contact.getNumber());
        }

        @Override
        public void onClick(View itemView){
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    public ContactAdapter(List<Contact> dataset, ContactItemClickListener listener){
        this.dataset = dataset;
        this.mOnClickListener = listener;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.contact_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ContactViewHolder viewHolder= new ContactViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount(){
        Log.d(TAG, "getItemCount: "+ dataset.size() );
        return dataset.size();
    }



}
