package com.gm.SCADA_Alert;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;



/**
 * Arrayadapter (for Android) with text filtering for the use with a TextWatcher.
 * Note: the objects in the List need a valid toString() method.
 * @author Tobias Schürg
 * 
 */
 

public class FilteredAlarmArrayAdapter extends ArrayAdapter<AlarmMessage> {
 
	private List<AlarmMessage> objects;
	private Context context;
	private Filter filter;
 
	public FilteredAlarmArrayAdapter(Context context, int resourceId, List<AlarmMessage> objects) {
		super(context, resourceId, objects);
		this.context = context;
		this.objects = objects;
	}
 
	@Override
	public int getCount() {
		return objects.size();
	}
 
	@Override
	public AlarmMessage getItem(int position) {
		return objects.get(position);
	}

	private class ViewHolder {
		ImageView img;
		TextView titulo;
		TextView subtitulo;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
 
                // TODO: inflate your view HERE ...
		View item = convertView;
		ViewHolder holder;
		
		if(item == null)
		{
			LayoutInflater inflater = (LayoutInflater)context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
			item = inflater.inflate(R.layout.alarm_listitem_layout, null);
			
			holder = new ViewHolder();
			holder.img = (ImageView)item.findViewById(R.id.Alarm_listitem_layout_img); 
			holder.titulo = (TextView)item.findViewById(R.id.Alarm_listitem_layout_Titulo);
			holder.subtitulo = (TextView)item.findViewById(R.id.Alarm_listitem_layout_SubTitulo);
			item.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)item.getTag();
		}
		
		holder.img.setImageResource(R.drawable.ic_alarm_stop);
		holder.titulo.setText(objects.get(position).getTimestamp());
		holder.subtitulo.setText(objects.get(position).getMessage());
		
		return(item);
	}
 
	@Override
	public Filter getFilter() {
		if (filter == null)
			filter = new AppFilter<AlarmMessage>(objects);
		return filter;
	}
 

	private class AppFilter<T> extends Filter {
 
		private ArrayList<AlarmMessage> sourceObjects;
 
		public AppFilter(List<AlarmMessage> objects) {
			sourceObjects = new ArrayList<AlarmMessage>();
			synchronized (this) {
				sourceObjects.addAll(objects);
			}
		}
 
		@Override
		protected FilterResults performFiltering(CharSequence chars) {
			String filterSeq = chars.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (filterSeq != null && filterSeq.length() > 0) {
				ArrayList<AlarmMessage> filter = new ArrayList<AlarmMessage>();
 
				for (AlarmMessage object : sourceObjects) {
					// the filtering itself:
					//Log.d("SICOPOLY3", object.toString().toLowerCase());
					//Log.d("SICOPOLY3", object.getypeStr());
					if (object.toString().toLowerCase().contains(filterSeq)) {
						filter.add((AlarmMessage) object);
					}
				}
				result.count = filter.size();
				result.values = filter;
			} else {
				// add all objects
				synchronized (this) {
					result.values = sourceObjects;
					result.count = sourceObjects.size();
				}
			}
			return result;
		}
	
 
		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			// NOTE: this function is *always* called from the UI thread.
			ArrayList<AlarmMessage> filtered = (ArrayList<AlarmMessage>) results.values;
			notifyDataSetChanged();
			clear();
			for (int i = 0, l = filtered.size(); i < l; i++)
				add((AlarmMessage) filtered.get(i));
			notifyDataSetInvalidated();
		}
		
	}
 
}