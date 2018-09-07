package com.example.ashish.startup.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ashish.startup.Adapters.MarksCardAdapter;
import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

public class UserShowMarksFragment extends android.support.v4.app.Fragment {

    private String subject_id,subject_name,Teacher_Name,email;
    private List<Marks> marksList;
    private MarksCardAdapter marksCardAdapter;
    private OnFragmentInteractionListener mListener;

    public UserShowMarksFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subject_id = getArguments().getString("subject_id");
        subject_name = getArguments().getString("subject_name");
        Teacher_Name = getArguments().getString("Teacher_Name");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        email = user.getEmail();
        loadMarksCard();
    }

    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    private void loadMarksCard() {
        try{
            rootRef.collection("Users").document(Teacher_Name).collection("Subjects").document(subject_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                        marksCardAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("error",e.getLocalizedMessage());
                }
            });
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
        marksCardAdapter = new MarksCardAdapter(getContext(),marksList,subject_id,Teacher_Name,email);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(marksCardAdapter);
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
