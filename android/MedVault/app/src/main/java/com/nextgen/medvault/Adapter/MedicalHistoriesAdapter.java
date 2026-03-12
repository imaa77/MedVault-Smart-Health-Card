package com.nextgen.medvault.Adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nextgen.medvault.Models.MedicalHistory;
import com.nextgen.medvault.R;
import com.nextgen.medvault.Utils.GlobalPreference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MedicalHistoriesAdapter extends RecyclerView.Adapter<MedicalHistoriesAdapter.ViewHolder>{

    private ArrayList<MedicalHistory> histories;
    private Context context;
    private String ip, uid;

    public MedicalHistoriesAdapter(ArrayList<MedicalHistory> histories, Context context) {
        this.histories = histories;
        this.context = context;
        GlobalPreference preference = new GlobalPreference(context);
        ip = preference.RetriveIP();
        uid = preference.getUID();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_history_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MedicalHistory history = histories.get(position);
        holder.fileNameTV.setText(history.getTitle());
        holder.dateTimeTV.setText(history.getDate_time());
        
        holder.historyCardCV.setOnClickListener(v -> {
            String filePath = history.getFile_name();
            if (filePath == null || filePath.isEmpty()) return;
            String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
            if (extension.equals("pdf")) {
                openPdf(filePath);
            } else if (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png")) {
                showImageDialog(filePath);
            }
        });

        holder.deleteHistoryBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Record")
                    .setMessage("Are you sure you want to delete this medical record?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteRecord(history.getId(), position))
                    .setNegativeButton("Cancel", null)
                    .show();
        });

    }

    private void deleteRecord(String id, int position) {
        String url = "http://" + ip + "/medvault/delete_medical_history.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("DELETE_RES", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getBoolean("success")) {
                    histories.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, histories.size());
                    Toast.makeText(context, "Record deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("DELETE_JSON_ERR", e.toString());
                Toast.makeText(context, "Server Error: " + response, Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            String errorMsg = error.toString();
            if (error.networkResponse != null) {
                errorMsg += " (Code: " + error.networkResponse.statusCode + ")";
            }
            Log.e("DELETE_VOLLEY_ERR", errorMsg);
            Toast.makeText(context, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    private void openPdf(String filePath) {
        String fileUrl = "http://"+ip+"/medvault/" + filePath;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(fileUrl), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImageDialog(String filePath) {
        View view = LayoutInflater.from(context).inflate(R.layout.raw_dialog_image, null);
        ImageView imageView = view.findViewById(R.id.previewImageView);
        String imageUrl =  "http://"+ip+"/medvault/" + filePath;
        Glide.with(context).load(imageUrl).into(imageView);
        new AlertDialog.Builder(context).setView(view).setCancelable(true).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTV, dateTimeTV;
        CardView historyCardCV;
        ImageView deleteHistoryBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTV = itemView.findViewById(R.id.fileNameTV);
            dateTimeTV = itemView.findViewById(R.id.dateTimeTV);
            historyCardCV = itemView.findViewById(R.id.historyCardCV);
            deleteHistoryBtn = itemView.findViewById(R.id.deleteHistoryBtn);
        }
    }
}
