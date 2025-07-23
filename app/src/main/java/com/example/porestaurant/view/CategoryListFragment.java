package com.example.porestaurant.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Category;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryListFragment extends Fragment {

    private RecyclerView rv;
    private CategoryAdapter adapter;
    private ApiService api;

    // Giữ cả 2 list: model để thao tác CRUD, name để đẩy vào adapter
    private final List<Category> categoryModels = new ArrayList<>();
    private final List<String> categoryNames = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        rv = v.findViewById(R.id.rvCategories);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new CategoryAdapter(categoryNames, new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(String name) {
                // handle click (edit)
                int pos = categoryNames.indexOf(name);
                if (pos >= 0) showDialog(categoryModels.get(pos), pos);
            }

            @Override
            public void onCategoryLongClick(String name, int position) {
                // handle long‑click (delete)
                new AlertDialog.Builder(requireContext())
                        .setTitle("Xóa danh mục")
                        .setMessage("Bạn có chắc muốn xóa \"" + name + "\"?")
                        .setPositiveButton("Xóa", (d,w) -> deleteCategory(position))
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        rv.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fabAdd);
        fab.setOnClickListener(x -> showDialog(null, -1));

        api = ApiClient.getClient().create(ApiService.class);
        loadCategories();
    }

    private void loadCategories() {
        api.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    categoryModels.clear();
                    categoryModels.addAll(resp.body());

                    categoryNames.clear();
                    for (Category c : categoryModels) {
                        categoryNames.add(c.getCategoryName());
                    }
                    adapter.setCategories(categoryNames);
                } else {
                    Toast.makeText(requireContext(),
                            "Load thất bại: " + resp.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Lỗi: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @param edit    Nếu khác null nghĩa là Edit, truyền model để lấy id + desc hiện tại
     * @param position index trong danh sách (dùng cho update/delete)
     */
    private void showDialog(@Nullable Category edit, int position) {
        boolean isEdit = (edit != null);

        AlertDialog.Builder b = new AlertDialog.Builder(requireContext());
        View form = getLayoutInflater().inflate(R.layout.dialog_category, null);
        EditText edtName = form.findViewById(R.id.edtName);
        EditText edtDesc = form.findViewById(R.id.edtDescription);

        if (isEdit) {
            edtName.setText(edit.getCategoryName());
            edtDesc.setText(edit.getDescription());
            b.setTitle("Sửa danh mục");
        } else {
            b.setTitle("Thêm danh mục");
        }

        b.setView(form);
        b.setPositiveButton(isEdit ? "Cập nhật" : "Tạo", (d, w) -> {
            String name = edtName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            String desc = edtDesc.getText().toString().trim();
            if (isEdit) {
                updateCategory(edit.getCategoryId(), name, desc, position);
            } else {
                createCategory(name, desc);
            }
        });
        b.setNegativeButton("Hủy", null);
        b.show();
    }

    private void createCategory(String name, String desc) {
        Category c = new Category();
        c.setCategoryName(name);
        c.setDescription(desc);

        api.createCategory(c).enqueue(new Callback<Category>() {
            @Override public void onResponse(Call<Category> call, Response<Category> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    // Thêm vào cả 2 list và adapter
                    categoryModels.add(resp.body());
                    categoryNames.add(resp.body().getCategoryName());
                    adapter.addCategory(resp.body().getCategoryName());
                } else {
                    Toast.makeText(requireContext(),
                            "Tạo thất bại: " + resp.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Lỗi: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCategory(int id, String name, String desc, int position) {
        Category c = new Category();
        c.setCategoryName(name);
        c.setDescription(desc);

        api.updateCategory(id, c).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> resp) {
                if (resp.isSuccessful()) {
                    // Cập nhật model + tên + adapter
                    categoryModels.get(position).setCategoryName(name);
                    categoryModels.get(position).setDescription(desc);
                    adapter.updateCategory(position, name);
                } else {
                    Toast.makeText(requireContext(),
                            "Cập nhật thất bại: " + resp.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Lỗi: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCategory(int position) {
        int id = categoryModels.get(position).getCategoryId();
        api.deleteCategory(id).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> resp) {
                if (resp.isSuccessful()) {
                    categoryModels.remove(position);
                    adapter.removeCategory(position);
                } else {
                    Toast.makeText(requireContext(),
                            "Xóa thất bại: " + resp.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Lỗi: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
