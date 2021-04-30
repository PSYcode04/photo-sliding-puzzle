package com.example.photoslidingpuzzle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_FILE = 23;
    private static final int ACCESS_FILE = 43;

    private long backKeyPressedTime = 0;

    // 변수
    public int num = 3; // 퍼즐 초기값은 3*3
    protected Bitmap[] oriPuzzlePiece; // 이미지 비트맵 배열
    protected PuzzlePiece[] puzzlePiece; // 퍼즐 정답 형태 배열
    protected PuzzlePiece[] shufflePiece; // shuffle했을 때 배열
    private int[] puzzleOrder;
    int[][] board; // 3*3, 4*4 형태 위치를 찾기 위한 보드판
    int loc = 0;
    int x = 0, y = 0; // 보드판에서 좌표
    private boolean answer = false; // 정답 체크
    private boolean click = false; // shuffle버튼 눌렀는지 체크
    int cnt = 0; // 자기보다 큰 인덱스가 더 작은 자리에 위치해 있는 개수

    int width;  // 화면 가로 크기 구하기


    ActionBar actionBar;
    ImageView imageView;
    Bitmap selectImage;
    Button select3by3;
    Button select4by4;
    Button shuffle;
    GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        checkSelfPermission();  // Check Permission

        // Intro 액티비티 종료
        IntroActivity introActivity = (IntroActivity)IntroActivity.activity;
        introActivity.finish();


        // 화면 크기 구하기
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size); // or getSize(size)
        width = size.x - 70; // 디스플레이 가로 크기에서 50(pixel) 빼기

        actionBar = getSupportActionBar();
        imageView = (ImageView) findViewById(R.id.originalImage);
        select3by3 = (Button) findViewById(R.id.button1);
        select4by4 = (Button) findViewById(R.id.button2);
        shuffle = (Button) findViewById(R.id.shuffle);
        gridView = (GridView) findViewById(R.id.GridView);

        // 기본 이미지 보드 설정
        selectImage = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        resizeImage();
        setImage(3);

        // 이미지 선택
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_FILE);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "select"), ACCESS_FILE);

                }
            }
        });

        // 버튼 클릭하면 나눌 숫자 변경
        // 3*3
        select3by3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setImage(3);
            }
        });

        // 4*4
        select4by4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setImage(4);
            }
        });

        // SHUFFLE 버튼
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = true; // shuffle을 눌렀음을 표시
                answer = false;
                cnt = 0;
                int length = num * num ;
                int lastNum = length - 1;
                
                PuzzleAdapter adapter = new PuzzleAdapter(getApplicationContext(), selectImage, num, puzzlePiece);

                while(true) {
                    cnt = 0;
                    Collections.shuffle(Arrays.asList(adapter.newPuzzlePiece));
                    // shuffle 눌렀을 때의 퍼즐 순서 저장
                    for(int i = 0; i < length; i++) {
                        shufflePiece[i] = new PuzzlePiece(adapter.newPuzzlePiece[i].getImagePiece(), adapter.newPuzzlePiece[i].getIndex());
                    }

                    // 무질서도 체크
                    for(int i = 0; i < length; i++) {
                        // 빈칸은 건너뜀
                        if(shufflePiece[i].getIndex() == lastNum && num == 3) {
                            continue;
                        } else if (shufflePiece[i].getIndex() == lastNum && num == 4) {
                            cnt += i/num + 1;
                            continue;
                        }
                        for (int j = i + 1; j < length; j++) {
                            // 현재 자리에 원래 자릿값 보다 작은 수가 뒤에 있고, 그 수가 빈칸의 인덱스(8 or 15)가 아니라면 cnt ++
                             if(shufflePiece[i].getIndex() > shufflePiece[j].getIndex() && shufflePiece[j].getIndex() != lastNum)
                                cnt++;
                        }
                    }

                    if(cnt % 2 == 0)
                        break;
                }


                gridView.setAdapter(adapter);
                gridView.setNumColumns(num);
            }
        });

        // 퍼즐 클릭했을 때 동작
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            int clickLocation; // 클릭한 퍼즐 위치
            int blank; // 빈칸 위치

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickLocation = position;
                blank = shufflePiece.length - 1;

                // shuffle버튼을 누르고, 정답이 아닐 경우에만 클릭 가능
                if(click && !answer) {
                    // 클릭한 퍼즐 좌표 찾기
                    for(int h = 0; h < num; h++) {
                        for(int w = 0; w < num; w++){
                            if(board[h][w] == clickLocation) {
                                y = h;
                                x = w;
                                break;
                            }
                        }
                    }

                    // 퍼즐 초기값은 3*3
                    if(y - 1 >= 0 && shufflePiece[board[y-1][x]].getIndex() == blank){
                        swapPiece(clickLocation, board[y-1][x]);
                        checkAnswer();
                    }
                    // 아래쪽이 빈칸인 경우
                    else if(y + 1 < num  && shufflePiece[board[y+1][x]].getIndex() == blank) {
                        swapPiece(clickLocation, board[y+1][x]);
                        checkAnswer();
                    }
                    // 오른쪽이 빈칸인 경우
                    else if(x + 1 < num && shufflePiece[board[y][x+1]].getIndex() == blank){
                        swapPiece(clickLocation, board[y][x+1]);
                        checkAnswer();
                    }
                    // 왼쪽이 빈칸인 경우
                    else if(x - 1 >= 0 && shufflePiece[board[y][x-1]].getIndex() == blank){
                        swapPiece(clickLocation, board[y][x-1]);
                        checkAnswer();
                    }
                    // 주변에 빈칸이 없는 공간을 클릭했을 경우는 동작 안함

                    // 클릭한 후, 이동한 퍼즐을 화면에 표시
                    PuzzleAdapter adapter = new PuzzleAdapter(getApplicationContext(), selectImage, num, shufflePiece);
                    gridView.setAdapter(adapter);
                    gridView.setNumColumns(num);

                    // 정답이라면 Toast메시지 출력후, 더 이상 클릭 불가!
                    if(answer)
                        Toast.makeText(getApplicationContext(), "FINISH!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show();
            return;
        }
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
        }
    }

    // 이미지 리사이즈
    protected void resizeImage(){
        Bitmap resized = null;

        resized = Bitmap.createScaledBitmap(selectImage, width, width, true);
        selectImage = resized;
    }

    // 보드판 update
    protected void setImage(int puzzleNum){
        num = puzzleNum;
        loc = 0;
        click = false;
        cutImage(selectImage);
        answer = false;
        puzzlePiece = new PuzzlePiece[num*num];
        shufflePiece = new PuzzlePiece[num*num];

        for(int i = 0; i < num*num; i++) {
            puzzlePiece[i] = new PuzzlePiece(oriPuzzlePiece[i], i);
        }

        board = new int[num][num];
        for(int i = 0; i < num; i++) {
            for(int j = 0; j < num; j++, loc++)
                board[i][j] = loc;
        }

        PuzzleAdapter adapter = new PuzzleAdapter(getApplicationContext(), selectImage, num, puzzlePiece);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(num);
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACCESS_FILE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri FILE_URI = data.getData();

            CropImage.activity(FILE_URI)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageView.setImageURI(resultUri);
                selectImage = null;
                try {
                    selectImage = MediaStore.Images.Media.getBitmap(getContentResolver(),result.getUri());
                    resizeImage();
                    setImage(3);
                    Toast.makeText(this, "Select!", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /////이미지, 퍼즐 설정/////////
    // 이미지 비트맵 분할
    public void cutImage(Bitmap image) {
        int x, y;
        int i = 0;
        oriPuzzlePiece = new Bitmap[num * num];

        for (y = 0; y <= (image.getHeight() - image.getHeight() / num); y += image.getHeight() / num) {
            for (x = 0; x <= (image.getWidth() - image.getWidth() / num); x += image.getWidth() / num, i++) {
                if(i == num*num-1)
                    break;
                oriPuzzlePiece[i] = Bitmap.createBitmap(image, x, y, image.getWidth()/ num, image.getHeight() / num);
            }
        }
    }

    // 정답인지 체크
    private void checkAnswer() {

        for(int i = 0; i < num*num; i++) {
            if(shufflePiece[i].getIndex() == i)
                continue;
            else {
                answer = false;
                return;
            }
        }
        answer = true;
    }

    // 퍼즐 위치 swap
    public void swapPiece(int loc1, int loc2){
        PuzzlePiece tmp1 = shufflePiece[loc1]; // 클릭한 위치
        PuzzlePiece tmp2 = shufflePiece[loc2]; // 변경할 위치?

        shufflePiece[loc1] = tmp2;
        shufflePiece[loc2] = tmp1;
    }


    /////권한 요청///
    public void checkSelfPermission() {
        String temp = "";

        //파일 읽기 권한 확인
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }

        //파일 쓰기 권한 확인
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }

        if(TextUtils.isEmpty(temp) == false) {
            // 권한 요청
            ActivityCompat.requestPermissions(this, temp.trim().split(" "),1);
        }
    }


    ///////TOOLBAR//////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if(item.getItemId() == R.id.action_info) {
            showInfo();
            // Toast.makeText(getApplicationContext(), "버튼 클릭", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void showInfo(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);

        alertBuilder.setTitle("** 게임 설명 **");
        alertBuilder.setMessage("이미지를 클릭하면 원하는 이미지로 바꿀 수 있습니다.\n\nSHUFFLE 버튼을 클릭하면 게임을 시작할 수 있습니다. ");
        alertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }
    /////////////////////////////////////////

}

