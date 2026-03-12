package com.nextgen.medvault.Services;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, String> headers = new HashMap<>();

    private static final String BOUNDARY = "apiclient-" + System.currentTimeMillis();
    private static final String LINE_FEED = "\r\n";

    public VolleyMultipartRequest(
            int method,
            String url,
            Response.Listener<NetworkResponse> listener,
            Response.ErrorListener errorListener) {

        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + BOUNDARY;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Text parameters
            for (Map.Entry<String, String> entry : getParams().entrySet()) {
                outputStream.write(("--" + BOUNDARY + LINE_FEED).getBytes());
                outputStream.write(("Content-Disposition: form-data; name=\"" +
                        entry.getKey() + "\"" + LINE_FEED).getBytes());
                outputStream.write(("Content-Type: text/plain; charset=UTF-8" + LINE_FEED).getBytes());
                outputStream.write(LINE_FEED.getBytes());
                outputStream.write(entry.getValue().getBytes());
                outputStream.write(LINE_FEED.getBytes());
            }

            // File parameters
            for (Map.Entry<String, DataPart> entry : getByteData().entrySet()) {
                DataPart data = entry.getValue();

                outputStream.write(("--" + BOUNDARY + LINE_FEED).getBytes());
                outputStream.write(("Content-Disposition: form-data; name=\"" +
                        entry.getKey() + "\"; filename=\"" +
                        data.fileName + "\"" + LINE_FEED).getBytes());
                outputStream.write(("Content-Type: " + data.type + LINE_FEED).getBytes());
                outputStream.write(LINE_FEED.getBytes());

                outputStream.write(data.content);
                outputStream.write(LINE_FEED.getBytes());
            }

            outputStream.write(("--" + BOUNDARY + "--").getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    protected abstract Map<String, DataPart> getByteData();

    protected Map<String, String> getParams() throws AuthFailureError {
        return new HashMap<>();
    }

    // Helper class
    public static class DataPart {
        public String fileName;
        public byte[] content;
        public String type;

        public DataPart(String fileName, byte[] content, String type) {
            this.fileName = fileName;
            this.content = content;
            this.type = type;
        }
    }
}