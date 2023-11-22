package com.icesoft.msdb.android.client;

//import android.app.Activity;
//import android.content.Context;
//import android.util.Log;
//
//import androidx.annotation.NonNull;

//import com.android.billingclient.api.AcknowledgePurchaseParams;
//import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
//import com.android.billingclient.api.BillingClient;
//import com.android.billingclient.api.BillingClientStateListener;
//import com.android.billingclient.api.BillingFlowParams;
//import com.android.billingclient.api.BillingResult;
//import com.android.billingclient.api.ProductDetails;
//import com.android.billingclient.api.Purchase;
//import com.android.billingclient.api.PurchaseHistoryResponseListener;
//import com.android.billingclient.api.PurchasesResponseListener;
//import com.android.billingclient.api.PurchasesUpdatedListener;
//import com.android.billingclient.api.QueryProductDetailsParams;
//import com.android.billingclient.api.QueryPurchaseHistoryParams;
//import com.android.billingclient.api.QueryPurchasesParams;

import java.util.List;

public class MSDBBillingClient { // implements PurchasesUpdatedListener {

//    private final BillingClient billingClient;
//
//    private ProductDetails supportSubscriptionProduct;
//
//    private boolean billingUnavailable = false;
//    private boolean serviceConnected = false;
//    private boolean subscribed = false;
//
//    private final BillingClientStateListener clientStateListener = new BillingClientStateListener() {
//        @Override
//        public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
//            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                serviceConnected = true;
//                getProducts();
//                checkSubscriptionStatus();
//                Log.d("MDSDB", "Products retrieved");
////                billingClient.queryPurchaseHistoryAsync(
////                        QueryPurchaseHistoryParams.newBuilder()
////                                .setProductType(BillingClient.ProductType.SUBS)
////                                .build(),
////                        new PurchaseHistoryResponseListener() {
////                            public void onPurchaseHistoryResponse(
////                                    BillingResult billingResult, List purchasesHistoryList) {
////                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
////                                    System.out.println(purchasesHistoryList);
////                                }
////                            }
////                        }
////                );
//            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
//                billingUnavailable = true;
//            }
//        }
//
//        @Override
//        public void onBillingServiceDisconnected() {
//            serviceConnected = false;
//        }
//    };
//
//    public MSDBBillingClient(Context context) {
//        this.billingClient = BillingClient.newBuilder(context)
//                .enablePendingPurchases()
//                .setListener(this)
//                .build();
//
//        this.billingClient.startConnection(clientStateListener);
//    }
//
//    @Override
//    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
//        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
//                && purchases != null) {
//            for (Purchase purchase : purchases) {
//                handlePurchase(purchase);
//            }
//        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
//            // Handle an error caused by a user cancelling the purchase flow.
//        } else {
//            // Handle any other error codes.
//        }
//    }
//
//    public void getProducts() {
//        QueryProductDetailsParams queryProductDetailsParams =
//                QueryProductDetailsParams.newBuilder()
//                        .setProductList(
//                                List.of(
//                                        QueryProductDetailsParams.Product.newBuilder()
//                                                .setProductId("com.icesoft.msdb.support_subscription")
//                                                .setProductType(BillingClient.ProductType.SUBS)
//                                                .build()))
//                        .build();
//        // products = Collections.emptyList();
//        billingClient.queryProductDetailsAsync(
//                queryProductDetailsParams,
//                (billingResult, productDetailsList) -> {
//                    // check billingResult
//                    // process returned productDetailsList
//                    supportSubscriptionProduct = productDetailsList.get(0);
//                   // products.addAll(productDetailsList);
//                }
//        );
//    }
//
//    public void launchBillingFlow(Activity activity) {
//        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
//                List.of(
//                        BillingFlowParams.ProductDetailsParams.newBuilder()
//                                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
//                                .setProductDetails(supportSubscriptionProduct)
//                                // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
//                                // for a list of offers that are available to the user
//                                .setOfferToken(supportSubscriptionProduct.getSubscriptionOfferDetails().get(0).getOfferToken())
//                                .build()
//                );
//
//        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
//                .setProductDetailsParamsList(productDetailsParamsList)
//                .build();
//
//        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
//        subscribed = billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK;
//    }
//
//    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = billingResult -> {
//         subscribed = billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK;
//    };
//
//    public void checkSubscriptionStatus() {
//        billingClient.queryPurchasesAsync(
//                QueryPurchasesParams.newBuilder()
//                        .setProductType(BillingClient.ProductType.SUBS)
//                        .build(),
//                (billingResult, purchases) -> {
//                    subscribed = purchases.size() > 0;
//                }
//        );
//    }
//
//    private void handlePurchase(Purchase purchase) {
//        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
//            if (!purchase.isAcknowledged()) {
//                AcknowledgePurchaseParams acknowledgePurchaseParams =
//                        AcknowledgePurchaseParams.newBuilder()
//                                .setPurchaseToken(purchase.getPurchaseToken())
//                                .build();
//                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
//            }
//        }
//    }
//
//    public boolean isBillingAvailable() {
//        return !billingUnavailable;
//    }
//
//    public boolean isSubscribed() { return subscribed; }
}
