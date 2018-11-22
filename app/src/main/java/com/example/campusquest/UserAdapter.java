package com.example.campusquest;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

//public class UserAdapter extends ArrayAdapter<User> {
//    private int resourceId;
//    public UserAdapter(Context context, int listViewResourceId, List<User> objects){
//        super(context,listViewResourceId,objects);
//        resourceId = listViewResourceId;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent){
//        User user = getItem(position);
//        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
//        TextView rank = (TextView) view.findViewById(R.id.rank_text);
//        TextView steps = (TextView) view.findViewById(R.id.steps_text);
//        TextView name = (TextView) view.findViewById(R.id.name_text);
//        ImageView userImage = (ImageView) view.findViewById(R.id.user_image);
//        userImage.setImageResource(user.getImageId());
//        name.setText(user.getName());
//        steps.setText(Integer.toString(user.getSteps()));
//        rank.setText(Integer.toString(user.getRank()));
//        return view;
//    }
//}

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private List<User> userList;
    private static final String TAG = ContactAdapter.class.getSimpleName();

    public class UserViewHolder extends RecyclerView.ViewHolder{
        TextView rank;
        TextView steps;
        TextView name;
        ImageView userImage;

        public UserViewHolder(View view) {
            super(view);
            rank = view.findViewById(R.id.rank_text);
            steps =  view.findViewById(R.id.steps_text);
            name = view.findViewById(R.id.name_text);
            userImage =  view.findViewById(R.id.user_image);
        }

        public void bind(int itemIndex){
            User user = userList.get(itemIndex);
            Log.d(TAG, "bind: "+ user.getRank()+user.getName());
            rank.setText(Integer.toString(user.getRank()));
            steps.setText(Integer.toString(user.getSteps()));
            name.setText(user.getName());
            userImage.setImageResource(user.getImageId());
        }
    }

    public UserAdapter(List<User> users){
        this.userList = users;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup viewgroup, int viewType){
        Context context = viewgroup.getContext();
        int layoutIdForListItem = R.layout.user_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem,viewgroup, shouldAttachToParentImmediately);
        UserViewHolder viewHolder = new UserViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position){
        Log.d(TAG,"#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount(){
        Log.d(TAG, "getItemCount: " + userList.size());
        return userList.size();
    }
}
