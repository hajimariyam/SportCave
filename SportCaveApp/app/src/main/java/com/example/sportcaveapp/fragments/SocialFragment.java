package com.example.sportcaveapp.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportcaveapp.R;
import com.example.sportcaveapp.Reaction;
import com.example.sportcaveapp.ReactionsAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SocialFragment extends Fragment implements MenuItem.OnActionExpandListener {

    public static final String TAG = "SocialFragment";
    public final static int PICK_PHOTO_CODE = 1046;
    public String photoFileName = "photo.jpeg";
    private ParseUser currentUser;
    private EditText etCompose;
    private Button btnPost;
    private Button btnGallery;
    private TextView tvAttachment;
    private ParseFile galleryPhoto;
    private RecyclerView rvReactions;
    protected ReactionsAdapter adapter;
    protected List<Reaction> allReactions;

    public SocialFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUser = ParseUser.getCurrentUser();
        etCompose = view.findViewById(R.id.etCompose);
        btnPost = view.findViewById(R.id.btnPost);
        rvReactions = view.findViewById(R.id.rvReactions);
        btnGallery = view.findViewById(R.id.btnGallery);
        tvAttachment = view.findViewById(R.id.tvAttachment);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = etCompose.getText().toString();

                if (comment.isEmpty()) {
                    Toast.makeText(getContext(), "Comment is blank!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Reaction reaction = new Reaction();
                reaction.setComment(comment);
                reaction.setUser(currentUser);
                if (galleryPhoto != null) {
                    reaction.put("photoReaction", galleryPhoto);
                }
                reaction.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while posting!", e);
                            Toast.makeText(getContext(), "Error while posting!", Toast.LENGTH_SHORT).show();
                        } else {
                        Log.i(TAG, "Reaction posted successfully!");
                        Toast.makeText(getContext(), "Reaction posted successfully!", Toast.LENGTH_SHORT).show(); }
                        etCompose.setText("");
                        tvAttachment.setText("No photo attachment");
                    }
                });
                allReactions.add(0, reaction);
                adapter.notifyItemInserted(0);
            }
        });

        allReactions = new ArrayList<>();
        adapter = new ReactionsAdapter(getContext(), allReactions);
        rvReactions.setAdapter(adapter);
        rvReactions.setLayoutManager(new LinearLayoutManager(getContext()));
        queryReactions();
    }

    protected void queryReactions() {
        ParseQuery<Reaction> query = ParseQuery.getQuery(Reaction.class);
        query.include(Reaction.KEY_USER);
        query.addDescendingOrder(Reaction.KEY_CREATED_KEY);
        query.findInBackground(new FindCallback<Reaction>() {
            @Override
            public void done(List<Reaction> reactions, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error retrieving comments!", e);
                    return;
                }
                for (Reaction reaction : reactions) {
                    Log.i(TAG, "Reaction: " + reaction.getComment() + ", username: " + reaction.getUser().getUsername());
                    if (reaction.getPhotoReaction() != null) {
                        Log.i(TAG, "Has photo!");
                    }
                }
                allReactions.addAll(reactions);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText == null || newText.trim().isEmpty()) {
                    adapter = new ReactionsAdapter(getContext(), allReactions);
                    rvReactions.setAdapter(adapter);
                    return false;
                }

                List<Reaction> filteredValues = new ArrayList<Reaction>(allReactions);
                for (Reaction reaction : allReactions) {
                    if (!reaction.getComment().toLowerCase().contains(newText.toLowerCase())) {
                        filteredValues.remove(reaction);
                    }
                }

                adapter = new ReactionsAdapter(getContext(), filteredValues);
                rvReactions.setAdapter(adapter);
                return false;
            }
        });
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    public void onPickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            if(Build.VERSION.SDK_INT > 27){
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            Bitmap selectedImage = loadFromUri(photoUri);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] scaledData = stream.toByteArray();

            galleryPhoto = new ParseFile(System.currentTimeMillis() + "_" + photoFileName, scaledData);
            tvAttachment.setText(System.currentTimeMillis() + "_" + photoFileName);
            galleryPhoto.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null) {
                        Log.i(TAG, "Photo from gallery has been saved as a Parse file!", e);
                        Toast.makeText(getContext(), "Photo attached!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed to save photo from gallery as a parse file!", e);
                        Toast.makeText(getContext(), "Error while attaching photo!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}