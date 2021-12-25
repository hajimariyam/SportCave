package com.example.sportcaveapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

import static com.parse.Parse.getApplicationContext;

public class ReactionsAdapter extends RecyclerView.Adapter<ReactionsAdapter.ViewHolder> {

    public static final String TAG = "ReactionsAdapter";
    private Context context;
    private List<Reaction> reactions;

    public ReactionsAdapter(Context context, List<Reaction> reactions) {
        this.context = context;
        this.reactions = reactions;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reaction reaction = reactions.get(position);
        holder.bind(reaction);
    }

    @Override
    public int getItemCount() {
        return reactions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfileImage;
        private TextView tvUser;
        private TextView tvName;
        private TextView tvComment;
        private TextView tvCreatedAt;
        private ImageView ivReactionImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvName = itemView.findViewById(R.id.tvName);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            ivReactionImage = itemView.findViewById(R.id.ivReactionImage);
        }

        public void bind(Reaction reaction) {
            tvUser.setText("@"+reaction.getUser().getUsername());
            new PatternEditableBuilder().
                    addPattern(Pattern.compile("\\@(\\w+)"), Color.BLACK,
                            new PatternEditableBuilder.SpannableClickedListener() {
                                @Override
                                public void onSpanClicked(String username) {
                                    Log.i(TAG, "Clicked username: " + username);
                                    goUserProfile(username);
                                }
                            }).into(tvUser);
            tvName.setText(reaction.getUser().get("profileName").toString());
            tvComment.setText(reaction.getComment());
            tvCreatedAt.setText(TimeFormatter.getTimeDifference(reaction.getCreatedAt().toString()));
            if (reaction.getUser().getParseFile("profilePicture") != null) {
                Glide.with(context).load(reaction.getUser().getParseFile("profilePicture").getUrl()).into(ivProfileImage); }
            else {
                Glide.with(context).load(R.drawable.user).into(ivProfileImage); }
            if (reaction.getPhotoReaction() != null) {
                Log.i(TAG, "Posting photo for: " + reaction.getComment());
                Glide.with(context).load(reaction.getPhotoReaction().getUrl()).into(ivReactionImage); }
        }
    }

    private void goUserProfile(String username) {
        context.startActivity(new Intent(context, UserProfile.class).putExtra("username", username));
    }
}
