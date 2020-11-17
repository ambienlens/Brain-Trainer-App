package com.venomous.braintrainer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.icu.util.ValueIterator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout gameLayout;
    ConstraintLayout startLayout;
    ConstraintLayout scoreLayout;
    Button continueButton;
    Button startButton;
    Button resetButton;
    int timeLimit; //Time in seconds
    int maxTime;
    int userIn=0;
    int level;
    int totalQuestions=-1;
    int correctAnswer=0;
    boolean isFirst;
    boolean isFirstTime;
    boolean isCancel;
    boolean newGame;
    Vibrator v;

    Button buttonB;
    Button buttonA;
    Button buttonC;
    Button buttonD;
    int correctOption;
    TextView question;
    TextView timeLeft;
    TextView answered;
    TextView points;
    TextView outputStats;
    TextView cancel;
    ArrayList<Integer> answers;
    ArrayList<Integer> highscores;
    CountDownTimer timer;
    ArrayAdapter<Integer> arrayAdapter;
    ImageView backdrop;

    boolean isLevelSelected = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void cancel(View view)
    {
        isCancel=true;
        int id;
        Button thisButton;
        timer.cancel();
        resetGame();
        resetButton.setText("GO TO MAIN MENU");
    }

    public void selectLevel(View view)
    {
        String levelName[] = {"levelButton","levelButton1","levelButton2"};
        int id;
        Button thisButton;
        Button isLevel = (Button) view;

        for (int i = 0; i < 3; i++)
        {
            id = getResources().getIdentifier(levelName[i], "id", getPackageName());
            thisButton = findViewById(id);
            thisButton.setTextColor(Color.parseColor("#FFFFFF"));
        }

        switch (Integer.parseInt(isLevel.getTag().toString()))
        {
            case 1:
                timeLimit=60;
                level=10;
                break;

            case 2:
                timeLimit=45;
                level=20;
                break;

            case 3:
                timeLimit=35;
                level=30;
                break;
        }

        try {
            for (int i = 0; i < 3; i++) {
                Log.i("Iteration",String.valueOf(i));
                if (Integer.parseInt(isLevel.getTag().toString()) != (i+1)) {
                    id = getResources().getIdentifier(levelName[i], "id", getPackageName());
                    thisButton = findViewById(id);
                    thisButton.setTextColor(Color.parseColor("#606060"));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Toast.makeText(this, "Start training your brain!", Toast.LENGTH_SHORT).show();
        isLevelSelected=true;
        maxTime=timeLimit;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void resetGame()
    {
        resetButton.setText("PLAY AGAIN");
        v.vibrate(VibrationEffect.createOneShot(1200, VibrationEffect.DEFAULT_AMPLITUDE));
        backdrop.setVisibility(View.VISIBLE);
        scoreLayout.setAlpha(0);
        scoreLayout.setVisibility(View.VISIBLE);
        scoreLayout.animate().alpha(1).setDuration(500);
        int percentage;
        if(correctAnswer==0)
            percentage=0;
        else
            percentage = correctAnswer * 100 / totalQuestions;
        Log.i("Percentage", String.valueOf(percentage));
        points.setText(String.valueOf(percentage+"%"));
        outputStats.setText("Correct Answer: "+correctAnswer+" Out of: "+totalQuestions);
        newGame=false;
        continueButton.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        maxTime=timeLimit;
        totalQuestions=-1;
        correctAnswer=0;
        answers.clear();
    }

    public void playAgain(View view)
    {
        //startLayout.animate().translationXBy(-1100);
        backdrop.setVisibility(View.INVISIBLE);
        scoreLayout.setVisibility(View.INVISIBLE);
        startLayout.setTranslationX(-1100);
        if(!isCancel)
        {
            timeLeft.animate().scaleX(1f).scaleY(1f).setDuration(80);
            timeLeft.setTextColor(Color.parseColor("#B4B4B4"));
            startGame(view);
        }
        else
        {
            gameLayout.setVisibility(View.INVISIBLE);
            startLayout.animate().translationXBy(1100).setDuration(200);
        }
    }

    public void startGame(View view)
    {
        if(isLevelSelected) {
            continueButton.setVisibility(View.VISIBLE);
            startLayout.animate().translationXBy(-1100).setDuration(500);
            gameLayout.setAlpha(0);
            gameLayout.setVisibility(View.VISIBLE);
            gameLayout.animate().alpha(1).setDuration(1000);
            cancel.setVisibility(View.VISIBLE);
            newGame(view);
            timer = new CountDownTimer(maxTime * 1000 + 100, 1000) {
                @Override
                public void onTick(long millis) {
                    if ((millis / 1000) <= 10) {
                        timeLeft.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200);
                        timeLeft.setTextColor(Color.parseColor("#d76161"));
                    }
                    timeLeft.setText("Time: " + String.valueOf(millis / 1000) + "s");
                }

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onFinish() {
                    Toast.makeText(MainActivity.this, "Time is Up!", Toast.LENGTH_SHORT).show();
                    isCancel=false;
                    resetGame();
                }
            }.start();
        }
        else
        {
            Toast.makeText(this, "Select a level", Toast.LENGTH_SHORT).show();
        }
    }

    public void newGame(View view)
    {
        newGame=true;
        continueButton.setText("CONTINUE");

        int a, b;
        Random random = new Random();
        a = random.nextInt(level) + (level/2);
        b = random.nextInt(level) + (level/2);
        totalQuestions++;
        isFirst = true;
        isFirstTime=true;
        boolean alter=true;

        correctOption = random.nextInt(4);
        answers.clear();
        for (int i = 0; i < 4; i++) {
            if (i == correctOption) {
                answers.add(a + b);
            } else {
                int wrongAnswer;
                if(alter)
                {
                    wrongAnswer = a + b + (random.nextInt(15));
                    alter=false;
                } else {
                    wrongAnswer = a + b +5 - (random.nextInt(b)+a);
                    alter=true;
                }
                while (wrongAnswer == (a + b)) {
                    if(alter)
                    {
                        wrongAnswer = a + b + (random.nextInt(15));
                        alter=false;
                    } else {
                        wrongAnswer = a + b +2 - (random.nextInt(b)+a);
                        alter=true;
                    }
                }
                answers.add(wrongAnswer);
            }
        }

        buttonA.setText(Integer.toString(answers.get(0)));
        buttonA.setTextColor(Color.parseColor("#383838"));
        buttonB.setText(Integer.toString(answers.get(1)));
        buttonB.setTextColor(Color.parseColor("#383838"));
        buttonC.setText(Integer.toString(answers.get(2)));
        buttonC.setTextColor(Color.parseColor("#383838"));
        buttonD.setText(Integer.toString(answers.get(3)));
        buttonD.setTextColor(Color.parseColor("#383838"));
        question.setText("What is, " + a + " + " + b);
        answered.setText("Ans: " + correctAnswer + "/" + totalQuestions);
    }

    @SuppressLint("ObjectAnimatorBinding")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void input(View view)
    {
        Button thisButton = (Button) view;
        userIn=Integer.parseInt(thisButton.getTag().toString());

        if(userIn==correctOption+1){
            //for right input
            ObjectAnimator.ofObject(
                    view, // Object to animating
                    "textColor", // Property to animate
                    new ArgbEvaluator(), // Interpolation function
                    Color.parseColor("#383838"), // Start color
                    Color.parseColor("#158467") // End color
            ).setDuration(150).start();
        }
        else{
            //for wrong input
            isFirst=false;
            v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        }

        if(isFirst && isFirstTime)
            correctAnswer++;
        isFirstTime=false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = this.getWindow();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        gameLayout=findViewById(R.id.quizLayout);
        startLayout=findViewById(R.id.startLayout);
        scoreLayout=findViewById(R.id.scoreLayout);
        continueButton=findViewById(R.id.continueButton);
        startButton=findViewById(R.id.startButton);
        buttonA=findViewById(R.id.button);
        buttonB=findViewById(R.id.button1);
        buttonC=findViewById(R.id.button2);
        buttonD=findViewById(R.id.button3);
        question=findViewById(R.id.questionTextview);
        timeLeft=findViewById(R.id.timeLeft);
        answered=findViewById(R.id.questionAnswered);
        answers = new ArrayList<Integer>();
        outputStats = findViewById(R.id.outputStats);
        cancel = findViewById(R.id.cancelTextview);
        resetButton = findViewById(R.id.button4);

        highscores=new ArrayList<Integer>();
        highscores.add(0);
        highscores.add(0);
        highscores.add(0);
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, highscores);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        backdrop = findViewById(R.id.backdrop);
        points = findViewById(R.id.pointsTextview);

        //arraylist init.
        for (int i=0;i<4;i++)
            answers.add(0);

        startLayout.setVisibility(View.VISIBLE);
        gameLayout.setVisibility(View.INVISIBLE);
        backdrop.setVisibility(View.INVISIBLE);
        scoreLayout.setVisibility(View.INVISIBLE);
    }
}