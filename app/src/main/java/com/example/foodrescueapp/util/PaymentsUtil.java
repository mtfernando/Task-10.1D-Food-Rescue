package com.example.foodrescueapp.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodrescueapp.Constants;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class PaymentsUtil {
    public static final String TAG = "PaymentsUtil";
    //All utilities requires for Google Pay

    private static JSONObject getBaseRequest() throws JSONException{
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }


    //Creating a new PaymentsClient
    public static PaymentsClient createPaymentsClient(Activity activity) {
        Wallet.WalletOptions walletOptions =
                new Wallet.WalletOptions.Builder().setEnvironment(Constants.PAYMENTS_ENVIRONMENT).build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    //All functions relating to creating the Payment Request are below

    //Returns the allowed authentication methods
    private static JSONArray getAllowedCardAuthMethods() {
        JSONObject authMethods = new JSONObject();
        return new JSONArray(Constants.SUPPORTED_METHODS);
    }

    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray(Constants.SUPPORTED_NETWORKS);
    }

    private static JSONObject getGatewayTokenizationSpecification() throws JSONException {
        return new JSONObject() {{
            put("type", "PAYMENT_GATEWAY");
            put("parameters", new JSONObject() {{
                put("gateway", "example");
                put("gatewayMerchantId", "exampleGatewayMerchantId");
            }});
        }};
    }

    private static JSONObject getCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
        parameters.put("allowedCardNetworks", getAllowedCardNetworks());
        cardPaymentMethod.put("parameters", parameters);
        cardPaymentMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification());

        return cardPaymentMethod;
    }

    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject().put("merchantName", "Test Merchant");
    }

    private static JSONObject getTransactionInfo(String price) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", price);
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("countryCode", Constants.COUNTRY_CODE);
        transactionInfo.put("currencyCode", Constants.CURRENCY_CODE);
        transactionInfo.put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE");

        return transactionInfo;
    }

    //Creating a enw payments request
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Optional<JSONObject> getPaymentDataRequest(Integer priceInteger) {

    //PaymentDataRequest requires four parameters apiVersion and apiVersionMinor (provided from getBaseRequest),
        //merchantInfo, allowedPaymentMethods
        //See docs: https://developers.google.com/pay/api/web/reference/request-objects
        final String price = priceInteger.toString();

        try {
            JSONObject paymentDataRequest = PaymentsUtil.getBaseRequest();
            paymentDataRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(PaymentsUtil.getCardPaymentMethod()));
            paymentDataRequest.put("transactionInfo", PaymentsUtil.getTransactionInfo(price));
            paymentDataRequest.put("merchantInfo", PaymentsUtil.getMerchantInfo());

            return Optional.of(paymentDataRequest);

        } catch (JSONException e) {
            Log.i(TAG, "getPaymentDataRequest: getPaymentDataRequest is returning Optional.empty()");
            return Optional.empty();
        }
    }

    //Requesting a payment from the user displaying overlay with transaction information
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void requestPayment(Activity activity, Integer price, View view) {

        //Get activity from context that is using the payment utility
        PaymentsClient paymentsClient = PaymentsUtil.createPaymentsClient(activity);

        //Get PaymentRequest and make sure a JSON object was returned
        Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(price);
        if (!paymentDataRequestJson.isPresent()) {
            Log.e(TAG, "requestPayment: getPaymentDataRequest is not present");
            return;
        }

        PaymentDataRequest request = PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        if (request != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request),
                    activity, Util.REQUEST_PAYMENT);
        }

    }
}

