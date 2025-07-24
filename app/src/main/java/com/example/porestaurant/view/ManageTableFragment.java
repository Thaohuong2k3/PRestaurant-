package com.example.porestaurant.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.porestaurant.R;
import com.example.porestaurant.model.TableDTO;
import com.example.porestaurant.model.TableStatusUpdateRequest;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageTableFragment extends Fragment {

    private RecyclerView recyclerViewTables;
    private TableAdapter tableAdapter;
    private ApiService apiService;
    private TextView tvNoTables;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewTables = view.findViewById(R.id.recycler_view_tables);
        tvNoTables = view.findViewById(R.id.tv_no_tables);
        apiService = ApiClient.getClient().create(ApiService.class);

        recyclerViewTables.setLayoutManager(new LinearLayoutManager(requireContext()));
        tableAdapter = new TableAdapter(new ArrayList<>(), this::updateTableStatus, this::cancelTable);
        recyclerViewTables.setAdapter(tableAdapter);

        loadTables();
    }

    private void loadTables() {
        Call<List<TableDTO>> call = apiService.getAvailableTables(); // Modify to get all tables if needed
        call.enqueue(new Callback<List<TableDTO>>() {
            @Override
            public void onResponse(Call<List<TableDTO>> call, Response<List<TableDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TableDTO> tables = response.body();
                    if (tables.isEmpty()) {
                        tvNoTables.setVisibility(View.VISIBLE);
                        recyclerViewTables.setVisibility(View.GONE);
                    } else {
                        tvNoTables.setVisibility(View.GONE);
                        recyclerViewTables.setVisibility(View.VISIBLE);
                        tableAdapter.updateTables(tables);
                    }
                } else {
                    Toast.makeText(requireContext(), "Error loading tables: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TableDTO>> call, Throwable t) {
                Toast.makeText(requireContext(), "Connection failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTableStatus(TableDTO table, String newStatus) {
        TableStatusUpdateRequest request = new TableStatusUpdateRequest(newStatus);
        Call<TableDTO> call = apiService.updateTableStatus(table.getTableId(), request);
        call.enqueue(new Callback<TableDTO>() {
            @Override
            public void onResponse(Call<TableDTO> call, Response<TableDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Table status updated to " + newStatus + "!", Toast.LENGTH_SHORT).show();
                    loadTables(); // Refresh table list
                } else {
                    Toast.makeText(requireContext(), "Failed to update status: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TableDTO> call, Throwable t) {
                Toast.makeText(requireContext(), "Connection failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelTable(int tableId) {
        Call<TableDTO> call = apiService.cancelTable(tableId);
        call.enqueue(new Callback<TableDTO>() {
            @Override
            public void onResponse(Call<TableDTO> call, Response<TableDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Table booking canceled!", Toast.LENGTH_SHORT).show();
                    loadTables(); // Refresh table list
                } else {
                    Toast.makeText(requireContext(), "Failed to cancel booking: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TableDTO> call, Throwable t) {
                Toast.makeText(requireContext(), "Connection failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {
        private List<TableDTO> tables;
        private final OnStatusChangeListener statusChangeListener;
        private final OnCancelListener cancelListener;

        interface OnStatusChangeListener {
            void onStatusChange(TableDTO table, String newStatus);
        }

        interface OnCancelListener {
            void onCancel(int tableId);
        }

        TableAdapter(List<TableDTO> tables, OnStatusChangeListener statusChangeListener, OnCancelListener cancelListener) {
            this.tables = tables;
            this.statusChangeListener = statusChangeListener;
            this.cancelListener = cancelListener;
        }

        void updateTables(List<TableDTO> newTables) {
            this.tables = newTables;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false);
            return new TableViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
            TableDTO table = tables.get(position);
            holder.bind(table, statusChangeListener, cancelListener);
        }

        @Override
        public int getItemCount() {
            return tables.size();
        }

        static class TableViewHolder extends RecyclerView.ViewHolder {
            TextView tvTableInfo;
            Spinner spinnerStatus;
            Button btnUpdate;
            Button btnCancel;

            TableViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTableInfo = itemView.findViewById(R.id.tv_table_info);
                spinnerStatus = itemView.findViewById(R.id.spinner_status);
                btnUpdate = itemView.findViewById(R.id.btn_update);
                btnCancel = itemView.findViewById(R.id.btn_cancel);
            }

            void bind(TableDTO table, OnStatusChangeListener statusChangeListener, OnCancelListener cancelListener) {
                tvTableInfo.setText("Table " + table.getTableNumber() + " (ID: " + table.getTableId() + ", Capacity: " + table.getCapacity() + ")");
                List<String> statuses = Arrays.asList("AVAILABLE", "OCCUPIED", "RESERVED");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(),
                        android.R.layout.simple_spinner_item, statuses);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStatus.setAdapter(adapter);
                spinnerStatus.setSelection(statuses.indexOf(table.getStatus()));

                btnUpdate.setOnClickListener(v -> {
                    String newStatus = spinnerStatus.getSelectedItem().toString();
                    statusChangeListener.onStatusChange(table, newStatus);
                });

                btnCancel.setOnClickListener(v -> cancelListener.onCancel(table.getTableId()));
            }
        }
    }
}