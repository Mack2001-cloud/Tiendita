package com.codedev.droidcoffevfinal.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.graphics.Canvas;
import android.content.ActivityNotFoundException;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import java.io.File;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;
import com.codedev.droidcoffevfinal.OrderManager;
import com.codedev.droidcoffevfinal.R;
import com.codedev.droidcoffevfinal.databinding.FragmentGalleryBinding;

import java.text.DecimalFormat;

public class GalleryFragment extends Fragment {

    private LinearLayout layoutOrderItems, layoutEmptyState, layoutActionButtons;
    private MaterialCardView cardOrderSummary, cardOrderConfirmation;
    private TextView tvSubtotal, tvTax, tvTotal;
    private Button btnClearOrder, btnPlaceOrder;
    private OrderManager orderManager;
    private DecimalFormat currencyFormat;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        // Initialize OrderManager and formatter
        orderManager = OrderManager.getInstance();
        currencyFormat = new DecimalFormat("$#,##0.00");

        // Find views
        layoutOrderItems = root.findViewById(R.id.layout_order_items);
        layoutEmptyState = root.findViewById(R.id.layout_empty_state);
        layoutActionButtons = root.findViewById(R.id.layout_action_buttons);
        cardOrderSummary = root.findViewById(R.id.card_order_summary);
        cardOrderConfirmation = root.findViewById(R.id.card_order_confirmation);

        tvSubtotal = root.findViewById(R.id.tv_subtotal);
        tvTax = root.findViewById(R.id.tv_tax);
        tvTotal = root.findViewById(R.id.tv_total);

        btnClearOrder = root.findViewById(R.id.btn_clear_order);
        btnPlaceOrder = root.findViewById(R.id.btn_place_order);

        // Set click listeners
        btnClearOrder.setOnClickListener(v -> {
            orderManager.clearOrder();
            updateOrderView();

            // Redirigir automáticamente al fragmento del menú
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_menu);
        });

        btnPlaceOrder.setOnClickListener(v -> {
            placeOrder();
        });

        // Update order view
        updateOrderView();

        return root;
    }

    private void updateOrderView() {
        // Clear previous items
        layoutOrderItems.removeAllViews();

        if (orderManager.getTotalItems() == 0) {
            // Show empty state
            layoutEmptyState.setVisibility(View.VISIBLE);
            cardOrderSummary.setVisibility(View.GONE);
            layoutActionButtons.setVisibility(View.GONE);
            cardOrderConfirmation.setVisibility(View.GONE);
        } else {
            // Hide empty state
            layoutEmptyState.setVisibility(View.GONE);
            cardOrderSummary.setVisibility(View.VISIBLE);
            layoutActionButtons.setVisibility(View.VISIBLE);
            cardOrderConfirmation.setVisibility(View.GONE);

            // Add order items
            addOrderItemsToView();
            updateOrderSummary();
        }
    }

    private void addOrderItemsToView() {
        // Add Donut items
        if (orderManager.getItemCount(OrderManager.DONUT) > 0) {
            View itemView = createOrderItemView("Dona Glaseada",
                    orderManager.getItemCount(OrderManager.DONUT),
                    OrderManager.DONUT_PRICE);
            layoutOrderItems.addView(itemView);
        }

        // Add Ice Cream items
        if (orderManager.getItemCount(OrderManager.ICE_CREAM) > 0) {
            View itemView = createOrderItemView("Helado Premium",
                    orderManager.getItemCount(OrderManager.ICE_CREAM),
                    OrderManager.ICE_CREAM_PRICE);
            layoutOrderItems.addView(itemView);
        }

        // Add FroYo items
        if (orderManager.getItemCount(OrderManager.FROYO) > 0) {
            View itemView = createOrderItemView("Frozen Yogurt",
                    orderManager.getItemCount(OrderManager.FROYO),
                    OrderManager.FROYO_PRICE);
            layoutOrderItems.addView(itemView);
        }
    }

    private View createOrderItemView(String itemName, int quantity, double price) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.order_item_layout, layoutOrderItems, false);

        TextView tvItemName = itemView.findViewById(R.id.tv_item_name);
        TextView tvItemQuantity = itemView.findViewById(R.id.tv_item_quantity);
        TextView tvItemPrice = itemView.findViewById(R.id.tv_item_price);
        TextView tvItemTotal = itemView.findViewById(R.id.tv_item_total);

        tvItemName.setText(itemName);
        tvItemQuantity.setText("x" + quantity);
        tvItemPrice.setText(currencyFormat.format(price));
        tvItemTotal.setText(currencyFormat.format(price * quantity));

        return itemView;
    }

    private void updateOrderSummary() {
        double subtotal = orderManager.getSubtotal();
        double tax = subtotal * 0.16; // 16% tax
        double total = subtotal + tax;

        tvSubtotal.setText(currencyFormat.format(subtotal));
        tvTax.setText(currencyFormat.format(tax));
        tvTotal.setText(currencyFormat.format(total));
    }

    private void placeOrder() {
        // Hide action buttons and summary
        layoutActionButtons.setVisibility(View.GONE);
        cardOrderSummary.setVisibility(View.GONE);

        // Show confirmation
        cardOrderConfirmation.setVisibility(View.VISIBLE);

        // Clear order after 3 seconds
        new android.os.Handler().postDelayed(() -> {
            orderManager.clearOrder();
            updateOrderView();
            // Redirigir automáticamente al fragmento del menú
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_menu);
        }, 2000);

        generateOrderPdf();
    }

    private void generateOrderPdf() {
        PdfDocument pdfDocument = new PdfDocument();

        // Configurar página (A4, 595x842 puntos)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setTextSize(16);

        int y = 40; // posición vertical inicial

        canvas.drawText("Resumen de Pedido", 40, y, paint);
        y += 30;

        // Listar productos con cantidad y precio
        if (orderManager.getItemCount(OrderManager.DONUT) > 0) {
            canvas.drawText("Dona Glaseada x" + orderManager.getItemCount(OrderManager.DONUT) +
                    " - " + currencyFormat.format(OrderManager.DONUT_PRICE * orderManager.getItemCount(OrderManager.DONUT)), 40, y, paint);
            y += 25;
        }
        if (orderManager.getItemCount(OrderManager.ICE_CREAM) > 0) {
            canvas.drawText("Helado Premium x" + orderManager.getItemCount(OrderManager.ICE_CREAM) +
                    " - " + currencyFormat.format(OrderManager.ICE_CREAM_PRICE * orderManager.getItemCount(OrderManager.ICE_CREAM)), 40, y, paint);
            y += 25;
        }
        if (orderManager.getItemCount(OrderManager.FROYO) > 0) {
            canvas.drawText("Frozen Yogurt x" + orderManager.getItemCount(OrderManager.FROYO) +
                    " - " + currencyFormat.format(OrderManager.FROYO_PRICE * orderManager.getItemCount(OrderManager.FROYO)), 40, y, paint);
            y += 25;
        }

        y += 20;
        canvas.drawText("Subtotal: " + currencyFormat.format(orderManager.getSubtotal()), 40, y, paint);
        y += 25;
        double tax = orderManager.getSubtotal() * 0.16;
        canvas.drawText("Impuestos (16%): " + currencyFormat.format(tax), 40, y, paint);
        y += 25;
        double total = orderManager.getSubtotal() + tax;
        canvas.drawText("Total: " + currencyFormat.format(total), 40, y, paint);

        pdfDocument.finishPage(page);

        // Guardar archivo
        String directoryPath = requireContext().getExternalFilesDir(null).getPath() + "/orders/";
        File directory = requireContext().getExternalFilesDir("orders");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(requireContext().getFilesDir(), "pedido_" + System.currentTimeMillis() + ".pdf");


        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            //Toast.makeText(getContext(), "PDF generado en la carpeta descargas", Toast.LENGTH_SHORT).show();

            //sharePdfWithAnyApp(file);

            new android.os.Handler().postDelayed(() -> {
                openPdf(file);
            }, 2300);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al generar PDF", Toast.LENGTH_SHORT).show();
        }


        pdfDocument.close();
    }


    private void sharePdfWithAnyApp(File file) {
        try {
            Uri uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Compartir pedido PDF con..."));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al compartir PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPdf(File file) {
        Uri uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".provider", // Asegúrate que sea ".provider"
                file
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No hay aplicación para abrir archivos PDF", Toast.LENGTH_SHORT).show();
        }
    }




}

