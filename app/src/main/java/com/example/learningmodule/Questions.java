package com.example.learningmodule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Questions extends AppCompatActivity implements View.OnClickListener{

    private ImageView question;
    private Button option1, option2, option3, option4;
    //private List<QuestionList> questionList;
    ArrayList<QuestionList> questionList;
    int questionNum;
    int score;
    private ActionBar toolbar;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String data = extras.getString("catTitle");

        toolbar = getSupportActionBar();
        toolbar.setTitle(data);

        final String questionref = data;


        question = findViewById(R.id.image_question);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);

        questionList = new ArrayList<>();

        getQuestionList(data);


        score = 0;
    }

    private  void getQuestionList(String data){
        questionList.clear();
            reference = FirebaseDatabase.getInstance().getReference().child("Question").child(data);
            reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    QuestionList l = dataSnapshot1.getValue(QuestionList.class);
                    questionList.add(l);
                }
                setQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       // setQuestion();

    }

    private void setQuestion(){
        //question.setImageAlpha(Integer.parseInt(questionList.get(0).getQuestion()));
        Picasso.get().load(questionList.get(0).getQuestion()).into(question);
        option1.setText(questionList.get(0).getOption1());
        option2.setText(questionList.get(0).getOption2());
        option3.setText(questionList.get(0).getOption3());
        option4.setText(questionList.get(0).getOption4());

        questionNum = 0;

    }

    @Override
    public void onClick(View v) {
        int selectedOption = 0;

        switch (v.getId()){
            case R.id.option1:
                selectedOption = 1;
                break;

            case R.id.option2:
                selectedOption = 2;
                break;

            case R.id.option3:
                selectedOption = 3;
                break;

            case R.id.option4:
                selectedOption = 4;
                break;

            default:;
        }

        checkAnswer(selectedOption, v);

    }

    private void checkAnswer(int selectedOption, View view){
        if(selectedOption == questionList.get(questionNum).getCorrectAnswer()){
            //Right Answer
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            score++;

        }else{
            //Wrong Answer
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.RED));

            switch (questionList.get(questionNum).getCorrectAnswer())
            {
                case 1:
                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 2:
                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 3:
                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 4:
                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;

            }
        }

        changeQuestion();
    }

    private void changeQuestion(){
        if(questionNum<questionList.size()-1){
            questionNum++;

            playAnim(question,0,0);
            playAnim(option1,0,1);
            playAnim(option2,0,2);
            playAnim(option3,0,3);
            playAnim(option4,0,4);

        }else{
            //display score
            int finalscore = score*10;
            //int finalscore = score;
            Intent intent = new Intent(Questions.this, Score.class);
            intent.putExtra("finalscore", String.valueOf(finalscore));
            startActivity(intent);
            //Questions.this.finish();
        }
    }

    private void playAnim(final View view, final int value, final int viewNum){
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500)
                .setStartDelay(100).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(value == 0)
                        {
                            switch (viewNum)
                            {
                                case 0:
                                    Picasso.get().load(questionList.get(questionNum).getQuestion()).into(question);
                                    break;
                                case 1:
                                    ((Button)view).setText(questionList.get(questionNum).getOption1());
                                    break;
                                case 2:
                                    ((Button)view).setText(questionList.get(questionNum).getOption2());
                                    break;
                                case 3:
                                    ((Button)view).setText(questionList.get(questionNum).getOption3());
                                    break;
                                case 4:
                                    ((Button)view).setText(questionList.get(questionNum).getOption4());
                                    break;

                            }


                            if(viewNum != 0)
                                ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BA32AC")));


                            playAnim(view,1,viewNum);

                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}