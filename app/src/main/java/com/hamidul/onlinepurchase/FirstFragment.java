package com.hamidul.onlinepurchase;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class FirstFragment extends Fragment {
    RecyclerView recyclerView;
    LinearLayout progressBar;
    HashMap<String ,String > hashMap;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList();
    ArrayList<HashMap<String,String>> cartList = new ArrayList<>();
    LottieAnimationView animationView;
    SwipeRefreshLayout swipeRefreshLayout;
    Toolbar toolbar;
    public static MyAdapter myAdapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Toast toast;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    StyleableToast styleableToast;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View myView = inflater.inflate(R.layout.fragment_first, container, false);

        progressBar = myView.findViewById(R.id.progressBar);
        recyclerView = myView.findViewById(R.id.recyclerView);
        animationView = myView.findViewById(R.id.animationView);
        //swipeRefreshLayout = myView.findViewById(R.id.swipeRefreshLayout);
        toolbar = myView.findViewById(R.id.toolBar);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Set the Toolbar as the ActionBar for this fragment
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        loadData();
        cartUpdate();

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                loadData();
//            }
//        });

        return myView;
    }// onCreateView end

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem!=null){

            View myView = LayoutInflater.from(getActivity()).inflate(R.layout.custom_search_view,null);

            SearchView searchView = myView.findViewById(R.id.searchView);

            searchItem.setActionView(myView);
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

            searchView.requestFocus();

        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.addCart){
            MainActivity.backButton = true;
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout,new CartList());
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadData(){
        arrayList = new ArrayList();
        progressBar.setVisibility(View.VISIBLE);

        String url = "https://smhamidulcodding.000webhostapp.com/ecommerce_app/view.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                //swipeRefreshLayout.setRefreshing(false);
                for (int x = 0; x<response.length(); x++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(x);
                        String id = jsonObject.getString("id");
                        String image = jsonObject.getString("image");
                        String name = jsonObject.getString("name");
                        String weight = jsonObject.getString("weight");
                        String tp = jsonObject.getString("tp");
                        String price = jsonObject.getString("price");

                        hashMap = new HashMap<>();
                        hashMap.put("id",id);
                        hashMap.put("image",image);
                        hashMap.put("name",name);
                        hashMap.put("weight",weight);
                        hashMap.put("tp",tp);
                        hashMap.put("price",price);
                        arrayList.add(hashMap);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }// for loop end
                myAdapter = new MyAdapter(arrayList);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
                //recyclerView.setHasFixedSize(true);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                //swipeRefreshLayout.setRefreshing(false);
            }
        });
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayRequest);

    }// loadData End

    private class MyAdapter extends RecyclerView.Adapter<FirstFragment.MyAdapter.myViewHolder>{

        ArrayList<HashMap<String,String>> itemList;
        ArrayList<HashMap<String,String>> filterList;
        String queryText = "";
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
            TextView name,weight,tp,price;
            LottieAnimationView lottieAnimationView;
            LinearLayout addCart;
            public myViewHolder(@NonNull View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.imageView);
                name = itemView.findViewById(R.id.tvName);
                weight = itemView.findViewById(R.id.tvWeight);
                //tp = itemView.findViewById(R.id.tvTp);
                price = itemView.findViewById(R.id.tvPrice);
                lottieAnimationView = itemView.findViewById(R.id.lottieAnimationView);
                addCart = itemView.findViewById(R.id.addCart);

            }
        }

        @Override
        public void onBindViewHolder(@NonNull FirstFragment.MyAdapter.myViewHolder holder, int position) {

            hashMap = (HashMap<String, String>) filterList.get(position);

            String id = hashMap.get("id");
            String image = hashMap.get("image");
            String name = hashMap.get("name");
            String weight = hashMap.get("weight");
            String price = hashMap.get("price");

            if (name.contains("Pringles")){
                float tp = (float) (Float.parseFloat(price)/1.12);
                holder.price.setText("MRP : "+price+" TK\nTP : "+decimalFormat.format(tp)+" TK");
            }
            else {
                float tp = (float) (Float.parseFloat(price)/1.15);
                holder.price.setText("MRP : "+price+" TK\nTP : "+decimalFormat.format(tp)+" TK");
            }

            if (queryText!=null && !queryText.isEmpty()){

                int starPos = name.toLowerCase().indexOf(queryText.toLowerCase());
                int endPos = starPos+queryText.length();

                if (starPos!= -1){

                    Spannable spannable = new SpannableString(name);
                    ColorStateList colorStateList = new ColorStateList(new int[][] {new int[] {}},new int[] {Color.parseColor("#009688")});
                    TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null, Typeface.NORMAL,-1,colorStateList,null);
                    spannable.setSpan(textAppearanceSpan,starPos,endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    holder.name.setText(spannable);

                }else {
                    holder.name.setText(name);
                }

                int wetStartPos = weight.toLowerCase().indexOf(queryText.toLowerCase());
                int wetEndPos = wetStartPos+queryText.length();

                if (wetStartPos!=-1){

                    Spannable spannable = new SpannableString(weight);
                    ColorStateList colorStateList = new ColorStateList(new int[][]{new int[]{}},new int[]{Color.YELLOW});
                    TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null,Typeface.NORMAL,-1,colorStateList,null);
                    spannable.setSpan(textAppearanceSpan,wetStartPos,wetEndPos,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    holder.weight.setText("Weight : "+spannable);

                }else {
                    holder.weight.setText("Weight : "+weight);
                }


            }else {
                holder.name.setText(name);
                holder.weight.setText("Weight : "+weight);
            }

//            holder.name.setText(name);
//            holder.weight.setText("Weight : "+weight);
            //holder.tp.setText("TP : "+tp);
            //holder.price.setText("MRP : "+price);
            holder.addCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean b=true;

                    for (HashMap<String,String> check : cartList){
                        if (check.get("id").equals(id)){
//                            toastMessage("Previously added to cart");
                            if (styleableToast!=null) styleableToast.cancel();
                            styleableToast = StyleableToast.makeText(getActivity(), "Previously added to cart", Toast.LENGTH_LONG, R.style.mytoast);
                            styleableToast.show();
                            b = false;
                        }
                    }

                    if(b){
//                        toastMessage("Added to cart");
                        if (styleableToast!=null) styleableToast.cancel();
                        styleableToast = StyleableToast.makeText(getActivity(), "Added to cart", Toast.LENGTH_LONG, R.style.mytoast);
                        styleableToast.show();
                        saveData(id);
                    }

                }
            });

            Picasso.get()
                    .load(image)
                    .into(holder.imageView, new Callback() {
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
                queryText = "";
                filterList.addAll(itemList);
            }else {

                queryText = query.toString();

                for (HashMap<String,String> item : itemList){
                    if (item.get("name").toLowerCase().contains(query)  || item.get("weight").toLowerCase().contains(query)){
                        filterList.add(item);
                    }
                }
            }

            notifyDataSetChanged();

        }

    }// MyAdapter End

    private void saveData(String id){
        Gson gson = new Gson();
        hashMap = new HashMap<>();
        hashMap.put("id",id);
        cartList.add(hashMap);
        String json = gson.toJson(cartList);
        editor.putString("cartList",json);
        editor.apply();
    }

    private void toastMessage(String message){
        if (toast!=null) toast.cancel();
        toast = Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT);
        toast.show();
    }

    private void cartUpdate(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("cartList",null);
        Type type = new TypeToken<ArrayList<HashMap<String,String>>>(){
        }.getType();
        cartList = gson.fromJson(json,type);
        if (cartList == null){
            cartList = new ArrayList<>();
        }

    }

}// Fragment End