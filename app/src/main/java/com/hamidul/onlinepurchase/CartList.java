package com.hamidul.onlinepurchase;

import android.content.Context;
import android.content.SharedPreferences;
import android.health.connect.datatypes.StepsCadenceRecord;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class CartList extends Fragment {

    ArrayList<ModelClass> cartList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    GridView gridView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_cart_list, container, false);

        gridView = myView.findViewById(R.id.gridView);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        loadCartData();

        MyAdapter myAdapter = new MyAdapter();
        gridView.setAdapter(myAdapter);


        return myView;
    }

    public class MyAdapter extends BaseAdapter{
        LayoutInflater layoutInflater;
        @Override
        public int getCount() {
            return cartList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View cartView = layoutInflater.inflate(R.layout.item,parent,false);

            TextView tvName = cartView.findViewById(R.id.tvName);
            TextView tvWeight = cartView.findViewById(R.id.tvWeight);
            TextView tvPrice = cartView.findViewById(R.id.tvPrice);

            String name = cartList.get(position).name;
            String weight = cartList.get(position).weight;
            String price = cartList.get(position).price;

            tvName.setText(name);
            tvWeight.setText(weight);
            tvPrice.setText(price);


            return cartView;
        }
    }

    public void loadCartData(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list",null);
        Type type = new TypeToken<ArrayList<ModelClass>>(){
        }.getType();
        cartList = gson.fromJson(json,type);
        if (cartList==null){
            cartList = new ArrayList<>();
        }

    }




}