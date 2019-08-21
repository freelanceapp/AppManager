package umairayub.appmanager;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private OnItemClickListener mListener;
    private List<Item> itemList;
    private List<Item> itemListCopy;
    public int lastPosition = -1;
    private Context context;




    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onInfoClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView appname, packname, version;
        public ImageView img;
        public Button btnd,btni;

        public MyViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            appname = (TextView) view.findViewById(R.id.tvN);
            packname = (TextView) view.findViewById(R.id.tvP);
            version = (TextView) view.findViewById(R.id.tvV);
            img = (ImageView) view.findViewById(R.id.imgV);
            btnd = (Button) view.findViewById(R.id.btnd);
            btni = (Button) view.findViewById(R.id.btni);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            btnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
            btni.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onInfoClick(position);
                        }
                    }
                }
            });
        }
    }


    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
        this.itemListCopy = new ArrayList<>(itemList);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        context = parent.getContext();

        return new MyViewHolder(itemView,mListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.appname.setText(item.getName());
        holder.packname.setText(item.getPack());
        holder.version.setText(item.getVersionname());
        holder.img.setImageDrawable(item.getIcon());



        /* load the animation and fire it... */
        Animation animation = AnimationUtils.loadAnimation(context, (holder.getAdapterPosition() > lastPosition) ? R.anim.anim_down : R.anim.anim_up);
        holder.itemView.startAnimation(animation);

        /* assign current adapter position to the 'lastPosition' var */
        lastPosition = holder.getAdapterPosition();
    }

//    @Override
//    public Filter getFilter() {
//        return filter();
//    }

    public void filter(String text) {
        itemList.clear();
        if(text.isEmpty()){
            itemList.addAll(itemListCopy);
        } else{
            text = text.toLowerCase();
            for(Item item: itemListCopy){
                if(item.getName().toLowerCase().contains(text)){
                    itemList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            itemList.clear();
//            itemList.addAll((List) results.values);
//            notifyDataSetChanged();
//        }
//    };
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /* override the following method and add the code... */
    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
}