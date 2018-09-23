package com.example.ashish.startup.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ashish.startup.Adapters.MarksListAdapter;
import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TestFragment extends android.support.v4.app.Fragment {

    private OnFragmentInteractionListener mListener;
    private FirebaseAuth firebaseAuth;
    private List<Marks> marksList;
    private MarksListAdapter marksListAdapter;
    private String c,email_red,institute;

    public TestFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("","in class test fragment");

        firebaseAuth = FirebaseAuth.getInstance();
        c = getArguments().getString("class_id");
        institute = getArguments().getString("institute");
        loadTests();
    }

    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    private void loadTests() {

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        email_red = email.substring(0, email.length() - 10);

        try{
        rootRef.collection("Users").document(email_red).collection("Subjects").document(c).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    final DocumentSnapshot document = task.getResult();
                    document.getReference().collection("Marks").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                            for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                                if (doc.getType()== DocumentChange.Type.ADDED){
                                    String marksID = doc.getDocument().getId();
                                    Marks marks = doc.getDocument().toObject(Marks.class).withID(marksID);
                                    marksList.add(marks);
                                    marksListAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(e -> Log.e("error",e.getLocalizedMessage()));
        }
        catch (Exception e){e.getLocalizedMessage();}
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.test_list);

        marksList = new ArrayList<>();
        marksListAdapter = new MarksListAdapter(getContext(),marksList,email_red,c,institute);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(marksListAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
