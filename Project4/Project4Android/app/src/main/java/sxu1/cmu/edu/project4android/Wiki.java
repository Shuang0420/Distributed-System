package sxu1.cmu.edu.project4android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Shuang on 16/11/9.
 */

public class Wiki extends AppCompatActivity{

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

        /*
         * The click listener will need a reference to this object, so that upon successfully finding a picture from Heroku, it
         * can callback to this object with the resulting text string  The "this" of the OnClick will be the OnClickListener, not
         * this Wiki.
         */
            final Wiki wiki = this;
        /*
         * Find the "submit" button, and add a listener to it
         */
            Button submitButton = (Button)findViewById(R.id.submit);


            // Add a listener to the send button
            submitButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View viewParam) {
                    String searchTerm = ((EditText)findViewById(R.id.searchTerm)).getText().toString();
                    GetResult gp = new GetResult();
                    gp.search(searchTerm, wiki); // Done asynchronously in another thread.  It calls ip.pictureReady() in this thread when complete.
                }
            });
        }

        /*
         * This is called by the GetResult object when the picture is ready.  This allows for passing back the text string for updating the TextView.
         */
        public void docReady(String doc) {
            TextView searchView = (EditText)findViewById(R.id.searchTerm);
            TextView resultView = (TextView)findViewById(R.id.resultView);
            if (doc != null) {
                resultView.setText("Here is a document snippet of a "+((EditText)findViewById(R.id.searchTerm)).getText().toString()+"\n\n"+doc);
            } else {
                resultView.setText("Sorry, I could not find a document of a "+((EditText)findViewById(R.id.searchTerm)).getText().toString());
            }
            searchView.setText("");
        }
    }


