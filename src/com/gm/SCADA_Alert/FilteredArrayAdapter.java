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
 

public class FilteredArrayAdapter extends ArrayAdapter<LogMessage> {
 
	private List<LogMessage> objects;
	private Context context;
	private Filter filter;
 
	public FilteredArrayAdapter(Context context, int resourceId, List<LogMessage> objects) {
		super(context, resourceId, objects);
		this.context = context;
		this.objects = objects;
	}
 
	@Override
	public int getCount() {
		return objects.size();
	}
 
	@Override
	public LogMessage getItem(int position) {
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
			item = inflater.inflate(R.layout.log_listitem_layout, null);
			
			holder = new ViewHolder();
			holder.img = (ImageView)item.findViewById(R.id.log_listitem_layout_img);
			holder.titulo = (TextView)item.findViewById(R.id.log_listitem_layout_Titulo);
			holder.subtitulo = (TextView)item.findViewById(R.id.log_listitem_layout_SubTitulo);
			item.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)item.getTag();
		}
		
		if (objects.get(position).getType() == 1) {
			holder.img.setImageResource(R.drawable.ic_error);
		}
		else if (objects.get(position).getType() == 2) {
			holder.img.setImageResource(R.drawable.ic_alert);
		}
		else {
			holder.img.setImageResource(R.drawable.ic_info);
		}			
		holder.titulo.setText(objects.get(position).getTimestamp());
		holder.subtitulo.setText(objects.get(position).getMessage());
		
		return(item);
	}
 
	@Override
	public Filter getFilter() {
		if (filter == null)
			filter = new AppFilter<LogMessage>(objects);
		return filter;
	}
 

	private class AppFilter<T> extends Filter {
 
		private ArrayList<LogMessage> sourceObjects;
 
		public AppFilter(List<LogMessage> objects) {
			sourceObjects = new ArrayList<LogMessage>();
			synchronized (this) {
				sourceObjects.addAll(objects);
			}
		}
 
		@Override
		protected FilterResults performFiltering(CharSequence chars) {
			String filterSeq = chars.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (filterSeq != null && filterSeq.length() > 0) {
				ArrayList<LogMessage> filter = new ArrayList<LogMessage>();
 
				for (LogMessage object : sourceObjects) {
					// the filtering itself:
					//Log.d("SICOPOLY3", object.toString().toLowerCase());
					//Log.d("SICOPOLY3", object.getypeStr());
					if (((object.toString().toLowerCase().contains(filterSeq))) || (object.getypeStr().equalsIgnoreCase(filterSeq))) {
						filter.add((LogMessage) object);
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
			ArrayList<LogMessage> filtered = (ArrayList<LogMessage>) results.values;
			notifyDataSetChanged();
			clear();
			for (int i = 0, l = filtered.size(); i < l; i++)
				add((LogMessage) filtered.get(i));
			notifyDataSetInvalidated();
		}
		
	}
 
}