package com.example.minimalhome.service;

import android.util.Log;
import com.example.minimalhome.util.JwtUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class AuthService {
    // Minimal implementation will come later
    private static  final String TAG = "AuthService";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String BASE_URL = "http://10.0.2.2:8080";

    private final OkHttpClient client = new OkHttpClient();
    private final JwtUtil jwtUtil = new JwtUtil();

    public void login(String username, String password, AuthCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            callback.onError("Error creating request: " + e.getMessage());
            callback.onComplete();
            return;
        }

        RequestBody body = RequestBody.create(JSON, jsonBody.toString());
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/auth/login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Login failed", e);
                callback.onError("Network error: " + e.getMessage());
                callback.onComplete();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    if (!response.isSuccessful()) {
                        callback.onError("Login failed: " + responseBody);
                        return;
                    }

                    JSONObject jsonResponse = new JSONObject(responseBody);
                    if (jsonResponse.has("data")) {
                        JSONObject data = jsonResponse.getJSONObject("data");
                        String token = data.getString("token");
                        callback.onSuccess(token);
                    } else {
                        callback.onError("Invalid server response");
                    }
                } catch (JSONException e) {
                    callback.onError("Error parsing response: " + e.getMessage());
                } finally {
                    callback.onComplete();
                }
            }
        });
    }
}





















