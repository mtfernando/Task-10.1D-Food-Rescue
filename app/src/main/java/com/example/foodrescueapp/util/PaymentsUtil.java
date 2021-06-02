package com.example.foodrescueapp.util;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.foodrescueapp.Constants;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class PaymentsUtil {
    //All utilities requires for Google Pay

    public static JSONObject getBaseRequest() throws JSONException{
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }


    //Creating a new PaymentsClient
    public static PaymentsClient createPaymentsClient(Activity activity) {
        Wallet.WalletOptions walletOptions =
                new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build();
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

    private static JSONObject getCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
        parameters.put("allowedCardNetworks", getAllowedCardNetworks());
        cardPaymentMethod.put("parameters", parameters);

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

        //PaymentDataRequest requires three parameters apiVersiona nd apiVersionMinor (provided from getBaseRequest),
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
                return Optional.empty();
            }
        }
    }
}
