    package com.example.imusic;

    import android.Manifest;
    import android.content.Intent;
    import android.os.Bundle;
    import android.os.Environment;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.ListView;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.appcompat.app.AppCompatActivity;


    import com.karumi.dexter.Dexter;
    import com.karumi.dexter.PermissionToken;
    import com.karumi.dexter.listener.PermissionDeniedResponse;
    import com.karumi.dexter.listener.PermissionGrantedResponse;
    import com.karumi.dexter.listener.PermissionRequest;
    import com.karumi.dexter.listener.single.PermissionListener;

    import java.io.File;
    import java.util.ArrayList;

    public class MainActivity extends AppCompatActivity {

        ListView listview;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main);
            listview = findViewById(R.id.listview);

            Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_MEDIA_AUDIO)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            Toast.makeText(MainActivity.this, "Runtime Permission Given", Toast.LENGTH_SHORT).show();
                            File musicFolder = Environment.getExternalStorageDirectory();
                            ArrayList<File> mySongs = fetchSongs(musicFolder);

                            String [] items = new String[mySongs.size()];

                            for (int i=0; i<mySongs.size(); i++){
                                items[i] = mySongs.get(i).getName().replace(".mp3","");
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this , android.R.layout.simple_list_item_1, items);
                            listview.setAdapter(adapter);
                            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(MainActivity.this, PlaySong.class);
                                    String currentSong = listview.getItemAtPosition(position).toString();


                                    intent.putExtra("songlist", mySongs);
                                    intent.putExtra("position", position);
                                    intent.putExtra("currentSong", currentSong);

                                    startActivity(intent);

                                }
                            });
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();

                        }
                    })
                    .check();
        }
        public ArrayList<File> fetchSongs(File file){

            ArrayList<File> arrayList = new ArrayList<>();
            File[] files = file.listFiles();

            if(files != null){

                for(File myFile : files){

                    if(!myFile.isHidden() && myFile.isDirectory()){
                        arrayList.addAll(fetchSongs(myFile));
                    }

                    else{
                        if(myFile.getName().endsWith(".mp3")){
                            arrayList.add(myFile);
                        }
                    }

                }

            }

            return arrayList;
        }
    }