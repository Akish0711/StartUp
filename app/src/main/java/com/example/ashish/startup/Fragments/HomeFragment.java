package com.example.ashish.startup.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ashish.startup.Models.Classes;
import com.example.ashish.startup.Adapters.ClassesListAdapter;
import com.example.ashish.startup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ClassesListAdapter classesListAdapter;
    private List<Classes> classesList;
    private List<String> keyList;
    private String email_red;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        keyList = new ArrayList<>();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        email_red = email.substring(0, email.length() - 10).toUpperCase();
        loadClasses();
    }
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    private void loadClasses() {

        // name, website
        rootRef.collection("Users").document(email_red).collection("Subjects").addSnapshotListener((documentSnapshots, e) -> {
            for (DocumentChange doc: documentSnapshots.getDocumentChanges()){
                switch (doc.getType()) {
                    case ADDED:
                        String class_id = doc.getDocument().getId();
                        Classes classes = doc.getDocument().toObject(Classes.class).withID(class_id);
                        classesList.add(classes);
                        keyList.add(class_id);
                        classesListAdapter.notifyDataSetChanged();
                        break;
                    case MODIFIED:
                        break;
                    case REMOVED:
                        class_id = doc.getDocument().getId();
                        int index = keyList.indexOf(class_id);
                        classesList.remove(index);
                        keyList.remove(index);
                        classesListAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.main_list);

        classesList = new ArrayList<>();
        classesListAdapter = new ClassesListAdapter(getContext(),classesList,email_red);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(classesListAdapter);
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
