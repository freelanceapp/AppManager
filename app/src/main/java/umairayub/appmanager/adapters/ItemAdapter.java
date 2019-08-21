package umairayub.appmanager.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import umairayub.appmanager.Item;
import umairayub.appmanager.R;
import umairayub.appmanager.activities.MainActivity;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private OnItemClickListener mListener;
    private List<Item> itemList;
    private List<Item> itemListCopy;
    public int lastPosition = -1;
    Context context;
    MainActivity mainActivity;

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onInfoClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView appname, packname, version,appsize;
        public ImageView img;
        public Button btnd,btni;
        public CheckBox checkBox;
        MainActivity mainActivity;
        public MyViewHolder(View view, final OnItemClickListener listener,MainActivity mainActivity) {
            super(view);
            appname = (TextView) view.findViewById(R.id.tvN);
            packname = (TextView) view.findViewById(R.id.tvP);
            version = (TextView) view.findViewById(R.id.tvV);
            appsize = (TextView) view.findViewById(R.id.tvSize);
            img = (ImageView) view.findViewById(R.id.imgV);
            btnd = (Button) view.findViewById(R.id.btnd);
            btni = (Button) view.findViewById(R.id.btni);
            checkBox = (CheckBox)view.findViewById(R.id.checkBox);
            checkBox.setOnClickListener(this);
            this.mainActivity = mainActivity;

            view.setOnLongClickListener((View.OnLongClickListener) context);
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

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Item item = itemList.get(adapterPosition);
            if(item.isSelected()){
                itemList.get(adapterPosition).setSelected(false);
                ((MainActivity)context).prepareSelection(view,adapterPosition);
                checkBox.setChecked(false);
            }else {
                item.setSelected(true);
                checkBox.setChecked(true);
            }
            ((MainActivity)context).prepareSelection(view,adapterPosition);

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

       MyViewHolder myviewholder = new MyViewHolder(itemView,mListener,mainActivity);

        return myviewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Item item = itemList.get(position);
        holder.appname.setText(item.getName());
        holder.packname.setText(item.getPack());
        holder.version.setText("Version "+item.getVersionname());
        holder.appsize.setText("Size "+MainActivity.bytesToMb(item.getAppsize()));
        holder.img.setImageDrawable(item.getIcon());
        if(item.isSelected()){
            holder.checkBox.setChecked(true);
            item.setSelected(true);
        }else {
            holder.checkBox.setChecked(false);
            item.setSelected(false);
        }
        if(!MainActivity.is_in_Action){
            holder.checkBox.setVisibility(View.GONE);
            holder.btni.setVisibility(View.VISIBLE);
            holder.btnd.setVisibility(View.VISIBLE);
        }else {
            holder.btnd.setVisibility(View.GONE);
            holder.btni.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.VISIBLE);




        }

        /* load the animation and fire it... */
        Animation animation = AnimationUtils.loadAnimation(context, (holder.getAdapterPosition() > lastPosition) ? R.anim.anim_down : R.anim.anim_up);
        holder.itemView.startAnimation(animation);

        /* assign current adapter position to the 'lastPosition' var */
        lastPosition = holder.getAdapterPosition();
    }



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

    @Override
    public int getItemCount() {
        return itemList.size();
    }

//    /* override the following method and add the code... */
//    @Override
//    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
//        super.onViewDetachedFromWindow(holder);
//        holder.itemView.clearAnimation();
//    }
}