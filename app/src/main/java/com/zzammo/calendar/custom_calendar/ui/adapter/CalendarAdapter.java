package com.zzammo.calendar.custom_calendar.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.zzammo.calendar.R;
import com.zzammo.calendar.custom_calendar.ui.viewmodel.CalendarHeaderViewModel;
import com.zzammo.calendar.custom_calendar.ui.viewmodel.CalendarViewModel;
import com.zzammo.calendar.custom_calendar.ui.viewmodel.EmptyViewModel;
import com.zzammo.calendar.database.Database;
import com.zzammo.calendar.database.Schedule;
import com.zzammo.calendar.databinding.CalendarHeaderBinding;
import com.zzammo.calendar.databinding.DayItemBinding;
import com.zzammo.calendar.databinding.EmptyDayBinding;
import com.zzammo.calendar.util.Time;

import java.util.ArrayList;
import java.util.Calendar;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


public class CalendarAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> {
    private final int HEADER_TYPE = 0;
    private final int EMPTY_TYPE = 1;
    private final int DAY_TYPE = 2;
    public Context mContext;
    public Database DB;
    ArrayList<Schedule> mScheduled;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public CalendarAdapter(Context mContext) {
        super(new DiffUtil.ItemCallback<Object>() {
            @Override
            public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                Gson gson = new Gson();
                return gson.toJson(oldItem).equals(gson.toJson(newItem));
            }
        });
        this.mContext=mContext;
        mScheduled=new ArrayList<>();
        DB = new Database(mContext);
    }




    @Override
    public int getItemViewType(int position) { //뷰타입 나누기
        Object item = getItem(position);
        if (item instanceof Long) {
            return HEADER_TYPE; //날짜 타입
        } else if (item instanceof String) {
            return EMPTY_TYPE; // 비어있는 일자 타입
        } else {
            return DAY_TYPE; // 일자 타입

        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) { // 날짜 타입
            CalendarHeaderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_calendar_header, parent, false);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true); //Span을 하나로 통합하기
            binding.getRoot().setLayoutParams(params);
            return new HeaderViewHolder(binding);
        } else if (viewType == EMPTY_TYPE) { //비어있는 일자 타입
            EmptyDayBinding binding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_day_empty, parent, false);
            return new EmptyViewHolder(binding);
        }
        DayItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_day, parent, false);// 일자 타입
        return new DayViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == HEADER_TYPE) { //날짜 타입 꾸미기
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            Object item = getItem(position);
            CalendarHeaderViewModel model = new CalendarHeaderViewModel();
            if (item instanceof Long) {
                model.setHeaderDate((Long) item);
            }
            holder.setViewModel(model);
        } else if (viewType == EMPTY_TYPE) { //비어있는 날짜 타입 꾸미기
            EmptyViewHolder holder = (EmptyViewHolder) viewHolder;
            EmptyViewModel model = new EmptyViewModel();
            holder.setViewModel(model);
        } else if (viewType == DAY_TYPE) { // 일자 타입 꾸미기
            DayViewHolder holder = (DayViewHolder) viewHolder;
            Object item = getItem(position);
            CalendarViewModel model = new CalendarViewModel();
            if (item instanceof Calendar) {
                model.setCalendar((Calendar) item);
            }
            Log.d("minseok",((Calendar)item).get(Calendar.YEAR)+" "+((Calendar)item).get(Calendar.MONTH)+" "+((Calendar)item).get(Calendar.DATE)+"");
            Long startmill = Time.CalendarToMill((Calendar)item);
            mScheduled.clear();
            DB.loadAllScheduleDuring(startmill,startmill+Time.ONE_DAY,mScheduled);
            if(mScheduled.size()>0)Log.d("minseok",mScheduled.size()+"");
            if(mScheduled.size()>3)holder.binding.scheduleMainMore.setVisibility(View.VISIBLE);
            while(mScheduled.size()<3){
                mScheduled.add(null);
            }
            model.mSchedule.setValue(mScheduled);
            holder.setViewModel(model);
        }
    }


    private class HeaderViewHolder extends RecyclerView.ViewHolder { //날짜 타입 ViewHolder
        private CalendarHeaderBinding binding;

        private HeaderViewHolder(@NonNull CalendarHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setViewModel(CalendarHeaderViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }


    private class EmptyViewHolder extends RecyclerView.ViewHolder { // 비어있는 요일 타입 ViewHolder
        private EmptyDayBinding binding;

        private EmptyViewHolder(@NonNull EmptyDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setViewModel(EmptyViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }

    }

    private class DayViewHolder extends RecyclerView.ViewHolder {// 요일 타입 ViewHolder
        private DayItemBinding binding;

        private DayViewHolder(@NonNull DayItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setViewModel(CalendarViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }
}
