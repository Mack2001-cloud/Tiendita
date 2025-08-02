package com.codedev.droidcoffevfinal.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.codedev.droidcoffevfinal.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.codedev.droidcoffevfinal.OrderManager;

public class HomeFragment extends Fragment {

    private TextView tvDonutCount, tvIceCreamCount, tvFroyoCount;
    private Button btnAddDonut, btnAddIceCream, btnAddFroyo;

    private Button btnRemoveDonut, btnRemoveIceCream, btnRemoveFroyo;

    private OrderManager orderManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize OrderManager
        orderManager = OrderManager.getInstance();

        // Find views
        tvDonutCount = root.findViewById(R.id.tv_donut_count);
        tvIceCreamCount = root.findViewById(R.id.tv_ice_cream_count);
        tvFroyoCount = root.findViewById(R.id.tv_froyo_count);

        btnAddDonut = root.findViewById(R.id.btn_add_donut);
        btnAddIceCream = root.findViewById(R.id.btn_add_ice_cream);
        btnAddFroyo = root.findViewById(R.id.btn_add_froyo);

        btnRemoveDonut = root.findViewById(R.id.btn_remove_donut);
        btnRemoveIceCream = root.findViewById(R.id.btn_remove_ice_cream);
        btnRemoveFroyo = root.findViewById(R.id.btn_remove_froyo);

        btnRemoveDonut.setOnClickListener(v -> {
            orderManager.removeItem(OrderManager.DONUT);
            updateCounts();
        });

        btnRemoveIceCream.setOnClickListener(v -> {
            orderManager.removeItem(OrderManager.ICE_CREAM);
            updateCounts();
        });

        btnRemoveFroyo.setOnClickListener(v -> {
            orderManager.removeItem(OrderManager.FROYO);
            updateCounts();
        });

        // Set click listeners
        btnAddDonut.setOnClickListener(v -> {
            orderManager.addItem(OrderManager.DONUT);
            updateCounts();
        });

        btnAddIceCream.setOnClickListener(v -> {
            orderManager.addItem(OrderManager.ICE_CREAM);
            updateCounts();
        });

        btnAddFroyo.setOnClickListener(v -> {
            orderManager.addItem(OrderManager.FROYO);
            updateCounts();
        });

        // Update initial counts
        updateCounts();

        return root;
    }

    private void updateCounts() {
        tvDonutCount.setText(String.valueOf(orderManager.getItemCount(OrderManager.DONUT)));
        tvIceCreamCount.setText(String.valueOf(orderManager.getItemCount(OrderManager.ICE_CREAM)));
        tvFroyoCount.setText(String.valueOf(orderManager.getItemCount(OrderManager.FROYO)));
    }
}
