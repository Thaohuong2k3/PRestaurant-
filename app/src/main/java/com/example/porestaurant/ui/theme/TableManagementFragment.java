// app/src/main/java/com/example/porestaurant/ui/theme/TableManagementFragment.java
package com.example.porestaurant.ui.theme;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Admin.TableBookingRequest;
import com.example.porestaurant.model.Admin.TableDTO;
import com.example.porestaurant.model.Admin.TableStatusUpdateRequest;
import com.example.porestaurant.repository.TableRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TableManagementFragment extends Fragment {
    private RecyclerView rvTables;
    private FloatingActionButton fabBook;
    private TableAdapter adapter;
    private final TableRepository repo = new TableRepository();
    private final SimpleDateFormat isoFmt =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_table_management, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle saved) {
        super.onViewCreated(view, saved);

        rvTables = view.findViewById(R.id.rvTables);
        fabBook  = view.findViewById(R.id.fabBookTable);

        rvTables.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TableAdapter(new ArrayList<>());
        rvTables.setAdapter(adapter);

        fabBook.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showBookingDialog();
            }
        });

        loadTables();
    }

    private void loadTables() {
        repo.getAllTables(new TableRepository.Result<List<TableDTO>>() {
            @Override public void onSuccess(List<TableDTO> data) {
                adapter.setItems(data);
            }
            @Override public void onError(String err) {
                Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBookingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Đặt bàn");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText etId = new EditText(getContext());
        etId.setHint("Table ID");
        final EditText etDate = new EditText(getContext());
        etDate.setHint("YYYY-MM-DDThh:mm");

        layout.addView(etId);
        layout.addView(etDate);
        builder.setView(layout);

        builder.setPositiveButton("Book", (dialog, which) -> {
            try {
                TableBookingRequest req = new TableBookingRequest();
                req.setTableId(Integer.parseInt(etId.getText().toString()));
                req.setCheckin(isoFmt.parse(etDate.getText().toString()));
                repo.bookTable(req, new TableRepository.Result<TableDTO>() {
                    @Override public void onSuccess(TableDTO data) {
                        loadTables();
                    }
                    @Override public void onError(String err) {
                        Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception ex) {
                Toast.makeText(getContext(), "Input không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showStatusDialog(final TableDTO table) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cập nhật trạng thái");

        final EditText etStatus = new EditText(getContext());
        etStatus.setHint("New status");
        builder.setView(etStatus);

        builder.setPositiveButton("OK", (dialog, which) -> {
            TableStatusUpdateRequest req = new TableStatusUpdateRequest();
            req.setNewStatus(etStatus.getText().toString());
            repo.updateTableStatus(table.getTableId(), req, new TableRepository.Result<TableDTO>() {
                @Override public void onSuccess(TableDTO data) {
                    loadTables();
                }
                @Override public void onError(String err) {
                    Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    class TableAdapter extends RecyclerView.Adapter<TableAdapter.VH> {
        private List<TableDTO> items;
        TableAdapter(List<TableDTO> list) { items = list; }
        void setItems(List<TableDTO> list) {
            items = list;
            notifyDataSetChanged();
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_table, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            TableDTO t = items.get(position);
            holder.tvNum.setText(t.getTableNumber());
            holder.tvStatus.setText(t.getStatus());

            holder.btn1.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if ("Available".equalsIgnoreCase(t.getStatus())) {
                        showBookingDialog();
                    } else {
                        showStatusDialog(t);
                    }
                }
            });
            holder.btn2.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    repo.cancelTable(t.getTableId(), new TableRepository.Result<TableDTO>() {
                        @Override public void onSuccess(TableDTO data) {
                            loadTables();
                        }
                        @Override public void onError(String err) {
                            Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        @Override public int getItemCount() {
            return items.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView tvNum, tvStatus;
            ImageButton btn1, btn2;
            VH(View itemView) {
                super(itemView);
                tvNum    = itemView.findViewById(R.id.tvTableNumber);
                tvStatus = itemView.findViewById(R.id.tvTableStatus);
                btn1     = itemView.findViewById(R.id.btnAction1);
                btn2     = itemView.findViewById(R.id.btnAction2);
            }
        }
    }
}
