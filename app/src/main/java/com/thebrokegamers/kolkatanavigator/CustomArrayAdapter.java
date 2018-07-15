package com.thebrokegamers.kolkatanavigator;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomArrayAdapter extends ArrayAdapter<String> implements Filterable{
    private LayoutInflater layoutInflater;
    private ArrayList<String> mStoppages;
    private Typeface font;

    private Filter mFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return (String)resultValue;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null) {
                ArrayList<String> suggestions = new ArrayList<String>();
                for (String c : mStoppages) {
                    // Note: change the "contains" to "startsWith" if you only want starting matches
                    if (c.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(c);
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                // we have filtered results
                addAll((ArrayList<String>) results.values);
            } else {
                // no filter, add entire original list back in
                addAll(mStoppages);
            }
            notifyDataSetChanged();
        }
    };

    public CustomArrayAdapter(Context context, int layout_resource_id, ArrayList<String> stoppages) {
        super(context, layout_resource_id, stoppages);
        // copy all the customers into a master list
        mStoppages = new ArrayList<String>(stoppages);
        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        font = Typeface.createFromAsset(context.getAssets(), "Exo2-Regular.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.suggestions_listview, parent, false);


        String ch = getItem(position);

        TextView name = (TextView) customView.findViewById(R.id.stringSuggestion);
        name.setText(ch.toUpperCase());
        name.setTypeface(font);

        return customView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}
