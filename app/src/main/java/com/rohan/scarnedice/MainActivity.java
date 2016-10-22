package com.rohan.scarnedice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int userTotalScore;
    private int userCurrentTurnScore;
    private int computerTotalScore;
    private int computerCurrentTurnScore;

    Button rollButton;
    Button holdButton;
    Button resetButton;

    ImageView diceImageView;

    TextView winningPointsHeaderTextView;
    TextView resultTextView;
    TextView userTotalTextView;
    TextView computerTotalTextView;
    TextView currentTurnTextView;

    Random random;

    private int winningScore;
    private int computerTurnDelay;

    boolean isUserTurn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        random = new Random();

        isUserTurn = true;

        rollButton = (Button) findViewById(R.id.roll_button);
        holdButton = (Button) findViewById(R.id.hold_button);
        resetButton = (Button) findViewById(R.id.reset_button);
        diceImageView = (ImageView) findViewById(R.id.dice_image_view);

        winningPointsHeaderTextView = (TextView) findViewById(R.id.winning_points_header_text_view);
        resultTextView = (TextView) findViewById(R.id.result_text_view);
        userTotalTextView = (TextView) findViewById(R.id.user_total_text_view);
        computerTotalTextView = (TextView) findViewById(R.id.computer_total_text_view);
        currentTurnTextView = (TextView) findViewById(R.id.current_turn_score_text_view);

        winningScore = 100;
        computerTurnDelay = 1000; //for milliseconds
        updateWinningScore();

        rollButton.setOnClickListener(this);
        holdButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.roll_button:
                rollDice();
                break;

            case R.id.hold_button:
                hold();
                break;

            case R.id.reset_button:
                reset();
                break;
        }
    }

    private void reset() {

        userCurrentTurnScore = 0;
        computerCurrentTurnScore = 0;
        userTotalScore = 0;
        computerTotalScore = 0;

        currentTurnTextView.setText(getString(R.string.user_current_turn_score).concat(String.valueOf(0)));
        userTotalTextView.setText(getString(R.string.user_total_score).concat(String.valueOf(0)));
        computerTotalTextView.setText(getString(R.string.computer_total_score).concat(String.valueOf(0)));

        resultTextView.setVisibility(View.INVISIBLE);
        holdButton.setVisibility(View.VISIBLE);
        rollButton.setVisibility(View.VISIBLE);

        enableButtons();
        isUserTurn = true;
    }

    private void enableButtons() {
        rollButton.setEnabled(true);
        holdButton.setEnabled(true);
    }

    private void disableButtons() {
        rollButton.setEnabled(false);
        holdButton.setEnabled(false);
    }

    private void hold() {
        if (isUserTurn) {
            userTotalScore += userCurrentTurnScore;
            userCurrentTurnScore = 0;
            currentTurnTextView.setText(getString(R.string.computer_current_turn_score).concat(String.valueOf(computerCurrentTurnScore)));
            userTotalTextView.setText(new StringBuilder().append(getString(R.string.user_total_score)).append(userTotalScore).toString());
            isUserTurn = false;
            rollDice();

        } else {
            computerTotalScore += computerCurrentTurnScore;
            computerCurrentTurnScore = 0;
            currentTurnTextView.setText(getString(R.string.user_current_turn_score).concat(String.valueOf(userCurrentTurnScore)));
            computerTotalTextView.setText(new StringBuilder().append(getString(R.string.computer_total_score)).append(computerTotalScore).toString());
            isUserTurn = true;
            Toast.makeText(this, "Computer holds. User's turn..", Toast.LENGTH_SHORT).show();

            enableButtons();
        }


    }

    private void changeDiceImage(int n) {
        switch (n) {
            case 1:
                diceImageView.setImageResource(R.drawable.dice1);
                break;
            case 2:
                diceImageView.setImageResource(R.drawable.dice2);
                break;
            case 3:
                diceImageView.setImageResource(R.drawable.dice3);
                break;
            case 4:
                diceImageView.setImageResource(R.drawable.dice4);
                break;
            case 5:
                diceImageView.setImageResource(R.drawable.dice5);
                break;
            case 6:
                diceImageView.setImageResource(R.drawable.dice6);
                break;
        }
    }

    private void computerTurn(final int n) {

        disableButtons();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                userCurrentTurnScore = 0;

                changeDiceImage(n);

                if (n == 1) {
                    computerCurrentTurnScore = 0;
                    isUserTurn = true;
                    currentTurnTextView.setText(getString(R.string.user_current_turn_score).concat(String.valueOf(userCurrentTurnScore)));

                    Toast.makeText(MainActivity.this, "Computer rolled a one. User's turn..", Toast.LENGTH_SHORT).show();

                    enableButtons();
                } else {

                    computerCurrentTurnScore += n;
                    currentTurnTextView.setText(getString(R.string.computer_current_turn_score).concat(String.valueOf(computerCurrentTurnScore)));

                    //Check if computer has won
                    if (computerTotalScore + computerCurrentTurnScore >= winningScore) {
                        resultTextView.setText(getString(R.string.result_computer_wins));
                        resultTextView.setVisibility(View.VISIBLE);
                        holdButton.setVisibility(View.GONE);
                        rollButton.setVisibility(View.GONE);

                        computerTotalScore += computerCurrentTurnScore;
                        computerTotalTextView.setText(new StringBuilder().append(getString(R.string.computer_total_score)).append(computerTotalScore).toString());
                        enableButtons();
                    }
                    //Hold if computerCurrentTurnScore > 20% of winning score
                    else if (computerCurrentTurnScore >= 0.2 * winningScore) {
                        hold();
                        return;
                    } else {
                        rollDice();
                    }

                }


            }
        }, computerTurnDelay);
    }

    private void updateScores(int n) {

        //User's Turn
        if (isUserTurn) {

            computerCurrentTurnScore = 0;

            changeDiceImage(n);

            if (n == 1) {

                userCurrentTurnScore = 0;
                isUserTurn = false;
                currentTurnTextView.setText(getString(R.string.computer_current_turn_score).concat(String.valueOf(computerCurrentTurnScore)));

                rollDice();

            } else {

                userCurrentTurnScore += n;
                currentTurnTextView.setText(getString(R.string.user_current_turn_score).concat(String.valueOf(userCurrentTurnScore)));

                //Check if user has won
                if (userCurrentTurnScore + userTotalScore >= winningScore) {
                    resultTextView.setText(getString(R.string.result_user_win));
                    resultTextView.setVisibility(View.VISIBLE);
                    holdButton.setVisibility(View.GONE);
                    rollButton.setVisibility(View.GONE);

                    userTotalScore += userCurrentTurnScore;

                    userTotalTextView.setText(new StringBuilder().append(getString(R.string.user_total_score)).append(userTotalScore).toString());
                }
            }

        }

        //Computer's Turn
        else {
            computerTurn(n);
        }
    }

    private void rollDice() {
        int number = random.nextInt(6) + 1;
        updateScores(number);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set_winning_points:
                setWinningPoints();
                return true;
            case R.id.action_computer_turn_delay:
                setComputerTurnDelay();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateWinningScore() {
        String str = "First to reach <b>" + winningScore + "</b> points wins.";
        winningPointsHeaderTextView.setText(Html.fromHtml(str));
    }

    private void setWinningPoints() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setMessage("Set winning points");

        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(10);
        numberPicker.setMaxValue(100);

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                winningScore = numberPicker.getValue();
                updateWinningScore();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        final FrameLayout parent = new FrameLayout(this);
        parent.addView(numberPicker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        builder.setView(parent);

        Dialog dialog = builder.create();
        dialog.show();
    }

    private void setComputerTurnDelay() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setMessage("Set computer turn delay (in seconds)");

        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(5);

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                computerTurnDelay = numberPicker.getValue() * 1000;     //for milliseconds
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        final FrameLayout parent = new FrameLayout(this);
        parent.addView(numberPicker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        builder.setView(parent);

        Dialog dialog = builder.create();
        dialog.show();
    }
}
