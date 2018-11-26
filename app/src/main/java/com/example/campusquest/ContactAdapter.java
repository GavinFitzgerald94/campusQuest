package com.example.campusquest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    Context c;
    private Contact[] dataset;
    ArrayList<Contact> checkedContact = new ArrayList<>();
    private static final String TAG = ContactAdapter.class.getSimpleName();

    public interface ItemClickListener {
        void onItemClick(View v,int pos);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView contactName;
        TextView contactNumber;
        CheckBox myCheckBox;
        ItemClickListener itemClickListener;


        public ContactViewHolder(View itemView){
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            contactNumber = itemView.findViewById(R.id.contact_number);
            myCheckBox = itemView.findViewById(R.id.checkBox);

            myCheckBox.setOnClickListener(this);

        }

        public void setItemClickListener(ItemClickListener ic)
        {
            this.itemClickListener=ic;
        }


        @Override
        public void onClick(View v){
            this.itemClickListener.onItemClick(v,getLayoutPosition());
        }
    }

    public ContactAdapter(Context c, Contact[] dataset){
        this.dataset = dataset;
        this.c =c;
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
        final Contact contact = dataset[position];
        holder.contactName.setText(contact.getName());
        holder.contactNumber.setText(contact.getNumber());
        holder.myCheckBox.setChecked(contact.isSelected());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                CheckBox myCheckBox= (CheckBox) v;
                Contact currentContact= dataset[pos];

                if(myCheckBox.isChecked()) {
                    currentContact.setSelected(true);
                    checkedContact.add(currentContact);
                }
                else if(!myCheckBox.isChecked()) {
                    currentContact.setSelected(false);
                    checkedContact.remove(currentContact);
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        Log.d(TAG, "getItemCount: "+ dataset.length );
        return dataset.length;
    }



}
