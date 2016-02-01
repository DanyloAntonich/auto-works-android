package autoworks.app.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import autoworks.app.R;
import autoworks.app.dialogs.TransparentProgressDialog;
import helpers.HttpPOSTOnTaskCompleted;
import helpers.HttpPOSTProcess;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_Change_Password.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_Change_Password#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Change_Password extends android.support.v4.app.Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    View view;
    EditText customer_password, customer_new_password, customer_confirm_new_password;
    RelativeLayout change_password_send;
    Dialog progressDialog;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Change_Password.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Change_Password newInstance(String param1, String param2) {
        Fragment_Change_Password fragment = new Fragment_Change_Password();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_Change_Password() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


   /* @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //show logo
        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        ImageView logo = (ImageView)actionBar.getCustomView().findViewById(R.id.actionBarLogo);
        logo.setVisibility(View.VISIBLE);

    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment__change__password, container, false);
        customer_password = (EditText) view.findViewById(R.id.customer_password);
        customer_new_password = (EditText) view.findViewById(R.id.customer_new_password);
        customer_confirm_new_password = (EditText) view.findViewById(R.id.customer_confirm_new_password);
        change_password_send = (RelativeLayout) view.findViewById(R.id.change_password_send);
        change_password_send.setOnClickListener(this);
        return view;


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
      /*  try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private boolean validatePassword()
    {
      /*  if (customer_password.getText().toString().length() >= R.string.min_password_length &&
                customer_new_password.getText().toString().length() >= R.string.min_password_length &&
                customer_confirm_new_password.getText().toString().length() >= R.string.min_password_length)
        {*/
        boolean isValid = true;
        customer_password.setBackgroundColor(Color.WHITE);
        customer_new_password.setBackgroundColor(Color.WHITE);
        customer_confirm_new_password.setBackgroundColor(Color.WHITE);


        if (customer_password.getText().toString().length() < getResources().getInteger(R.integer.min_password_length)){
            customer_password.setText("");
            customer_password.setHint(getString(R.string.hint_password) + ": " + getString(R.string.error_password_length));
            customer_password.setBackgroundColor(getResources().getColor(R.color.error_background_color));
            isValid = false;
        }

        if (customer_new_password.getText().toString().length() < getResources().getInteger(R.integer.min_password_length)){
            customer_new_password.setText("");
            customer_new_password.setHint(getString(R.string.hint_new_pasword)+": "+getString(R.string.error_password_length));
            customer_new_password.setBackgroundColor(getResources().getColor(R.color.error_background_color));
            isValid = false;
        }

        if (!customer_confirm_new_password.getText().toString().equals(customer_new_password.getText().toString())){
            customer_confirm_new_password.setText("");
            customer_confirm_new_password.setHint(getString(R.string.hint_retype_pasword)+": "+getString(R.string.error_password_doesnt_match));
            customer_confirm_new_password.setBackgroundColor(getResources().getColor(R.color.error_background_color));
            isValid = false;
        }

        return isValid;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_password_send: {

                if (validatePassword())
                {
                    progressDialog = TransparentProgressDialog.createProgressDialog(this.getActivity());
                    progressDialog.show();

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("old_password", customer_password.getText().toString()));
                    params.add(new BasicNameValuePair("new_password", customer_new_password.getText().toString()));
                    params.add(new BasicNameValuePair("customer_id", MainActivity.currentUser.getCustomerID()+""));


                    HttpPOSTProcess changePassAsync = new HttpPOSTProcess(MainActivity.getDataURL2, MainActivity.change_pass_tag, params, new HttpPOSTOnTaskCompleted() {
                        @Override
                        public void onHttpPOSTOnTaskCompleted(JSONObject json) {
                            try {
                                if (json != null) {
                                    String result =json.getString("result");
                                    if (result.equals("SUCCESSFULLY"))
                                    {
                                        changePasswordComplete(true);
                                    } else if (json.getString("result").equals("ERROR")) {

                                        Log.d("AutoWorks", "duplicate user");
                                        changePasswordComplete(false);
                                    }
                                }
                            } catch (JSONException e) {

                                changePasswordComplete(false);
                                e.printStackTrace();
                            }
                        }
                    });
                }

                break;

            }
        }
    }

    private void changePasswordComplete(boolean isSuccessful)
    {
        progressDialog.dismiss();
        if (isSuccessful)
        {
            //change password successfully -> go back, show confirm dialog


            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setMessage("Password updated successfully!")
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            dialog.cancel();
                            getFragmentManager().popBackStackImmediate();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();


        }else
        {
            //not successfully
            //show errors (old password not match)
            customer_password.setText("");
            customer_password.setHint(R.string.error_old_password_doesnt_match);
            customer_password.setBackgroundResource(R.color.error_background_color);
        }

    }


        /**
         * This interface must be implemented by activities that contain this
         * fragment to allow an interaction in this fragment to be communicated
         * to the activity and potentially other fragments contained in that
         * activity.
         * <p/>
         * See the Android Training lesson <a href=
         * "http://developer.android.com/training/basics/fragments/communicating.html"
         * >Communicating with Other Fragments</a> for more information.
         */
        public interface OnFragmentInteractionListener {
            // TODO: Update argument type and name
            public void onFragmentInteraction(Uri uri);
        }

    }
