package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportcaveapp.R;

import java.util.List;

import models.Match;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    Context context;
    List<Match> matches;

    public MatchAdapter(Context context, List<Match> matches) {
        this.context = context;
        this.matches = matches;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View matchview = LayoutInflater.from(context).inflate(R.layout.iteam_match,viewGroup,false);
        return new ViewHolder(matchview);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Match match = matches.get(i);

        viewHolder.bind(match);

    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvname1;
        TextView tvname2;
        ImageView ivteam1;
        ImageView ivteam2;
        TextView tvgoals1;
        TextView tvgoals2;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname1 = itemView.findViewById(R.id.tvname1);
            tvname2 = itemView.findViewById(R.id.tvname2);
            ivteam1 = itemView.findViewById(R.id.ivteam1);
            ivteam2 = itemView.findViewById(R.id.ivteam2);
            tvgoals1 = itemView.findViewById(R.id.tvgoals1);
            tvgoals2 = itemView.findViewById(R.id.tvgoals2);

        }

        public void bind(Match match) {
            tvname1.setText(match.getTeam1());
            tvname2.setText(match.getTeam2());
            tvgoals1.setText(match.getGoals1());
            tvgoals2.setText(match.getGoals2());
            Glide.with(context).load(match.getLogo1()).into(ivteam1);
            Glide.with(context).load(match.getLogo2()).into(ivteam2);

        }
    }
}
