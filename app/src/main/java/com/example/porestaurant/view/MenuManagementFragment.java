package com.example.porestaurant.view;

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
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Category;
import com.example.porestaurant.model.Menu;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;
import com.example.porestaurant.repository.MenuRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MenuManagementFragment extends Fragment {
    private RecyclerView rvMenu;
    private ImageButton fabAdd;
    private MenuRepository repo;
    private MenuAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();
    private byte[] selectedImageData;
    private String selectedImageMimeType;
    private AlertDialog currentDialog;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                            Uri uri = result.getData().getData();
                            try (InputStream is = requireContext()
                                    .getContentResolver()
                                    .openInputStream(uri);
                                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                                byte[] buf = new byte[4096];
                                int len;
                                while ((len = is.read(buf)) != -1) baos.write(buf, 0, len);
                                selectedImageData = baos.toByteArray();
                                selectedImageMimeType = requireContext()
                                        .getContentResolver()
                                        .getType(uri);

                                ImageView iv = currentDialog.findViewById(R.id.ivPreview);
                                iv.setImageBitmap(
                                        BitmapFactory.decodeByteArray(
                                                selectedImageData, 0, selectedImageData.length));
                            } catch (IOException e) {
                                Toast.makeText(getContext(), "Không thể đọc ảnh", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup cont,
                             @Nullable Bundle saved) {
        return inf.inflate(R.layout.fragment_menu_management, cont, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        rvMenu = v.findViewById(R.id.rvMenu);
        fabAdd = v.findViewById(R.id.fabAddMenu);

        repo = new MenuRepository();
        adapter = new MenuAdapter(new ArrayList<>());
        rvMenu.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMenu.setAdapter(adapter);

        fabAdd.setOnClickListener(x -> showMenuDialog(null));
        loadCategories();
        loadMenus();
    }

    private void loadCategories() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override public void onResponse(Call<List<Category>> call, Response<List<Category>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(resp.body());
                }
            }
            @Override public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(getContext(), "Không tải được danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMenus() {
        repo.getAllMenus(new MenuRepository.MenuCallback() {
            @Override public void onSuccess(List<Menu> data) {
                adapter.setItems(data);
            }
            @Override public void onError(String err) {
                Toast.makeText(getContext(), "Lỗi tải menu: " + err, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMenuDialog(@Nullable Menu edit) {
        selectedImageData = null;
        selectedImageMimeType = null;

        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        View form = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_menu, null, false);

        ImageView ivPrev     = form.findViewById(R.id.ivPreview);
        Button btnPick       = form.findViewById(R.id.btnChooseImage);
        EditText etName      = form.findViewById(R.id.etMenuName);
        EditText etPrice     = form.findViewById(R.id.etMenuPrice);
        EditText etDesc      = form.findViewById(R.id.etMenuDescription);
        Spinner spCat        = form.findViewById(R.id.spMenuCategory);
        CheckBox cbAvail     = form.findViewById(R.id.cbMenuAvailable);

        ArrayAdapter<Category> spAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                categoryList);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCat.setAdapter(spAdapter);

        if (edit != null) {
            etName.setText(edit.getName());
            etPrice.setText(String.valueOf(edit.getPrice()));
            etDesc.setText(edit.getDescription());
            cbAvail.setChecked(edit.isAvailable());
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getCategoryId() == edit.getCategoryId()) {
                    spCat.setSelection(i);
                    break;
                }
            }
            byte[] ex = edit.getImageData();
            if (ex != null && ex.length > 0) {
                ivPrev.setImageBitmap(BitmapFactory.decodeByteArray(ex, 0, ex.length));
                selectedImageData = ex;
                selectedImageMimeType = edit.getImageMimeType();
            }
        }

        btnPick.setOnClickListener(v -> {
            Intent it = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(it);
        });

        b.setView(form)
                .setTitle(edit == null ? "Thêm món" : "Chỉnh sửa món")
                .setPositiveButton("Lưu", (dlg, which) -> {
                    String name  = etName.getText().toString().trim();
                    String pTxt   = etPrice.getText().toString().trim();
                    String desc   = etDesc.getText().toString().trim();
                    boolean avail = cbAvail.isChecked();
                    Category sel  = (Category) spCat.getSelectedItem();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pTxt)) {
                        Toast.makeText(getContext(), "Nhập tên và giá", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price = Double.parseDouble(pTxt);
                    if (edit == null) {
                        Menu m = new Menu();
                        m.setName(name);
                        m.setPrice(price);
                        m.setDescription(desc);
                        m.setCategoryId(sel.getCategoryId());
                        m.setCategoryName(sel.getCategoryName());
                        m.setAvailable(avail);

                        repo.createMenu(m, selectedImageData, selectedImageMimeType,
                                new MenuRepository.SimpleCallback() {
                                    @Override public void onSuccess() {
                                        loadMenus();
                                        Toast.makeText(getContext(),
                                                "Thêm thành công", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override public void onError(String err) {
                                        Toast.makeText(getContext(),
                                                "Lỗi thêm: " + err, Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    } else {
                        edit.setName(name);
                        edit.setPrice(price);
                        edit.setDescription(desc);
                        edit.setCategoryId(sel.getCategoryId());
                        edit.setCategoryName(sel.getCategoryName());
                        edit.setAvailable(avail);

                        repo.updateMenu(
                                edit.getMenuId(),
                                edit,
                                selectedImageData,
                                selectedImageMimeType,
                                new MenuRepository.SimpleCallback() {
                                    @Override public void onSuccess() {
                                        loadMenus();
                                        Toast.makeText(getContext(),
                                                "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override public void onError(String err) {
                                        Toast.makeText(getContext(),
                                                "Lỗi cập nhật: " + err, Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                })
                .setNegativeButton("Hủy", null);

        currentDialog = b.show();
    }
    private class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.VH> {
        private final List<Menu> items;
        MenuAdapter(List<Menu> d) { items = d; }

        void setItems(List<Menu> d) {
            items.clear();
            items.addAll(d);
            notifyDataSetChanged();
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup p, int vT) {
            View v = LayoutInflater.from(p.getContext())
                    .inflate(R.layout.item_menu, p, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Menu m = items.get(pos);
            h.tvName.setText(m.getName());
            h.tvPrice.setText(String.format("%.2f", m.getPrice()));
            h.tvDescription.setText(m.getDescription());
            h.tvCategory.setText(m.getCategoryName());
            h.tvStatus.setText(m.isAvailable() ? "Còn hàng" : "Hết hàng");

            byte[] img = m.getImageData();
            if (img != null && img.length > 0) {
                h.ivImage.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
            } else {
                h.ivImage.setImageResource(R.drawable.ic_launcher_background);
            }

            h.btnEdit.setOnClickListener(x -> showMenuDialog(m));
            h.btnDelete.setOnClickListener(x ->
                    repo.deleteMenu(
                            m.getMenuId(),  // hoặc m.getMenuId()
                            new MenuRepository.SimpleCallback() {
                                @Override public void onSuccess() {
                                    loadMenus();
                                    Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                                }
                                @Override public void onError(String err) {
                                    Toast.makeText(getContext(), "Lỗi xóa: " + err, Toast.LENGTH_SHORT).show();
                                }
                            })
            );
        }

        @Override public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvName, tvPrice, tvDescription, tvCategory, tvStatus;
            ImageButton btnEdit, btnDelete;
            VH(@NonNull View it) {
                super(it);
                ivImage      = it.findViewById(R.id.ivMenuImage);
                tvName       = it.findViewById(R.id.tvMenuName);
                tvPrice      = it.findViewById(R.id.tvMenuPrice);
                tvDescription= it.findViewById(R.id.tvMenuDescription);
                tvCategory   = it.findViewById(R.id.tvMenuCategory);
                tvStatus     = it.findViewById(R.id.tvMenuStatus);
                btnEdit      = it.findViewById(R.id.btnEditMenu);
                btnDelete    = it.findViewById(R.id.btnDeleteMenu);
            }
        }
    }
}
