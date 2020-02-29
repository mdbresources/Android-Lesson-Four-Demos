package dev.mdb.imagedownloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnReset;
    private Button btnStart;
    private TextView txtTimer;

    private CountdownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnReset = findViewById(R.id.btnReset);
        btnStart = findViewById(R.id.btnPauseStart);
        txtTimer = findViewById(R.id.txtTime);

        // Create the instance of our task, with a default time of 30 seconds.
        // Then, start the execution, which will track the changes of the timer.
        timer = new CountdownTimer(30);
        timer.execute();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.reset();
                }
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtTimer = (TextView) v;
                if (txtTimer.getText().equals("Start")) {
                    timer.start();
                } else {
                    timer.pause();
                }
            }
        });
    }

    private class CountdownTimer extends AsyncTask<Void, Integer, Void> {

        private int timer;
        private int initialTime;
        private boolean paused;

        // Constructor for our task. DIFFERENT than the doInBackground.
        // This is not called with execute. It is just a normal Java
        // constructor.
        public CountdownTimer(int t) {
            timer = initialTime = t;
            paused = true;
            txtTimer.setText(String.valueOf(t));
        }

        // This is what will be called when we use execute.
        @Override
        protected Void doInBackground(Void... voids) {
            while (true) {
                while (timer > 0 && !paused) {
                    timer -= 1;
                    // This is what will call the onProgressUpdate function. We have to
                    // call the progress function manually.
                    publishProgress(timer);
                    try {
                        // This sleep will be done on our asynchronous task, so
                        // the UI thread will not have to wait the 1 second while
                        // the counter decrements.
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        protected void onProgressUpdate(Integer... time) {
            // We are allowed to manipulate the UI in this function. This will be done
            // on the UI thread so avoid intensive work in this function.
            txtTimer.setText(String.valueOf(time[0]));
            if (time[0] == 0) {
                txtTimer.setText("Timer Finished!");
            }
        }

        // Our AsyncTask can have our own custom functions to make
        // the task more controllable for the client.

        public void reset() {
            this.pause();
            this.timer = initialTime;
            txtTimer.setText(String.valueOf(initialTime));
        }

        public void pause() {
            this.paused = true;
            btnStart.setText("Start");
        }
        public void start() {
            this.paused = false;
            btnStart.setText("Pause");
        }
    }

}
