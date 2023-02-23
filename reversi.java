package com.example.myapplication;



import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.*;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class reversi {
    final int BLACK = 1;
    final int WHITE = -1;
    int PlayerTurn = 1;
    int pbord1 = 0x8000000;
    int pbord0 = 0x10;
    int obord1 = 0x10000000;
    int obord0 = 0x8;
    int canbord1 = 0x20100000;
    int canbord0 = 0x804;
    int[] aidata = new int[128];


    private Context con;
    private View originView;
    private View parentView;
    private File file;

    public reversi(Context context) {
        this.con = context;


        BufferedReader br = null;
        String fileName = "aioserodata.csv";
        file = new File(context.getFilesDir(), fileName);

        try {

            //読み込みファイルのインスタンス生成
            //ファイル名を指定する
//            br = new BufferedReader(new FileReader(file));
            Resources res = context.getResources();
            InputStream inputStream = res.openRawResource(R.raw.aioserodata);

//            InputStream inputStream = res.getResources().openRawResource("aioserodata.csv");
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            br = new BufferedReader(inputStreamReader);
            //読み込み行
            String line;

            //読み込み行数の管理
            int i = 0;


            //1行ずつ読み込みを行う
            while ((line = br.readLine()) != null) {

                //先頭行は列名
                if (i == 0) {

                    //カンマで分割した内容を配列に格納する
                    // arr = { "no","name","age","gender","bloodtype" };
                    String[] data = line.split(",");
                    aidata[0] = Integer.parseInt(data[0].trim());

                } else {

                    //データ内容をコンソールに表示する
                    System.out.println("-------------------------------");

                    //データ件数を表示
                    System.out.println("データ" + i + "件目");

                    //カンマで分割した内容を配列に格納する
                    String[] data = line.split(",");
                    aidata[i] = Integer.parseInt(data[0].trim());



                }

                //行数のインクリメント
                i++;

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private TextView getTextViewFromTag(int a, int b) {
        String s = String.valueOf(a) + String.valueOf(b);
        TextView res = parentView.findViewWithTag(s);
        return res;
    }

    private Integer SplitStrRCtoIntRow(String s) {
        int r;
        r = Integer.parseInt(s.substring(0, 1));
        return r;
    }

    private Integer SplitStrRCtoIntColumn(String s) {
        int c;
        c = Integer.parseInt(s.substring(1));
        return c;
    }

    private String TagToString(TextView v) {
        String s;
        s = v.getTag().toString();
        return s;
    }

    private boolean existStone(TextView v) {
        return !(v.getText().toString().equals(""));
    }

    private int getStoneColor(TextView v) {
        int sc = 0;

        switch (v.getText().toString()) {
            case ("●"):
                sc = BLACK;
                break;
            case ("○"):
                sc = WHITE;
        }
        return sc;
    }

    private int[] mobility(int p1,int p0,int o1,int o0) {
        int mob1 = 0;
        int mob0 = 0;
        int[] out = new int[2];



        int blank1 = ~(p1 | o1);
        int blank0 = ~(p0 | o0);

        int mo1 = o1 & 0x7e7e7e7e;
        int mo0 = o0 & 0x7e7e7e7e;

        // 右向き

        int ps1 = p1 << 1;
        int ps0 = p0 << 1;

        mob1  = (mo1 + ps1) & blank1 & ~ps1;
        mob0  = (mo0 + ps0) & blank0 & ~ps0;

        // 左向き

        int t0 = p0 >>> 1 & mo0;
        t0 |= t0 >>> 1 & mo0;
        t0 |= t0 >>> 1 & mo0;
        t0 |= t0 >>> 1 & mo0;
        t0 |= t0 >>> 1 & mo0;
        t0 |= t0 >>> 1 & mo0;

        mob0  |= t0 >>> 1 & blank0;

        int t1 = p1 >>> 1 & mo1;
        t1 |= t1 >>> 1 & mo1;
        t1 |= t1 >>> 1 & mo1;
        t1 |= t1 >>> 1 & mo1;
        t1 |= t1 >>> 1 & mo1;
        t1 |= t1 >>> 1 & mo1;

        mob1  |= t1 >>> 1 & blank1;

        // 上下

        mo1 = o1 & 0xffffff00;
        mo0 = o0 & 0x00ffffff;

        // 上向き
        t0 = p0 >>> 8 & mo0;
        t0 |= t0 >>> 8 & mo0;
        t0 |= t0 >>> 8 & mo0;

        t1 = (p1 >>> 8 | (t0 | p0) << 24) & mo1;
        t1 |= t1 >>> 8 & mo1;
        t1 |= t1 >>> 8 & mo1;

        mob1  |= (t1 >>> 8 | t0 << 24) & blank1;
        mob0  |= t0 >>> 8 & blank0;

        // 下
        t1 = p1 << 8 & mo1;
        t1 |= t1 << 8 & mo1;
        t1 |= t1 << 8 & mo1;

        t0 |= (p0 << 8 | (t1 | p1) >>> 24) & mo0;
        t0 |= t0 << 8 & mo0;
        t0 |= t0 << 8 & mo0;

        mob1  |= t1 << 8 & blank1;
        mob0  |= (t0 << 8 | t1 >>> 24) & blank0;

        // 斜め

        mo1 = o1 & 0x7e7e7e00;
        mo0 = o0 & 0x007e7e7e;

        // 左上
        t0 = p0 >>> 9 & mo0;
        t0 |= t0 >>> 9 & mo0;
        t0 |= t0 >>> 9 & mo0;

        t1 = (p1 >>> 9 | (t0 | p0) << 23) & mo1;
        t1 |= t1 >>> 9 & mo1;
        t1 |= t1 >>> 9 & mo1;

        mob1  |= (t1 >>> 9 | t0 << 23) & blank1;
        mob0  |= t0 >>> 9 & blank0;

        // 右下
        t1 = p1 << 9 & mo1;
        t1 |= t1 << 9 & mo1;
        t1 |= t1 << 9 & mo1;

        t0 = (p0 << 9 | (t1 | p1) >>> 23) & mo0;
        t0 |= t0 << 9 & mo0;
        t0 |= t0 << 9 & mo0;

        mob1  |= t1 << 9 & blank1;
        mob0  |= (t0 << 9 | t1 >>> 23) & blank0;

        // 右上
        t0 = p0 >>> 7 & mo0;
        t0 |= t0 >>> 7 & mo0;
        t0 |= t0 >>> 7 & mo0;

        t1 = (p1 >>> 7 | (t0 | p0) << 25) & mo1;
        t1 |= t1 >>> 7 & mo1;
        t1 |= t1 >>> 7 & mo1;

        mob1  |= (t1 >>> 7 | t0 << 25) & blank1;
        mob0  |= t0 >>> 7 & blank0;

        // 左下
        t1 = p1 << 7 & mo1;
        t1 |= t1 << 7 & mo1;
        t1 |= t1 << 7 & mo1;

        t0 = (p0 << 7 | (t1 | p1) >>> 25) & mo0;
        t0 |= t0 << 7 & mo0;
        t0 |= t0 << 7 & mo0;

        mob1  |= t1 << 7 & blank1;
        mob0  |= (t0 << 7 | t1 >>> 25) & blank0;

        // outには適当に使い回しのオブジェクトを渡す。
        // いちいちオブジェクトを作って返すのは実行速度に響きそうなので。
        // 全部展開するのが一番いいと思うが、めんどくさい。
        out[1] = mob1;
        out[0] = mob0;
        return  out;
    }

    private void setStoneToCell(TextView v) {
        switch (PlayerTurn) {
            case (BLACK):
                v.setText("●");
                break;
            case (WHITE):
                v.setText("○");
                break;
        }
    }

    private int[] flip1(int p1, int p0, int o1, int o0, int sq_bit) {
        int f1 = 0;
        int f0 = 0;
        int[] out = new int[2];

        int mo1 = o1 & 0x7e7e7e7e;
        int mo0 = o0 & 0x7e7e7e7e;

        // 右
        int d1 = 0x000000fe * sq_bit;
        int t1 = (mo1 | ~d1) + 1 & d1 & p1;
        f1 = t1 - ((t1 | -t1) >>> 31) & d1;

        // 左上
        t1 = sq_bit >>> 9 & o1;
        t1 |= t1 >>> 9 & o1;
        t1 |= t1 >>> 9 & o1;
        int t = t1 >>> 9 & p1;
        t = (t | -t) >> 31;
        f1 |= t1 & t;

        // 上 マスクは付けてはだめ。
        t1 = sq_bit >>> 8 & o1;
        t1 |= t1 >>> 8 & o1;
        t1 |= t1 >>> 8 & o1;
        t = t1 >>> 8 & p1;
        t = (t | -t) >> 31;
        f1 |= t1 & t;

        // 右上
        t1 = sq_bit >>> 7 & o1;
        t1 |= t1 >>> 7 & o1;
        t1 |= t1 >>> 7 & o1;
        t = t1 >>> 7 & p1;
        t = (t | -t) >> 31;
        f1 |= t1 & t;

        // 左
        t1 = sq_bit >>> 1 & mo1;
        t1 |= t1 >>> 1 & mo1;
        t1 |= t1 >>> 1 & mo1;
        t1 |= t1 >>> 1 & mo1;
        t1 |= t1 >>> 1 & mo1;
        t1 |= t1 >>> 1 & mo1;

        f1 |= t1 & -(t1 >>> 1 & p1);

        // 右下
        t1 = sq_bit << 9 & mo1;
        t1 |= t1 << 9 & mo1;
        t1 |= t1 << 9 & mo1;

        int t0 = (t1 | sq_bit) >>> 23 & mo0;
        t0 |= t0 << 9 & mo0;
        t0 |= t0 << 9 & mo0;



        t = t1 << 9 & p1 | (t0 << 9 | t1 >>> 23) & p0;
        t = (t | -t) >> 31;

        f1 |= t1 & t;
        f0 |= t0 & t;

        // 下 敵石にマスクはつけない
        t1 = sq_bit << 8 & o1;
        t1 |= t1 << 8 & o1;
        t1 |= t1 << 8 & o1;

        t0 = (t1 | sq_bit) >>> 24 & o0;
        t0 |= t0 << 8 & o0;
        t0 |= t0 << 8 & o0;

        t = t1 << 8 & p1 | (t0 << 8 | t1 >>> 24) & p0;
        t = (t | -t) >> 31;

        f1 |= t1 & t;
        f0 |= t0 & t;

        // 左下
        t1 = sq_bit << 7 & mo1;
        t1 |= t1 << 7 & mo1;
        t1 |= t1 << 7 & mo1;

        t0 = (t1 | sq_bit) >>> 25 & mo0;
        t0 |= t0 << 7 & mo0;
        t0 |= t0 << 7 & mo0;

        t = t1 << 7 & p1 | (t0 << 7 | t1 >>> 25) & p0;
        t = (t | -t) >> 31;

        f1 |= t1 & t;
        f0 |= t0 & t;

        out[1] = f1;
        out[0] = f0;
        return out;
    }


    private int[] flip0(int p1,int p0,int o1,int o0,int sq_bit) {
        int f1 = 0;
        int f0 = 0;
        int[] out = new int[2];

        int mo1 = o1 & 0x7e7e7e7e;
        int mo0 = o0 & 0x7e7e7e7e;

        // 右
        int d0 = 0x000000fe * sq_bit;
        int t0 = (mo0 | ~d0) + 1 & d0 & p0;
        f0 = t0 - ((t0 | -t0) >>> 31) & d0;

        // 左上
        t0 = sq_bit >>> 9 & mo0;
        t0 |= t0 >>> 9 & mo0;
        t0 |= t0 >>> 9 & mo0;

        int t1 = (t0 | sq_bit) << 23 & mo1;
        t1 |= t1 >>> 9 & mo1;
        t1 |= t1 >>> 9 & mo1;

        int t = (t1 >>> 9 | t0 << 23) & p1 | t0 >>> 9 & p0;
        t = (t | -t) >> 31;

        f1 |= t1 & t;
        f0 |= t0 & t;

        // 上 敵石にマスクはつけない
        t0 = sq_bit >>> 8 & o0;
        t0 |= t0 >>> 8 & o0;
        t0 |= t0 >>> 8 & o0;

        t1 = (t0 | sq_bit) << 24 & o1;
        t1 |= t1 >>> 8 & o1;
        t1 |= t1 >>> 8 & o1;

        t = (t1 >>> 8 | t0 << 24) & p1 | t0 >>> 8 & p0;
        t = (t | -t) >> 31;

        f1 |= t1 & t;
        f0 |= t0 & t;

        // 右上
        t0 = sq_bit >>> 7 & mo0;
        t0 |= t0 >>> 7 & mo0;
        t0 |= t0 >>> 7 & mo0;

        t1 = (t0 | sq_bit) << 25 & mo1;
        t1 |= t1 >>> 7 & mo1;
        t1 |= t1 >>> 7 & mo1;

        t = (t1 >>> 7 | t0 << 25) & p1 | t0 >>> 7 & p0;
        t = (t | -t) >> 31;

        f1 |= t1 & t;
        f0 |= t0 & t;

        // 左
        t0 = sq_bit >>> 1 & mo0;
        t0 |= t0 >>> 1 & mo0;
        t0 |= t0 >>> 1 & mo0;
        t0 |= t0 >>> 1 & mo0;
        t0 |= t0 >>> 1 & mo0;
        t0 |= t0 >>> 1 & mo0;

        f0 |= t0 & -(t0 >>> 1 & p0);

        // 右下
        t0 = sq_bit << 9 & mo0;
        t0 |= t0 << 9 & mo0;
        f0 |= t0 & -(t0 << 9 & p0);

        // 下 敵石マスク無し
        t0 = sq_bit << 8 & o0;
        t0 |= t0 << 8 & o0;
        f0 |= t0 & -(t0 << 8 & p0);

        // 左下
        t0 = sq_bit << 7 & mo0;
        t0 |= t0 << 7 & mo0;
        f0 |= t0 & -(t0 << 7 & p0);

        out[1] = f1;
        out[0] = f0;
        return out;
    }

    private int popcount(int x1,int x0) {
        int t0 = x1 - (x1 >>> 1 & 0x55555555);
        t0 = (t0 & 0x33333333) + ((t0 & 0xcccccccc) >>> 2);
        int t1 = x0 - (x0 >>> 1 & 0x55555555);
        t0 += (t1 & 0x33333333) + ((t1 & 0xcccccccc) >>> 2);
        t0 = (t0 & 0x0f0f0f0f) + ((t0 & 0xf0f0f0f0) >>> 4);
        int out = t0 * 0x01010101 >>> 24;
        return out;
    }


    public void putStone(TextView tv) {
        String stone = tv.getText().toString();
        int Row = SplitStrRCtoIntRow(TagToString(tv));
        int Column = SplitStrRCtoIntColumn(TagToString(tv));
        int pcount = 0;
        int ocount = 0;
        int sq_bit = 0;
        int[] out = new int[2];
        int ifrag = 0;

        parentView = (View) tv.getParent();
        originView = (View) parentView.getParent();

        if (!stone.equals("★")) {
            Toast.makeText(this.con, "そこに石は置けません", Toast.LENGTH_SHORT).show();
            return;
        }
        //★をリセット//
        int N=0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                if ((((canbord1 >>> N) &1) == 1)){
                    tv = getTextViewFromTag(i, j);
                    tv.setText("");
                }
                if ((((canbord0 >>> N) &1) == 1)){
                    tv = getTextViewFromTag(i+4, j);
                    tv.setText("");
                }
                N = N +1;
            }
        }


        //ひっくり返る石を求める//
        if (Row < 4) {

            sq_bit = (int) 1 << (Column + 8*(Row));
            out = flip1(pbord1,pbord0,obord1,obord0,sq_bit);
            ifrag = 1;
        }else{
            sq_bit = (int) 1 << (Column + 8*(Row-4));
            out = flip0(pbord1,pbord0,obord1,obord0,sq_bit);
            ifrag = 0;
        }

        int changebord1 = obord1;
        int changebord0 = obord0;

        obord1 = pbord1 + out[1];
        obord0 = pbord0 + out[0];
        pbord1 = changebord1 - out[1];
        pbord0 = changebord0 - out[0];

        if (ifrag==1){
            obord1 = obord1 + sq_bit;
        }else{
            obord0 = obord0 + sq_bit;
        }
        //石をひっくり返す//
        tv = getTextViewFromTag(Row, Column);
        setStoneToCell(tv);

        N = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                if ((((out[1] >>> N) &1) == 1)){
                    tv = getTextViewFromTag(i, j);
                    setStoneToCell(tv);
                }
                if ((((out[0] >>> N) &1) == 1)){
                    tv = getTextViewFromTag(i+4, j);
                    setStoneToCell(tv);
                }
                N = N +1;
            }
        }

        //石のおける場所を求める//

        out = mobility(pbord1,pbord0,obord1,obord0);
        canbord1 = out[1];
        canbord0 = out[0];
        N = 0;


        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                if ((((out[1] >>> N) &1) == 1)){
                    tv = getTextViewFromTag(i, j);
                    tv.setText("★");
                }
                if ((((out[0] >>> N) &1) == 1)){
                    tv = getTextViewFromTag(i+4, j);
                    tv.setText("★");
                }
                N = N +1;
            }
        }

        PlayerTurn = PlayerTurn * (-1);
        TextView turn = originView.findViewById(R.id.textViewturn);
        if (PlayerTurn == 1) {
            turn.setText("次は黒のターン");
        } else {
            turn.setText("次は白のターン");
        }

        pcount = popcount(pbord1,pbord0);
        ocount = popcount(obord1,obord0);

        String pcounttext = String.valueOf(pcount);
        String ocounttext = String.valueOf(ocount);
        TextView bc = originView.findViewById(R.id.textVieww);
        TextView wc = originView.findViewById(R.id.textViewb);

        if (PlayerTurn == 1) {
            bc.setText("●" + pcounttext);
            wc.setText("○" + ocounttext);
        } else {
            bc.setText("●" + ocounttext);
            wc.setText("○" + pcounttext);
        }
    putStonecpu();

    }

    public void putStonecpu() {
        TextView tv;
        int pcount = 0;
        int ocount = 0;
        int sq_bit = 0;
        int sq_bit1 = 0;
        int ifrag = 0;
        int[] bordp = new int[64];
        int[] bordo = new int[64];
        int changebord1 = 0;
        int changebord0 = 0;
        int changebord11 = 0;
        int changebord00 = 0;


        int[] out2 = new int[2];
        int[] out = new int[2];

        //★の中からaiが選択//
        int N=0;
        int num = 0;
        int sumai = 0;
        int sumai1 = -1;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                if ((((canbord1 >>> N) &1) == 1)){
                    sq_bit = 1 << N;
                    out2 = flip1(pbord1,pbord0,obord1,obord0,sq_bit);
                    changebord1 = pbord1 + out2[1] + sq_bit;
                    changebord0 = pbord0 + out2[0];
                    for (int k=0;k<32;k++){
                        if ((((changebord1 >>> k) &1) == 1)){
                            bordp[k]= 1;
                        }else{
                            bordp[k]= 0;
                        }
                        if ((((changebord0 >>> k) &1) == 1)){
                            bordp[k+32]= 1;
                        }else {
                            bordp[k + 32] = 0;
                        }

                    }
                    changebord1 = obord1 - out2[1];
                    changebord0 = obord0 - out2[0];
                    //bordo:入力データ//
                    for (int k=0;k<32;k++){
                        if ((((changebord1 >>> k) &1) == 1)){
                            bordo[k]= 1;
                        }else{
                            bordo[k]= 0;
                        }
                        if ((((changebord0 >>> k) &1) == 1)){
                            bordo[k+32]= 1;
                        }else {
                            bordo[k + 32] = 0;
                        }
                    }

                    for (int k=0;k<64;k++){
                        sumai = sumai + bordp[k]*aidata[k];
                        sumai = sumai + bordo[k]*aidata[k+64];
                    }
                    if(sumai1 < sumai ){
                        changebord00 = out2[0];
                        changebord11 = out2[1];
                        sq_bit1 = sq_bit;
                        num = N;
                        sumai1 = sumai;
                        ifrag=1;
                    }
                    sumai = 0;

                }
                if ((((canbord0 >>> N) &1) == 1)){
                    sq_bit = 1 << N;
                    out2 = flip0(pbord1,pbord0,obord1,obord0,sq_bit);
                    changebord1 = pbord1 + out2[1];
                    changebord0 = pbord0 + out2[0] + sq_bit;
                    for (int k=0;k<32;k++){
                        if ((((changebord1 >>> k) &1) == 1)){
                            bordp[k]= 1;
                        }else{
                            bordp[k]= 0;
                        }
                        if ((((changebord0 >>> k) &1) == 1)){
                            bordp[k+32]= 1;
                        }else {
                            bordp[k + 32] = 0;
                        }

                    }
                    changebord1 = obord1 - out2[1];
                    changebord0 = obord0 - out2[0];
                    //bordo:入力データ//
                    for (int k=0;k<32;k++){
                        if ((((changebord1 >>> k) &1) == 1)){
                            bordo[k]= 1;
                        }else{
                            bordo[k]= 0;
                        }
                        if ((((changebord0 >>> k) &1) == 1)){
                            bordo[k+32]= 1;
                        }else {
                            bordo[k + 32] = 0;
                        }
                    }

                    for (int k=0;k<64;k++){
                        sumai = sumai + bordp[k]*aidata[k];
                        sumai = sumai + bordo[k]*aidata[k+64];
                    }
                    if(sumai1 < sumai ){
                        changebord00 = out2[0];
                        changebord11 = out2[1];
                        sq_bit1 = sq_bit;
                        num = N+32;
                        sumai1 = sumai;
                        ifrag=0;
                    }
                    sumai = 0;

                }
                N = N +1;
            }
        }
        N=0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                if ((((canbord1 >>> N) &1) == 1)){
                    tv = getTextViewFromTag(i, j);
                    tv.setText("");
                }
                if ((((canbord0 >>> N) &1) == 1)){
                    tv = getTextViewFromTag(i+4, j);
                    tv.setText("");
                }
                N = N +1;
            }
        }

        changebord1 = obord1;
        changebord0 = obord0;

        obord1 = pbord1 + changebord11;
        obord0 = pbord0 + changebord00;
        pbord1 = changebord1 - changebord11;
        pbord0 = changebord0 - changebord00;

        if (ifrag==1){
            obord1 = obord1 + sq_bit1;
        }else{
            obord0 = obord0 + sq_bit1;
        }
        //石をひっくり返す//
        int Row;
        int Column;



        Row = num/8;
        Column = num%8;



        tv = getTextViewFromTag(Row, Column);
        setStoneToCell(tv);

        N = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                if ((((changebord11 >>> N) &1) == 1)){
                    tv = getTextViewFromTag(i, j);
                    setStoneToCell(tv);
                }
                if ((((changebord00 >>> N) &1) == 1)){
                    tv = getTextViewFromTag(i+4, j);
                    setStoneToCell(tv);
                }
                N = N +1;
            }
        }

        //石のおける場所を求める//

        out = mobility(pbord1,pbord0,obord1,obord0);
        canbord1 = out[1];
        canbord0 = out[0];
        N = 0;


        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                if ((((out[1] >>> N) &1) == 1)){
                    tv = getTextViewFromTag(i, j);
                    tv.setText("★");
                }
                if ((((out[0] >>> N) &1) == 1)){
                    tv = getTextViewFromTag(i+4, j);
                    tv.setText("★");
                }
                N = N +1;
            }
        }

        PlayerTurn = PlayerTurn * (-1);
        TextView turn = originView.findViewById(R.id.textViewturn);
        if (PlayerTurn == 1) {
            turn.setText("次は黒のターン");
        } else {
            turn.setText("次は白のターン");
        }

        pcount = popcount(pbord1,pbord0);
        ocount = popcount(obord1,obord0);

        String pcounttext = String.valueOf(pcount);
        String ocounttext = String.valueOf(ocount);
        TextView bc = originView.findViewById(R.id.textVieww);
        TextView wc = originView.findViewById(R.id.textViewb);

        if (PlayerTurn == 1) {
            bc.setText("●" + pcounttext);
            wc.setText("○" + ocounttext);
        } else {
            bc.setText("●" + ocounttext);
            wc.setText("○" + pcounttext);
        }


    }



}
