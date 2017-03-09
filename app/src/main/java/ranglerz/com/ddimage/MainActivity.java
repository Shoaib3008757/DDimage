package ranglerz.com.ddimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    ProgressBar progressBar;
    private static final int IO_BUFFER_SIZE = 4 * 1024;
    ArrayList<HashMap<String, Object>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        progressBar = (ProgressBar)findViewById(R.id.prograssBar);

        new GetContacts().execute();

    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://donate-life.ranglerz.be/getallimages.php";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("result");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String name = c.getString("name");
                        String email = c.getString("url");

                        // tmp hash map for single contact
                        HashMap<String, Object> contact = new HashMap<>();

                        Bitmap imageBitmap = getBitmapFromURL(email);
                        Drawable drawable = new BitmapDrawable(getResources(), imageBitmap);



                        // adding each child node to HashMap key => value
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("bitmap", drawable);
                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.e("TAGE", "result of " + result);
            Log.e("TAG", "VVVVV " + contactList);
            String url = contactList.get(0).get("email").toString();
            String name = contactList.get(1).get("name").toString();


            Drawable image = (Drawable) contactList.get(2).get("bitmap");

            Log.e("TAG", "VVVVVVV " + url);
            Log.e("TAG", "VVVVVVV " + name);
            Log.e("TAG", "VVVVVVV " + image);


            lv.setAdapter(new ImageAdapter(MainActivity.this, contactList));
            progressBar.setVisibility(View.GONE);
        }
    }


    ////////////////////////////////

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String, Object>> MyArr = new ArrayList<>();

        public ImageAdapter(Context c, ArrayList<HashMap<String, Object>> list)
        {
            // TODO Auto-generated method stub
            context = c;
            MyArr = list;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return MyArr.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item, null);
            }

            // ColImage

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            imageView.getLayoutParams().height = 300;
            imageView.getLayoutParams().width = 300;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            try
            {

                ImageLoader.getInstance().init(UILConfig());

                ImageLoader.getInstance().displayImage(((String)MyArr.get(position).get("email")), imageView);

            } catch (Exception e) {
                // When Error
                imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            }

            // ColPosition
            TextView txtPosition = (TextView) convertView.findViewById(R.id.email);
            txtPosition.setPadding(10, 0, 0, 0);
            txtPosition.setText("ID : " + (String)MyArr.get(position).get("name"));

            // ColPicname
            TextView txtPicName = (TextView) convertView.findViewById(R.id.mobile);
            txtPicName.setPadding(50, 0, 0, 0);
            txtPicName.setText("Desc : " + (String)MyArr.get(position).get("email"));



            return convertView;

        }

    }//end of loadImageClass
    private static void copy(InputStream in, OutputStream out) throws IOException {

        byte[] b = new byte[IO_BUFFER_SIZE];

        int read;

        while ((read = in.read(b)) != -1) {

            out.write(b, 0, read);

        }

    }

/////////////////////////

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ImageLoaderConfiguration UILConfig(){
        //        /** To make the image fill the width and keep height ratio.**/
        //        <ImageView
        //        android:layout_width="fill_parent" //fill_width #1
        //        android:layout_height="wrap_content" //fill_width #2
        //        android:id="@+id/ivImage"
        //        android:src="@android:drawable/gallery_thumb"
        //        android:scaleType="fitCenter"   //fill_width #3
        //        android:adjustViewBounds="true" //fill_width #4
        //                />
        final DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)  //cache #1
                .cacheOnDisk(true) //cache #2
                .showImageOnLoading(android.R.drawable.stat_sys_download)
                .showImageForEmptyUri(android.R.drawable.ic_dialog_alert)
                .showImageOnFail(android.R.drawable.stat_notify_error)
                .considerExifParams(true) //cache #3
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED) //fill_width #5
                .build();

        ////cache #4
        //add <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/> to manifest
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        //end of cache 4
        return config;
    }
}