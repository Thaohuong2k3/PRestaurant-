// app/src/main/java/com/example/porestaurant/ui/theme/MenuManagementFragment.java
package com.example.porestaurant.ui.theme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Admin;
import com.example.porestaurant.repository.MenuRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MenuManagementFragment extends Fragment {
    private RecyclerView rvMenu;
    private ImageButton fabAdd;
    private MenuRepository repo;
    private MenuAdapter adapter;

    private byte[] selectedImageData;
    private String selectedImageMimeType;
    private AlertDialog currentDialog;

    // Launcher để chọn ảnh từ gallery
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri uri = result.getData().getData();
                            try (InputStream is = requireContext()
                                    .getContentResolver()
                                    .openInputStream(uri);
                                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                                // đọc thủ công vào ByteArrayOutputStream
                                byte[] buffer = new byte[4096];
                                int len;
                                while ((len = is.read(buffer)) != -1) {
                                    baos.write(buffer, 0, len);
                                }
                                selectedImageData = baos.toByteArray();
                                selectedImageMimeType =
                                        requireContext().getContentResolver().getType(uri);

                                ImageView iv = currentDialog.findViewById(R.id.ivPreview);
                                iv.setImageBitmap(
                                        BitmapFactory.decodeByteArray(
                                                selectedImageData, 0, selectedImageData.length));
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(),
                                        "Không thể đọc ảnh",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_menu_management,
                container,
                false
        );
    }

    @Override public void onViewCreated(@NonNull View v,
                                        @Nullable Bundle s) {
        super.onViewCreated(v, s);
        rvMenu = v.findViewById(R.id.rvMenu);
        fabAdd = v.findViewById(R.id.fabAddMenu);

        repo = new MenuRepository();
        adapter = new MenuAdapter(new ArrayList<>());
        rvMenu.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMenu.setAdapter(adapter);

        fabAdd.setOnClickListener(x -> showMenuDialog(null));
        loadMenus();
    }

    private void loadMenus() {
        repo.getMenus(new MenuRepository.Result<List<Admin.MenuDTO>>() {
            @Override public void onSuccess(List<Admin.MenuDTO> data) {
                adapter.setItems(data);
            }
            @Override public void onError(String err) {
                Toast.makeText(getContext(),
                        "Lỗi tải menu: " + err,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMenuDialog(@Nullable Admin.MenuDTO edit) {
        // reset trước mỗi lần show dialog
        selectedImageData = null;
        selectedImageMimeType = null;

        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        View form = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_menu, null, false);

        ImageView ivPreview = form.findViewById(R.id.ivPreview);
        Button btnChoose   = form.findViewById(R.id.btnChooseImage);
        EditText etName    = form.findViewById(R.id.etMenuName);
        EditText etPrice   = form.findViewById(R.id.etMenuPrice);

        if (edit != null) {
            etName.setText(edit.getName());
            etPrice.setText(String.valueOf(edit.getPrice()));
            byte[] existing = edit.getImageData();
            if (existing != null && existing.length > 0) {
                ivPreview.setImageBitmap(
                        BitmapFactory.decodeByteArray(existing, 0, existing.length));
                selectedImageData = existing;
                selectedImageMimeType = edit.getImageMimeType();
            }
        }

        btnChoose.setOnClickListener(v -> {
            Intent pick = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(pick);
        });

        b.setView(form)
                .setTitle(edit == null ? "Thêm món" : "Chỉnh sửa món")
                .setPositiveButton("Lưu", (dlg, which) -> {
                    String name = etName.getText().toString().trim();
                    String priceTxt = etPrice.getText().toString().trim();
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceTxt)) {
                        Toast.makeText(getContext(),
                                "Vui lòng nhập tên và giá",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double price = Double.parseDouble(priceTxt);

                    if (edit == null) {
                        Admin.MenuDTO m = new Admin.MenuDTO();
                        m.setName(name);
                        m.setPrice(price);
                        repo.createMenu(
                                m,
                                selectedImageData,
                                selectedImageMimeType,
                                new MenuRepository.Result<Admin.MenuDTO>() {
                                    @Override public void onSuccess(Admin.MenuDTO created) {
                                        loadMenus();
                                        Toast.makeText(getContext(),
                                                "Thêm thành công",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    @Override public void onError(String err) {
                                        Toast.makeText(getContext(),
                                                "Lỗi thêm: " + err,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    } else {
                        edit.setName(name);
                        edit.setPrice(price);
                        repo.updateMenu(
                                edit.getMenuId(),
                                edit,
                                selectedImageData,
                                selectedImageMimeType,
                                new MenuRepository.Result<Void>() {
                                    @Override public void onSuccess(Void v) {
                                        loadMenus();
                                        Toast.makeText(getContext(),
                                                "Cập nhật thành công",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    @Override public void onError(String err) {
                                        Toast.makeText(getContext(),
                                                "Lỗi cập nhật: " + err,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                })
                .setNegativeButton("Hủy", null);

        currentDialog = b.show();
    }

    private class MenuAdapter
            extends RecyclerView.Adapter<MenuAdapter.VH> {

        private final List<Admin.MenuDTO> items;

        MenuAdapter(List<Admin.MenuDTO> data) {
            this.items = data;
        }

        void setItems(List<Admin.MenuDTO> data) {
            items.clear();
            items.addAll(data);
            notifyDataSetChanged();
        }

        @NonNull @Override
        public VH onCreateViewHolder(
                @NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_menu, parent, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(
                @NonNull VH holder, int position) {
            Admin.MenuDTO m = items.get(position);

            holder.tvName .setText(m.getName());
            holder.tvPrice.setText(
                    String.format("%.2f", m.getPrice()));

            byte[] img = m.getImageData();
            if (img != null && img.length > 0) {
                holder.ivImage.setImageBitmap(
                        BitmapFactory.decodeByteArray(img, 0, img.length));
            } else {
                holder.ivImage.setImageResource(
                        R.drawable.ic_launcher_background);
            }

            holder.btnEdit.setOnClickListener(x ->
                    showMenuDialog(m)
            );
            holder.btnDelete.setOnClickListener(x ->
                    repo.deleteMenu(m.getMenuId(),
                            new MenuRepository.Result<Void>() {
                                @Override public void onSuccess(Void v) {
                                    loadMenus();
                                    Toast.makeText(getContext(),
                                            "Xóa thành công",
                                            Toast.LENGTH_SHORT).show();
                                }
                                @Override public void onError(String err) {
                                    Toast.makeText(getContext(),
                                            "Lỗi xóa: " + err,
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
            );
        }

        @Override public int getItemCount() {
            return items.size();
        }

        class VH extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvName, tvPrice;
            ImageButton btnEdit, btnDelete;

            VH(@NonNull View itemView) {
                super(itemView);
                ivImage   = itemView.findViewById(R.id.ivMenuImage);
                tvName    = itemView.findViewById(R.id.tvMenuName);
                tvPrice   = itemView.findViewById(R.id.tvMenuPrice);
                btnEdit   = itemView.findViewById(R.id.btnEditMenu);
                btnDelete = itemView.findViewById(R.id.btnDeleteMenu);
            }
        }
    }
}
