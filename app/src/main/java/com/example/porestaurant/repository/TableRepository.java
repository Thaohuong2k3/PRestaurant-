// app/src/main/java/com/example/porestaurant/repository/TableRepository.java
package com.example.porestaurant.repository;

import com.example.porestaurant.model.Admin.TableBookingRequest;
import com.example.porestaurant.model.Admin.TableDTO;
import com.example.porestaurant.model.Admin.TableStatusUpdateRequest;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableRepository {
    private final ApiService api = ApiClient.getClient().create(ApiService.class);

    public interface Result<T> {
        void onSuccess(T data);
        void onError(String err);
    }

    public void getAllTables(Result<List<TableDTO>> cb) {
        api.getAllTables().enqueue(new Callback<List<TableDTO>>() {
            @Override public void onResponse(Call<List<TableDTO>> c, Response<List<TableDTO>> r) {
                if (r.isSuccessful()) cb.onSuccess(r.body());
                else                  cb.onError("Error " + r.code());
            }
            @Override public void onFailure(Call<List<TableDTO>> c, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void bookTable(TableBookingRequest req, Result<TableDTO> cb) {
        api.bookTable(req).enqueue(new Callback<TableDTO>() {
            @Override public void onResponse(Call<TableDTO> c, Response<TableDTO> r) {
                if (r.isSuccessful()) cb.onSuccess(r.body());
                else                  cb.onError("Error " + r.code());
            }
            @Override public void onFailure(Call<TableDTO> c, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void updateTableStatus(int id, TableStatusUpdateRequest req, Result<TableDTO> cb) {
        api.updateTableStatus(id, req).enqueue(new Callback<TableDTO>() {
            @Override public void onResponse(Call<TableDTO> c, Response<TableDTO> r) {
                if (r.isSuccessful()) cb.onSuccess(r.body());
                else                  cb.onError("Error " + r.code());
            }
            @Override public void onFailure(Call<TableDTO> c, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void cancelTable(int id, Result<TableDTO> cb) {
        api.cancelTable(id).enqueue(new Callback<TableDTO>() {
            @Override public void onResponse(Call<TableDTO> c, Response<TableDTO> r) {
                if (r.isSuccessful()) cb.onSuccess(r.body());
                else                  cb.onError("Error " + r.code());
            }
            @Override public void onFailure(Call<TableDTO> c, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }
}
