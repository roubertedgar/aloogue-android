package service.httputil;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Pair;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.json.JSONException;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class Service extends AsyncTask<Void, Void, String> {
    private OnFinishTask onFinishTask;
    private Class T;
    private Object object;
    private String method;
    private Pair[] params;
    private ProgressDialog progressDialog;
    private String path = "";

    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String GET = "GET";
    private static final String DELETE = "DELETE";

    public Service(OnFinishTask onFinishTask) {
        this.onFinishTask = onFinishTask;
    }

    public Service(OnFinishTask onFinishTask, ProgressDialog progressDialog) {
        this.onFinishTask = onFinishTask;
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        ConnectionManeger connection;
        ObjectWriter objectWriter = new ObjectMapper().writer();
        String response = "";
        try {

            URLBuilder urlBuilder = new URLBuilder(T);
            urlBuilder.append(path);
            if (method.equals(GET)) {
                urlBuilder.putParams(params);
                connection = new ConnectionManeger(urlBuilder.build());
                connection.connect();
            } else {
                String json = objectWriter.writeValueAsString(object);
                connection = new ConnectionManeger(urlBuilder.build(), method);
                OutputStreamWriter out = connection.getWriter();
                out.write(json);
                out.flush();
                out.close();
            }

            response = connection.getResponse();

        } catch (IOException e) {
            e.printStackTrace();
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
        return response;
    }

    public Service save(Object object, Class T) {
        this.T = T;
        this.object = object;
        this.method = POST;

        return this;
    }

    public Service find(Class T, Pair... params) {
        this.T = T;
        this.params = params;
        this.method = GET;
        return this;
    }

    public Service delete(Object object, Class T) {
        this.T = T;
        this.object = object;
        this.method = DELETE;
        return this;
    }

    public Service putPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    protected void onPostExecute(String json) {
        try {
            object = Util.fromJson(json, T);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        onFinishTask.onFinishTask(object);
    }
}
