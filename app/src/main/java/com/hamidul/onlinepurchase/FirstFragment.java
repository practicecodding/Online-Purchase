package com.hamidul.onlinepurchase;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FirstFragment extends Fragment {
    RecyclerView recyclerView;
    LinearLayout progressBar;
    HashMap<String ,String > hashMap;
    ArrayList arrayList = new ArrayList();
    LottieAnimationView animationView;
    SearchView searchView;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView search;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_first, container, false);

        progressBar = myView.findViewById(R.id.progressBar);
        recyclerView = myView.findViewById(R.id.recyclerView);
        animationView = myView.findViewById(R.id.animationView);
        searchView = myView.findViewById(R.id.searchView);
        swipeRefreshLayout = myView.findViewById(R.id.swipeRefreshLayout);
        search = myView.findViewById(R.id.search);

        loadData();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setVisibility(View.VISIBLE);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        return myView;
    }

    private void loadData(){
        arrayList = new ArrayList();
        progressBar.setVisibility(View.VISIBLE);

        String url = "https://smhamidulcodding.000webhostapp.com/ecommerce_app/view.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                for (int x = 0; x<response.length(); x++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(x);
                        String id = jsonObject.getString("id");
                        String image = jsonObject.getString("image");
                        String name = jsonObject.getString("name");
                        String weight = jsonObject.getString("weight");
                        String price = jsonObject.getString("price");

                        hashMap = new HashMap<>();
                        hashMap.put("id",id);
                        hashMap.put("image",image);
                        hashMap.put("name",name);
                        hashMap.put("weight",weight);
                        hashMap.put("price",price);
                        arrayList.add(hashMap);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }// for loop end
                MyAdapter myAdapter = new MyAdapter(arrayList);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
//                recyclerView.setHasFixedSize(true);
//                recyclerView.setHasFixedSize(true);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        myAdapter.filter(newText);
                        return true;
                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayRequest);

    }// loadData End

    private class MyAdapter extends RecyclerView.Adapter<FirstFragment.MyAdapter.myViewHolder>{

        ArrayList<HashMap<String,String>> itemList;
        ArrayList<HashMap<String,String>> filterList;
        public MyAdapter (ArrayList<HashMap<String,String>> itemList){
            this.itemList = itemList;
            this.filterList = new ArrayList<>(itemList);
        }

        @NonNull
        @Override
        public FirstFragment.MyAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View myView = layoutInflater.inflate(R.layout.item,parent,false);

            return new myViewHolder(myView);
        }

        private class myViewHolder extends RecyclerView.ViewHolder{
            ImageView imageView;
            TextView name,weight,price;
            LottieAnimationView lottieAnimationView;
            LinearLayout addCart;
            public myViewHolder(@NonNull View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.imageView);
                name = itemView.findViewById(R.id.tvName);
                weight = itemView.findViewById(R.id.tvWeight);
                price = itemView.findViewById(R.id.tvPrice);
                lottieAnimationView = itemView.findViewById(R.id.lottieAnimationView);
                addCart = itemView.findViewById(R.id.addCart);

            }
        }

        @Override
        public void onBindViewHolder(@NonNull FirstFragment.MyAdapter.myViewHolder holder, int position) {

            hashMap = (HashMap<String, String>) filterList.get(position);

            String name = hashMap.get("name");
            String weight = hashMap.get("weight");
            String price = hashMap.get("price");

            holder.name.setText(name);
            holder.weight.setText(weight);
            holder.price.setText("BDT : "+price);

//            holder.addCart.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });

            Picasso.get().load(hashMap.get("image")).into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                    holder.lottieAnimationView.setVisibility(View.GONE);
                    holder.imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    holder.imageView.setVisibility(View.GONE);
                    holder.lottieAnimationView.setVisibility(View.VISIBLE);
                }
            });

        }

        @Override
        public int getItemCount() {
            return filterList.size();
        }

        public void filter (String query){
            query = query.toLowerCase();
            filterList.clear();
            if (query.isEmpty()){
                filterList.addAll(itemList);
                searchView.setVisibility(View.GONE);
            }else {
                for (HashMap<String,String> item : itemList){
                    if (item.get("name").toLowerCase().contains(query)  || item.get("weight").toLowerCase().contains(query)){
                        filterList.add(item);
                    }
                }
            }

            notifyDataSetChanged();

        }

    }// MyAdapter End

}