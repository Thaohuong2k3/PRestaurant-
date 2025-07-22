// app/src/main/java/com/example/porestaurant/repository/StatisticalRepository.java
package com.example.porestaurant.repository;

import com.example.porestaurant.model.Admin;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticalRepository {
    private ApiService apiService;

    public StatisticalRepository() {
        apiService = ApiClient.getClient()
                .create(ApiService.class);
    }

    public void getRevenueByMonth(int year, final RevenueCallback cb) {
        apiService.getRevenueByMonth(year)
                .enqueue(new Callback<List<Admin.RevenueByMonthDto>>() {
                    @Override
                    public void onResponse(
                            Call<List<Admin.RevenueByMonthDto>> call,
                            Response<List<Admin.RevenueByMonthDto>> resp
                    ) {
                        if (resp.isSuccessful() && resp.body()!=null) {
                            cb.onSuccess(resp.body());
                        } else {
                            cb.onError("Server lỗi: " + resp.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Admin.RevenueByMonthDto>> call, Throwable t) {
                        cb.onError("Kết nối thất bại: " + t.getMessage());
                    }
                });
    }

    public interface RevenueCallback {
        void onSuccess(List<Admin.RevenueByMonthDto> data);
        void onError(String error);
    }

    public void getOrderCountByMonth(int year, final OrderCountCallback cb) {
        apiService.getOrderCountByMonth(year)
                .enqueue(new Callback<List<Admin.OrderCountByMonthDto>>() {
                    @Override
                    public void onResponse(
                            Call<List<Admin.OrderCountByMonthDto>> call,
                            Response<List<Admin.OrderCountByMonthDto>> resp
                    ) {
                        if (resp.isSuccessful() && resp.body()!=null) {
                            cb.onSuccess(resp.body());
                        } else {
                            cb.onError("Server lỗi: " + resp.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Admin.OrderCountByMonthDto>> call, Throwable t) {
                        cb.onError("Kết nối thất bại: " + t.getMessage());
                    }
                });
    }

    public interface OrderCountCallback {
        void onSuccess(List<Admin.OrderCountByMonthDto> data);
        void onError(String error);
    }

    public void getTopMenuItems(int topN, String from, String to, final TopMenuCallback cb) {
        apiService.getTopMenuItems(topN, from, to)
                .enqueue(new Callback<List<Admin.TopMenuItemDto>>() {
                    @Override
                    public void onResponse(
                            Call<List<Admin.TopMenuItemDto>> call,
                            Response<List<Admin.TopMenuItemDto>> resp
                    ) {
                        if (resp.isSuccessful() && resp.body()!=null) {
                            cb.onSuccess(resp.body());
                        } else {
                            cb.onError("Server lỗi: " + resp.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Admin.TopMenuItemDto>> call, Throwable t) {
                        cb.onError("Kết nối thất bại: " + t.getMessage());
                    }
                });
    }

    public interface TopMenuCallback {
        void onSuccess(List<Admin.TopMenuItemDto> data);
        void onError(String error);
    }

    public void getTableOccupancy(String date, final TableOccupancyCallback cb) {
        apiService.getTableOccupancy(date)
                .enqueue(new Callback<List<Admin.TableOccupancyDto>>() {
                    @Override
                    public void onResponse(
                            Call<List<Admin.TableOccupancyDto>> call,
                            Response<List<Admin.TableOccupancyDto>> resp
                    ) {
                        if (resp.isSuccessful() && resp.body()!=null) {
                            cb.onSuccess(resp.body());
                        } else {
                            cb.onError("Server lỗi: " + resp.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Admin.TableOccupancyDto>> call, Throwable t) {
                        cb.onError("Kết nối thất bại: " + t.getMessage());
                    }
                });
    }

    public interface TableOccupancyCallback {
        void onSuccess(List<Admin.TableOccupancyDto> data);
        void onError(String error);
    }

    public void getPaymentMethods(String from, String to, final PaymentMethodCallback cb) {
        apiService.getPaymentMethods(from, to)
                .enqueue(new Callback<List<Admin.PaymentMethodDto>>() {
                    @Override
                    public void onResponse(
                            Call<List<Admin.PaymentMethodDto>> call,
                            Response<List<Admin.PaymentMethodDto>> resp
                    ) {
                        if (resp.isSuccessful() && resp.body()!=null) {
                            cb.onSuccess(resp.body());
                        } else {
                            cb.onError("Server lỗi: " + resp.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Admin.PaymentMethodDto>> call, Throwable t) {
                        cb.onError("Kết nối thất bại: " + t.getMessage());
                    }
                });
    }

    public interface PaymentMethodCallback {
        void onSuccess(List<Admin.PaymentMethodDto> data);
        void onError(String error);
    }

    public void getCustomerCountByMonth(int year, final CustomerCountCallback cb) {
        apiService.getCustomerCountByMonth(year)
                .enqueue(new Callback<List<Admin.CustomerCountByMonthDto>>() {
                    @Override
                    public void onResponse(
                            Call<List<Admin.CustomerCountByMonthDto>> call,
                            Response<List<Admin.CustomerCountByMonthDto>> resp
                    ) {
                        if (resp.isSuccessful() && resp.body()!=null) {
                            cb.onSuccess(resp.body());
                        } else {
                            cb.onError("Server lỗi: " + resp.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Admin.CustomerCountByMonthDto>> call, Throwable t) {
                        cb.onError("Kết nối thất bại: " + t.getMessage());
                    }
                });
    }

    public interface CustomerCountCallback {
        void onSuccess(List<Admin.CustomerCountByMonthDto> data);
        void onError(String error);
    }

    public void getMenuStats(int year, final MenuStatsCallback cb) {
        apiService.getMenuStats(year)
                .enqueue(new Callback<Admin.MenuStatsDto>() {
                    @Override
                    public void onResponse(
                            Call<Admin.MenuStatsDto> call,
                            Response<Admin.MenuStatsDto> resp
                    ) {
                        if (resp.isSuccessful() && resp.body()!=null) {
                            cb.onSuccess(resp.body());
                        } else {
                            cb.onError("Server lỗi: " + resp.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<Admin.MenuStatsDto> call, Throwable t) {
                        cb.onError("Kết nối thất bại: " + t.getMessage());
                    }
                });
    }

    public interface MenuStatsCallback {
        void onSuccess(Admin.MenuStatsDto data);
        void onError(String error);
    }
}
