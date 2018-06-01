package com.anime.rezero.demochat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //-------------------------------------------------------------------------
    Button btnChup,btnChon;
    private  final String CLIENT_SEND_IMAGE = "CLIENT_SEND_IMAGE";
    private  final String SERVER_SEND_IMAGE = "SERVER_SEND_IMAGE";
    private  final String SERVER_SEND_SOUND = "SERVER_SEND_SOUND";
    private  final String CLIENT_SEND_REQUEST = "CLIENT_SEND_REQUEST";
    private  final String CLIENT_SEND_REQUEST_SOUND = "CLIENT_SEND_REQUEST_SOUND";
    private  final String CLIENT_SEND_SOUND = "CLIENT_SEND_SOUND";
    public final int REQUEST_CHUPHINH=123;
    public final int REQUEST_CHONHINH=321;
    //ImageView imgShow;
    byte[] imgAnh;


    //--------------------------------------------------------------------------
    public static ArrayList<NoiDungChat> noiDungChatArrayList;
    public  static NoiDungAdapter adapter2;
    EditText editUser,editChat,editIP;
    Button btnDangky,btnChat,btnOk,btnDangky1;
    Button btnGhiAm,btnDungGhiAm,btnGui,btnLay;
    Recorder recoder;
    ListView lvChat;
    ArrayAdapter adapter;

    /* biến msocket kết nối client và server*/
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://192.168.43.186:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
        mSocket.connect();
        mSocket.on(SERVER_SEND_IMAGE,onNewImage);
        mSocket.on(SERVER_SEND_SOUND,onNewSound);
        mSocket.on("ketquaDangKy",onNewMessageDangKy);
        mSocket.on("server-gui-chat",onNewMessage_DanhSachChat);
        setEven();
    }

    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    public  void initWidget(){
        editIP = (EditText) findViewById(R.id.edit_ip);
        btnDangky1 = (Button) findViewById(R.id.btn_dangky1);
        lvChat = (ListView) findViewById(R.id.lv_chat);
        editChat = (EditText) findViewById(R.id.edit_chat);
        btnChat = (Button) findViewById(R.id.btn_chat);
        editUser = (EditText) findViewById(R.id.edit_user);
        btnDangky = (Button) findViewById(R.id.btn_dangky);
        noiDungChatArrayList = new ArrayList<>();
        adapter2 = new NoiDungAdapter(getApplicationContext(),R.layout.noidungchat_activity,noiDungChatArrayList);
        lvChat.setAdapter(adapter2);
        //-------------------------------------------------------------------------
        btnChon = (Button) findViewById(R.id.btn_chonanh);
        btnChup = (Button) findViewById(R.id.btn_chupanh);
        //imgShow = (ImageView) findViewById(R.id.img_anh);
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnGhiAm = (Button) findViewById(R.id.btn_ghiam);
        btnDungGhiAm = (Button) findViewById(R.id.btn_dungghiam);
        btnGui = (Button) findViewById(R.id.btn_gui);
        btnLay = (Button) findViewById(R.id.btn_lay);
        recoder = new Recorder(this);
        //-------------------------------------------------------------------------

    }

    public  void setEven(){

        btnDangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("client-gui-username",editUser.getText().toString());
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("client-gui-tin-chat",editChat.getText().toString());
            }
        });

        //-------------------------------------------------------------------------
        btnChon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chonHinh();
            }
        });

        btnChup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chupHinh();

            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayHinhTuServer();
            }
        });

        btnGhiAm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoder.start();
            }
        });
        btnDungGhiAm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoder.stop();
            }
        });

        btnGui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendSound();
            }
        });

        btnLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestSound();
            }
        });

        lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoiDungChat noidungchat = noiDungChatArrayList.get(position);
                byte[] bytes = noidungchat.getAmthanh();
                playMp3FromByte(bytes);
            }
        });

        //-------------------------------------------------------------------------

    }


    public void sendRequestSound(){
        mSocket.emit(CLIENT_SEND_REQUEST_SOUND,"abcc");
    }

    public void SendSound(){
        String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ghiam.3gpp";
        byte[] bytes = getByteArrayFromLocalFile(outputFile);
        mSocket.emit(CLIENT_SEND_SOUND,bytes);
    }

    public void LayHinhTuServer(){
        mSocket.emit(CLIENT_SEND_REQUEST,"abc");
    }


    public  void chupHinh(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_CHUPHINH);
    }

    public  void chonHinh(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CHONHINH);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CHONHINH&& resultCode == RESULT_OK){
            try {
                Uri imageURI =data.getData();
                InputStream is = getContentResolver().openInputStream(imageURI);
                Bitmap bm = BitmapFactory.decodeStream(is);
                byte[] bytes = ChuyenThanhMangByte(bm);
                mSocket.emit(CLIENT_SEND_IMAGE,bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }else if(requestCode==REQUEST_CHUPHINH&& resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            byte[] bytes = ChuyenThanhMangByte(bitmap);
            mSocket.emit(CLIENT_SEND_IMAGE,bytes);
        }
    }

    public byte[] ChuyenThanhMangByte(Bitmap bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    //Ghi Âm
    public byte[] getByteArrayFromLocalFile(String path){
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }

    //-------------------------------------------------------------------------
    private Emitter.Listener onNewMessage_DanhSachChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String noidung;

                    try {
                        noidung = data.getString("tinchat");
                        noiDungChatArrayList.add(new NoiDungChat(noidung,new byte[0],new byte[0]));
                        adapter2.notifyDataSetChanged();

                    } catch (JSONException e) {
                        return;
                    }


                }
            });
        }
    };







    private Emitter.Listener onNewMessageDangKy = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String noidung;

                    try {
                        noidung = data.getString("noidung");
                        if(noidung== "true"){
                            Toast.makeText(MainActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        return;
                    }


                }
            });
        }
    };


    private Emitter.Listener onNewImage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {

                        byte[] mangHinhAnh = (byte[]) data.get("images");
                        NoiDungChat username=new NoiDungChat("",mangHinhAnh,new byte[0]);
                        noiDungChatArrayList.add(username);
                        adapter2.notifyDataSetChanged();
                        //Toast.makeText(MainActivity.this, "Length= "+mangHinhAnh.length, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };



    private Emitter.Listener onNewSound = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {

                        byte[] mangAmThanh = (byte[]) data.get("sounds");
                        NoiDungChat username=new NoiDungChat("",new byte[0],mangAmThanh);
                        noiDungChatArrayList.add(username);
                        adapter2.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Length= "+mangAmThanh.length, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };


    private void playMp3FromByte(byte[] mp3SoundByteArray) {
        try {

            File tempMp3 = File.createTempFile("kurchina", "mp3", getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            MediaPlayer mediaPlayer = new MediaPlayer();

            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }
}
