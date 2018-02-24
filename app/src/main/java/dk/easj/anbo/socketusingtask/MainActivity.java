package dk.easj.anbo.socketusingtask;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private static final String SPECIAL_IP_TO_HOST_COMPUTER = "10.0.2.2";
    // GenyMotion  10.0.3.2
    // Ordinary emulator 10.0.2.2
    private static final int PORT_NUMBER = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonClicked(View view) {
        EditText editText = findViewById(R.id.mainRequestEditText);
        String request = editText.getText().toString();
        Log.d("MINE", "request: " + request);

        RequestResponseTask task = new RequestResponseTask();
        task.execute(request);

        /*
        // doInBackground
        String response = null;
        try {
            response = getResponse(request);

            // onPostExecute
            TextView textView = findViewById(R.id.mainResponseTextView);
            textView.setText(response);
        } catch (IOException e) {
            Log.e("SHIT", e.toString());
        }
        */
    }

    private class RequestResponseTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... request) {
            Log.d("TASK", "request: " + request[0]);
            try {
                return getResponse(request[0]);
            } catch (IOException ex) {
                cancel(true); // delegate to onCancelled(...)
                return ex.toString();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d("TASK", "response: " + response);
            TextView textView = findViewById(R.id.mainResponseTextView);
            textView.setText(response);
        }

        @Override
        protected void onCancelled(String message) {
            super.onCancelled(message);
            Log.e("SHIT", message);
            TextView textView = findViewById(R.id.mainErrorMessagetext);
            textView.setText(message);
        }
    }

    /**
     * Will throw NetworkOnMainThreadException if executed from the main thread.
     * in API version 11 and up (aka. Honeycomb aka. Android 3.0)
     */
    private static String getResponse(String request) throws IOException {
        //try {
        Log.d("MINE", "Going to make a connection");
        Socket socket = new Socket(SPECIAL_IP_TO_HOST_COMPUTER, PORT_NUMBER);
        Log.d("MINE", socket.toString());
        OutputStream output = socket.getOutputStream();
        output.write((request + "\n").getBytes());
        output.flush();
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response = input.readLine();
        Log.d("MINE", "response " + response);
        socket.close();
        return response;
    }
}
