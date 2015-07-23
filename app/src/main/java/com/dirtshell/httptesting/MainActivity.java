package com.dirtshell.httptesting;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the "Done" button to submit the URL
        EditText editText = (EditText) findViewById(R.id.url_to_load_field);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        makeRequest(v);
                    } catch (IOException e) {
                        Log.d("HTTP RESPONSE", "An IOException occurred");
                    }
                    handled = true;
                }
                return handled;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Gets the website specified when the button is clicked
     * @param view
     * @throws IOException
     */
    public void makeRequest(View view) throws IOException {
        EditText url_field = (EditText) findViewById(R.id.url_to_load_field);
        String url = url_field.getText().toString();
        Log.d("URL TO LOOKUP", url);
        GetWebsite websiteConnectionTask = new GetWebsite();
        websiteConnectionTask.execute(url);
    }

    /**
     * The async fetch running on a seperate thread
     */
    private class GetWebsite extends AsyncTask<String, Integer, String> {
        private Exception exception;
        String result = "";

        /**
         * Appends each byte of the response to the end output string
         * @param urls: The URLs of the page to get
         * @return A string of the response
         */
        protected String doInBackground(String... urls) {
            String line;

            // Create a URL from the string passed in and open the connection
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader bIn = new BufferedReader(in);

                // Listen until there is no more data from the connection
                while ((line = bIn.readLine()) != null) {
                    result += line;
                }

                Log.d("HTTP RESPONSE", "Successfully finished listening. Closing connection");
                urlConnection.disconnect();
            } catch (MalformedURLException e) {
                Log.d("HTTP RESPONSE", "A malformed URL Exception was thrown");
                result = "Please enter a properly formed URL.\nTry another address.";
            } catch(IOException e) {
                Log.d("HTTP RESPONSE", "A FileIO error occurred");
                result = "A FileIO error occurred.\nTry another address.";
            }
            return result;
        }

        protected void onPostExecute(String response) {
            Log.d("HTTP RESPONSE", "onPostExecute finished. Showing the text in the TextView");
            TextView textView = (TextView) findViewById(R.id.response_field);
            textView.setMovementMethod(new ScrollingMovementMethod());
            textView.setText(result);
        }
    }
}
