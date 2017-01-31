package com.dareu.mobile.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.web.dto.response.entity.CategoryDescription;

import java.util.List;


/**
 * Created by jose.rubalcaba on 10/12/2016.
 */

public class CategoriesAdapter extends ArrayAdapter<CategoryDescription> {

    private Context context;
    private List<CategoryDescription> categories;

    public CategoriesAdapter(Context context, List<CategoryDescription> categories) {
        super(context, R.layout.category_item, categories);
        this.context = context;
        this.categories = categories;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.category_item, parent, false);
        TextView  nameView = (TextView) row.findViewById(R.id.categoryItemNameView);
        TextView  descView = (TextView) row.findViewById(R.id.categoryItemDescView);
        nameView.setText(categories.get(position).getName());
        descView.setText(categories.get(position).getDescription());
        return row;
    }


    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.category_item, parent, false);
        TextView  nameView = (TextView) row.findViewById(R.id.categoryItemNameView);
        TextView  descView = (TextView) row.findViewById(R.id.categoryItemDescView);
        nameView.setText(categories.get(position).getName());
        descView.setText(categories.get(position).getDescription());
        return row;
    }
}
