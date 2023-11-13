package com.hamidul.onlinepurchase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayout progressBar;
    HashMap<String ,String > hashMap;
    ArrayList arrayList = new ArrayList();
    LottieAnimationView animationView;
    SearchView searchView;
    BroadcastReceiver broadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

//        progressBar = findViewById(R.id.progressBar);
//        recyclerView = findViewById(R.id.recyclerView);
//        animationView = findViewById(R.id.animationView);
//        searchView = findViewById(R.id.searchView);

        broadcastReceiver = new ConnectionReceiverMain();

        registerNetwork();

    }

//    private void loadData(){
//        arrayList = new ArrayList();
//        progressBar.setVisibility(View.VISIBLE);
//
//        String url = "https://smhamidulcodding.000webhostapp.com/ecommerce_app/view.php";
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                progressBar.setVisibility(View.GONE);
//                for (int x = 0; x<response.length(); x++){
//                    try {
//                        JSONObject jsonObject = response.getJSONObject(x);
//                        String id = jsonObject.getString("id");
//                        String image = jsonObject.getString("image");
//                        String name = jsonObject.getString("name");
//                        String weight = jsonObject.getString("weight");
//                        String price = jsonObject.getString("price");
//
//                        hashMap = new HashMap<>();
//                        hashMap.put("id",id);
//                        hashMap.put("image",image);
//                        hashMap.put("name",name);
//                        hashMap.put("weight",weight);
//                        hashMap.put("price",price);
//                        arrayList.add(hashMap);
//
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                }// for loop end
//                MyAdapter myAdapter = new MyAdapter(arrayList);
//                recyclerView.setAdapter(myAdapter);
//                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
////                recyclerView.setHasFixedSize(true);
////                recyclerView.setHasFixedSize(true);
//
//                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                    @Override
//                    public boolean onQueryTextSubmit(String query) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onQueryTextChange(String newText) {
//                        myAdapter.filter(newText);
//                        return true;
//                    }
//                });
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
//        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        requestQueue.add(jsonArrayRequest);
//
//    }// loadData End


//    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.myViewHolder>{
//
//        ArrayList<HashMap<String,String>> itemList;
//        ArrayList<HashMap<String,String>> filterList;
//        public MyAdapter (ArrayList<HashMap<String,String>> itemList){
//            this.itemList = itemList;
//            this.filterList = new ArrayList<>(itemList);
//        }
//
//        private class myViewHolder extends RecyclerView.ViewHolder{
//            ImageView imageView;
//            TextView name,weight,price;
//            LottieAnimationView lottieAnimationView;
//            public myViewHolder(@NonNull View itemView) {
//                super(itemView);
//
//                imageView = itemView.findViewById(R.id.imageView);
//                name = itemView.findViewById(R.id.tvName);
//                weight = itemView.findViewById(R.id.tvWeight);
//                price = itemView.findViewById(R.id.tvPrice);
//                lottieAnimationView = itemView.findViewById(R.id.lottieAnimationView);
//
//            }
//        }
//
//
//        @NonNull
//        @Override
//        public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            LayoutInflater layoutInflater = getLayoutInflater();
//            View myView = layoutInflater.inflate(R.layout.item,parent,false);
//
//            return new myViewHolder(myView);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
//
//            hashMap = (HashMap<String, String>) filterList.get(position);
//
//            holder.name.setText(hashMap.get("name"));
//            holder.weight.setText(hashMap.get("weight"));
//            holder.price.setText("BDT : "+hashMap.get("price"));
//
//            Picasso.get().load(hashMap.get("image")).into(holder.imageView, new Callback() {
//                @Override
//                public void onSuccess() {
//                    holder.lottieAnimationView.setVisibility(View.GONE);
//                    holder.imageView.setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    holder.imageView.setVisibility(View.GONE);
//                    holder.lottieAnimationView.setVisibility(View.VISIBLE);
//                }
//            });
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return filterList.size();
//        }
//
//        public void filter (String query){
//            query = query.toLowerCase();
//            filterList.clear();
//            if (query.isEmpty()){
//                filterList.addAll(itemList);
//            }else {
//                for (HashMap<String,String> item : itemList){
//                    if (item.get("name").toLowerCase().contains(query)  || item.get("weight").toLowerCase().contains(query)){
//                        filterList.add(item);
//                    }
//                }
//            }
//
//            notifyDataSetChanged();
//
//        }
//
//    }// MyAdapter End

    protected void registerNetwork (){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }
    protected void unRegisterNetwork(){
        try {
            unregisterReceiver(broadcastReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterNetwork();
    }

    public class ConnectionReceiverMain extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (isConnected()){
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout,new FirstFragment());
//                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout,new SecondFragment());
//                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }


        }

        public boolean isConnected(){

            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return (networkInfo!=null && networkInfo.isConnected());
            }catch (NullPointerException e){
                e.printStackTrace();
                return false;
            }

        }

    }


}