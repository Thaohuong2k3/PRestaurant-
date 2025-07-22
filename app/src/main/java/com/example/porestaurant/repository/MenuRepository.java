// app/src/main/java/com/example/porestaurant/repository/MenuRepository.java
package com.example.porestaurant.repository;

import com.example.porestaurant.model.Admin;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuRepository {
    private final ApiService api;

    public interface Result<T> {
        void onSuccess(T data);
        void onError(String err);
    }

    public MenuRepository() {
        api = ApiClient.getClient().create(ApiService.class);
    }

    public void getMenus(Result<List<Admin.MenuDTO>> cb) {
        api.getMenus().enqueue(new Callback<List<Admin.MenuDTO>>() {
            @Override public void onResponse(Call<List<Admin.MenuDTO>> call, Response<List<Admin.MenuDTO>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body());
                else cb.onError("Error " + resp.code());
            }
            @Override public void onFailure(Call<List<Admin.MenuDTO>> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void createMenu(Admin.MenuDTO dto, byte[] imageData, String mimeType, Result<Admin.MenuDTO> cb) {
        RequestBody namePart  = RequestBody.create(MediaType.parse("text/plain"), dto.getName());
        RequestBody pricePart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(dto.getPrice()));
        MultipartBody.Part filePart = null;
        if (imageData != null && mimeType != null) {
            RequestBody reqFile = RequestBody.create(MediaType.parse(mimeType), imageData);
            filePart = MultipartBody.Part.createFormData("imageFile", "upload.jpg", reqFile);
        }

        api.createMenu(namePart, pricePart, filePart)
                .enqueue(new Callback<Admin.MenuDTO>() {
                    @Override public void onResponse(Call<Admin.MenuDTO> call, Response<Admin.MenuDTO> resp) {
                        if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body());
                        else cb.onError("Error " + resp.code());
                    }
                    @Override public void onFailure(Call<Admin.MenuDTO> call, Throwable t) {
                        cb.onError(t.getMessage());
                    }
                });
    }

    public void updateMenu(int id, Admin.MenuDTO dto, byte[] imageData, String mimeType, Result<Void> cb) {
        RequestBody namePart  = RequestBody.create(MediaType.parse("text/plain"), dto.getName());
        RequestBody pricePart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(dto.getPrice()));
        MultipartBody.Part filePart = null;
        if (imageData != null && mimeType != null) {
            RequestBody reqFile = RequestBody.create(MediaType.parse(mimeType), imageData);
            filePart = MultipartBody.Part.createFormData("imageFile", "upload.jpg", reqFile);
        }

        api.updateMenu(id, namePart, pricePart, filePart)
                .enqueue(new Callback<Void>() {
                    @Override public void onResponse(Call<Void> call, Response<Void> resp) {
                        if (resp.isSuccessful()) cb.onSuccess(null);
                        else cb.onError("Error " + resp.code());
                    }
                    @Override public void onFailure(Call<Void> call, Throwable t) {
                        cb.onError(t.getMessage());
                    }
                });
    }

    public void deleteMenu(int id, Result<Void> cb) {
        api.deleteMenu(id).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> resp) {
                if (resp.isSuccessful()) cb.onSuccess(null);
                else cb.onError("Error " + resp.code());
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }
}
