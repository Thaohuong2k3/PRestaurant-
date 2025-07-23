package com.example.porestaurant.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Table;
import com.example.porestaurant.repository.TableRepository;

import java.util.List;

public class UpdateTableStatusFragment extends Fragment {

    private ListView lvTables;
    private TableRepository tableRepo;
    private ArrayAdapter<Table> tableAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_table_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvTables = view.findViewById(R.id.lvTables);
        tableRepo = new TableRepository();

        loadTables();
    }

    private void loadTables() {
        tableRepo.getAllTables(new TableRepository.TableCallback() {
            @Override
            public void onSuccess(List<Table> tables) {
                tableAdapter = new ArrayAdapter<Table>(getContext(), android.R.layout.simple_list_item_1, tables) {
                    @NonNull
                    @Override
                    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                        View itemView = super.getView(position, convertView, parent);

                        // Chỉnh sửa cách hiển thị từng bàn
                        Table table = getItem(position);
                        if (table != null) {
                            TextView textView = itemView.findViewById(android.R.id.text1);
                            textView.setText("Bàn " + table.getTableNumber() + " - " + table.getStatus());

                            itemView.setOnClickListener(v -> showStatusDialog(table));
                        }

                        return itemView;
                    }
                };
                lvTables.setAdapter(tableAdapter);
            }

            @Override
            public void onError(String err) {
                Toast.makeText(getContext(), "Không thể tải bàn: " + err, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showStatusDialog(Table table) {
        // Mở một Dialog để thay đổi trạng thái bàn
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cập nhật trạng thái bàn " + table.getTableNumber());

        String[] statuses = {"Có sẵn", "Đã đặt", "Không hoạt động"};
        int currentStatusIndex = table.getStatus().equals("Có sẵn") ? 0 :
                table.getStatus().equals("Đã đặt") ? 1 : 2;

        builder.setSingleChoiceItems(statuses, currentStatusIndex, (dialog, which) -> {
            String newStatus = statuses[which];
            table.setStatus(newStatus);
            updateTableStatus(table);
            dialog.dismiss();
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void updateTableStatus(Table table) {
        tableRepo.updateTableStatus(table, new TableRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Cập nhật trạng thái bàn thành công", Toast.LENGTH_SHORT).show();
                loadTables();  // Reload danh sách bàn
            }

            @Override
            public void onError(String err) {
                Toast.makeText(getContext(), "Lỗi cập nhật trạng thái: " + err, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
