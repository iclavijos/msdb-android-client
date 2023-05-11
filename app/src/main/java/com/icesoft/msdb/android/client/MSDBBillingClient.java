package com.icesoft.msdb.android.client;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MSDBBillingClient implements PurchasesUpdatedListener {

    private BillingClient billingClient;

    private ProductDetails supportSubscriptionProduct;

    public MSDBBillingClient(Context context) {
        this.billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener(this)
                .build();

        this.billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts();
                    Log.d("MDSDB", "Products retrieved");
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }

    public void showProducts() {
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                Arrays.asList(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId("com.icesoft.msdb.support_subscription")
                                                .setProductType(BillingClient.ProductType.SUBS)
                                                .build()))
                        .build();
        List<ProductDetails> products = new ArrayList<>();
        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                (billingResult, productDetailsList) -> {
                    // check billingResult
                    // process returned productDetailsList
                   supportSubscriptionProduct = productDetailsList.get(0);
                }
        );
    }

    public void launchBillingFlow(Activity activity) {
        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                Arrays.asList(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                .setProductDetails(supportSubscriptionProduct)
                                // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                                // for a list of offers that are available to the user
                                .setOfferToken(supportSubscriptionProduct.getSubscriptionOfferDetails().get(0).getOfferToken())
                                .build()
                );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        // Launch the billing flow
        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
    }

    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = billingResult -> {

    };

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        }
    }

}
