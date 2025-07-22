package com.example.porestaurant.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.porestaurant.R;
import com.example.porestaurant.model.TableBookingRequest;
import com.example.porestaurant.model.TableDTO;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingFragment extends Fragment {
    private Spinner spinnerTables;
    private Spinner spinnerCapacity;
    private Button btnBook;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextView tvNoTables;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinnerTables = view.findViewById(R.id.spinner_tables);
        spinnerCapacity = view.findViewById(R.id.spinner_capacity);
        btnBook = view.findViewById(R.id.btn_book);
        datePicker = view.findViewById(R.id.date_picker);
        timePicker = view.findViewById(R.id.time_picker);
        tvNoTables = view.findViewById(R.id.tv_no_tables);
        apiService = ApiClient.getClient().create(ApiService.class);

        // Tự động đặt thời gian hiện tại
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
        timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(calendar.get(Calendar.MINUTE));

        // Đặt danh sách số người (capacity)
        setupCapacitySpinner();

        loadAvailableTables();

        btnBook.setOnClickListener(v -> bookTable());
    }

    private void setupCapacitySpinner() {
        List<String> capacities = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            capacities.add(String.valueOf(i));
        }
        ArrayAdapter<String> capacityAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, capacities);
        capacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCapacity.setAdapter(capacityAdapter);
    }

    private void loadAvailableTables() {
        Call<List<TableDTO>> call = apiService.getAvailableTables();
        call.enqueue(new Callback<List<TableDTO>>() {
            @Override
            public void onResponse(Call<List<TableDTO>> call, Response<List<TableDTO>> response) {
                if (response.isSuccessful()) {
                    List<TableDTO> tables = response.body();
                    if (tables == null || tables.isEmpty()) {
                        tvNoTables.setVisibility(View.VISIBLE);
                        spinnerTables.setEnabled(false);
                        spinnerCapacity.setEnabled(false);
                        datePicker.setEnabled(false);
                        timePicker.setEnabled(false);
                        btnBook.setEnabled(false);
                    } else {
                        tvNoTables.setVisibility(View.GONE);
                        spinnerTables.setEnabled(true);
                        spinnerCapacity.setEnabled(true);
                        datePicker.setEnabled(true);
                        timePicker.setEnabled(true);
                        btnBook.setEnabled(true);
                        String[] tableNumbers = new String[tables.size()];
                        for (int i = 0; i < tables.size(); i++) {
                            tableNumbers[i] = tables.get(i).getTableNumber() + " (ID: " + tables.get(i).getTableId() + ")";
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_spinner_item, tableNumbers);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerTables.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(requireContext(), "Lỗi khi tải bàn trống: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TableDTO>> call, Throwable t) {
                Toast.makeText(requireContext(), "Kết nối thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bookTable() {
        int position = spinnerTables.getSelectedItemPosition();
        Call<List<TableDTO>> call = apiService.getAvailableTables();
        call.enqueue(new Callback<List<TableDTO>>() {
            @Override
            public void onResponse(Call<List<TableDTO>> call, Response<List<TableDTO>> response) {
                if (response.isSuccessful() && response.body() != null && position >= 0 && position < response.body().size()) {
                    TableDTO table = response.body().get(position);
                    int year = datePicker.getYear();
                    int month = datePicker.getMonth();
                    int day = datePicker.getDayOfMonth();
                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
                    calendar.set(year, month, day, hour, minute, 0);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
                    String checkinTime = sdf.format(calendar.getTime());

                    // Lấy số người từ spinner
                    String capacityStr = spinnerCapacity.getSelectedItem().toString();
                    int capacity = Integer.parseInt(capacityStr);

                    // Kiểm tra capacity có hợp lệ với bàn
                    if (capacity > table.getCapacity()) {
                        Toast.makeText(requireContext(), "Số người vượt quá sức chứa bàn!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Kiểm tra thời gian không phải quá khứ
                    if (calendar.getTime().compareTo(new Date()) < 0) {
                        Toast.makeText(requireContext(), "Thời gian đặt không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    TableBookingRequest request = new TableBookingRequest(table.getTableId(), checkinTime);
                    Log.d("Booking", "Sending request for TableId: " + table.getTableId() + ", Checkin: " + checkinTime + ", Capacity: " + capacity);
                    Call<TableDTO> bookCall = apiService.bookTable(request);
                    bookCall.enqueue(new Callback<TableDTO>() {
                        @Override
                        public void onResponse(Call<TableDTO> call, Response<TableDTO> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(), "Đặt bàn thành công!", Toast.LENGTH_SHORT).show();
                                loadAvailableTables();
                            } else {
                                Log.e("Booking", "Failed: " + response.code() + " - " + response.message());
                                Toast.makeText(requireContext(), "Đặt bàn thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<TableDTO> call, Throwable t) {
                            Log.e("Booking", "Failure: " + t.getMessage());
                            Toast.makeText(requireContext(), "Kết nối thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "Vui lòng chọn bàn hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TableDTO>> call, Throwable t) {
                Toast.makeText(requireContext(), "Kết nối thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 