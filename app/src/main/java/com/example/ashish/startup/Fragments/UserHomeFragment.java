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

import com.example.ashish.startup.Adapters.SubjectsListAdapter;
import com.example.ashish.startup.Models.Subject;
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

public class UserHomeFragment extends android.support.v4.app.Fragment {
    private OnFragmentInteractionListener mListener;
    private SubjectsListAdapter subjectsListAdapter;
    private List<Subject> subjectList;
    private FirebaseAuth firebaseAuth;

    public UserHomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        loadClasses();
    }

    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    private void loadClasses() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        String email_red = email.substring(0, email.length() - 10);

        rootRef.collection("Users").document(email_red).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    final DocumentSnapshot document = task.getResult();
                    document.getReference().collection("Subjects").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            for (DocumentChange doc: documentSnapshots.getDocumentChanges()){
                                if (doc.getType()== DocumentChange.Type.ADDED){
                                    String subjectID = doc.getDocument().getId();
                                    Subject subjects = doc.getDocument().toObject(Subject.class).withID(subjectID);
                                    subjectList.add(subjects);
                                    subjectsListAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.main_user_list);

        subjectList = new ArrayList<>();
        subjectsListAdapter = new SubjectsListAdapter(getContext(),subjectList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(subjectsListAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
