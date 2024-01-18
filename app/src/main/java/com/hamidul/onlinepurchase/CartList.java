package com.hamidul.onlinepurchase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.health.connect.datatypes.StepsCadenceRecord;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class CartList extends Fragment {
    ArrayList<HashMap<String,String>> cartList = new ArrayList<>();
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    HashMap<String,String> hashMap;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    GridView gridView;
    LinearLayout full,empty;
    String string="";
    LinearLayout progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_cart_list, container, false);

        gridView = myView.findViewById(R.id.gridView);
        full = myView.findViewById(R.id.full);
        empty = myView.findViewById(R.id.empty);
        progressBar = myView.findViewById(R.id.progressBar);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadCartData();
        loadData();

        return myView;
    }

    public class MyAdapter extends BaseAdapter{
        LayoutInflater layoutInflater;
        @Override
        public int getCount() {
            return arrayList.size();
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
            View cartView = layoutInflater.inflate(R.layout.cart_item,parent,false);

            TextView tvName = cartView.findViewById(R.id.tvName);
            TextView tvWeight = cartView.findViewById(R.id.tvWeight);
            TextView tvPrice = cartView.findViewById(R.id.tvPrice);
            ImageView imageView = cartView.findViewById(R.id.imageView);
            LottieAnimationView lottieAnimationView = cartView.findViewById(R.id.lottieAnimationView);
            LinearLayout delete = cartView.findViewById(R.id.delete);
            LinearLayout orderNow = cartView.findViewById(R.id.orderNow);

            hashMap = (HashMap<String, String>) arrayList.get(position);

            String id = hashMap.get("id");
            String image = hashMap.get("image");
            String name = hashMap.get("name");
            String weight = hashMap.get("weight");
            String price = hashMap.get("price");

            tvName.setText(name);
            tvWeight.setText(weight);
            tvPrice.setText("BDT : "+price);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayList.remove(position);
                    cartList.remove(position);
                    Gson gson = new Gson();
                    String json = gson.toJson(cartList);
                    editor.putString("cartList",json);
                    editor.apply();
                    if (arrayList==null || arrayList.size()<1){
                        arrayList = new ArrayList<>();
                        empty.setVisibility(View.VISIBLE);
                        full.setVisibility(View.GONE);
                    }
                    MyAdapter myAdapter = new MyAdapter();
                    gridView.setAdapter(myAdapter);
                }
            });

            orderNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.user_details,null);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setView(view);

                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.user_details);

                    EditText edName = dialog.findViewById(R.id.edName);
                    EditText edNumber = dialog.findViewById(R.id.edNumber);
                    EditText edEmail = dialog.findViewById(R.id.edEmail);
                    EditText edAddress = dialog.findViewById(R.id.edAddress);
                    EditText edQuantity = dialog.findViewById(R.id.edQuantity);
                    Button buttonSubmit = dialog.findViewById(R.id.buttonSubmit);

//                    final AlertDialog alertDialog = builder.create();

                    TextWatcher watcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String name = edName.getText().toString();
                            String number = edNumber.getText().toString();
                            String email = edEmail.getText().toString();
                            String address = edAddress.getText().toString();
                            String quantity = edQuantity.getText().toString();

                            buttonSubmit.setEnabled(!name.isEmpty() && number.length()==11 && !address.isEmpty() && !quantity.isEmpty());

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    };

                    edName.addTextChangedListener(watcher);
                    edNumber.addTextChangedListener(watcher);
                    edEmail.addTextChangedListener(watcher);
                    edAddress.addTextChangedListener(watcher);
                    edQuantity.addTextChangedListener(watcher);

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    dialog.getWindow().setGravity(Gravity.BOTTOM);
                    dialog.setCancelable(true);
                    dialog.show();

                    buttonSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String customerName = edName.getText().toString();
                            String customerNumber = edNumber.getText().toString();
                            String customerEmail = edEmail.getText().toString();
                            String customerAddress = edAddress.getText().toString();
                            String productQuantity = edQuantity.getText().toString();

                            String url = "https://smhamidulcodding.000webhostapp.com/ecommerce_app/cart_item/customer_data.php?product_id="+id+"&name="+customerName+"&number="+customerNumber+"&email="+
                                    customerEmail+"&address="+customerAddress+"&quantity="+productQuantity;

                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });

                            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                            requestQueue.add(stringRequest);

                            arrayList.remove(position);
                            cartList.remove(position);
                            Gson gson = new Gson();
                            String json = gson.toJson(cartList);
                            editor.putString("cartList",json);
                            editor.apply();
                            if (arrayList==null || arrayList.size()<1){
                                arrayList = new ArrayList<>();
                                empty.setVisibility(View.VISIBLE);
                                full.setVisibility(View.GONE);
                            }
                            MyAdapter myAdapter = new MyAdapter();
                            gridView.setAdapter(myAdapter);

                            dialog.cancel();

                        }
                    });

                }
            });//===================================================================================

            Picasso.get()
                    .load(image)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            lottieAnimationView.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            imageView.setVisibility(View.GONE);
                            lottieAnimationView.setVisibility(View.VISIBLE);
                        }
                    });

            return cartView;
        }
    }

    public void loadCartData(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("cartList",null);
        Type type = new TypeToken<ArrayList<HashMap<String,String>>>(){
        }.getType();
        cartList = gson.fromJson(json,type);
        if (cartList==null || cartList.size()<1){
            cartList = new ArrayList<>();
            empty.setVisibility(View.VISIBLE);
            full.setVisibility(View.GONE);
        }
    }

    public void loadData (){
        arrayList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        for (int x = 0; x<cartList.size(); x++){
            hashMap = cartList.get(x);
            string = string+hashMap.get("id")+",";
        }

        String url = "https://smhamidulcodding.000webhostapp.com/ecommerce_app/cart_item/cart_item.php?search="+string+"0";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                for (int x = 0; x<response.length();x++){
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

                MyAdapter myAdapter = new MyAdapter();
                gridView.setAdapter(myAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayRequest);

    }


    private void loadUserDetails (){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.user_details,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        EditText edName = view.findViewById(R.id.edName);
        EditText edNumber = view.findViewById(R.id.edNumber);
        EditText edEmail = view.findViewById(R.id.edEmail);
        EditText edAddress = view.findViewById(R.id.edAddress);
        EditText edQuantity = view.findViewById(R.id.edQuantity);
        Button buttonSubmit = view.findViewById(R.id.buttonSubmit);

        final AlertDialog alertDialog = builder.create();

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = edName.getText().toString();
                String number = edNumber.getText().toString();
                String email = edEmail.getText().toString();
                String address = edAddress.getText().toString();
                String quantity = edQuantity.getText().toString();

                buttonSubmit.setEnabled(!name.isEmpty() && number.length()==11 && !email.isEmpty() && !address.isEmpty() && !quantity.isEmpty());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        edName.addTextChangedListener(watcher);
        edNumber.addTextChangedListener(watcher);
        edEmail.addTextChangedListener(watcher);
        edAddress.addTextChangedListener(watcher);
        edQuantity.addTextChangedListener(watcher);

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        alertDialog.show();

    }

}