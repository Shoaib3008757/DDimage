package ranglerz.com.ddimage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UploadOrDownload extends AppCompatActivity {

    Button bt_upload, bt_showAlll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_or_download);
        bt_upload = (Button)findViewById(R.id.bt_upload);
        bt_showAlll = (Button)findViewById(R.id.bt_show_all_image);

        showAllIamgeActivity();
        uploadImagesActvity();
    }
    public void showAllIamgeActivity(){
        bt_showAlll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showImageActvity = new Intent(UploadOrDownload.this, MainActivity.class);
                startActivity(showImageActvity);
            }
        });
    }

    public void uploadImagesActvity(){
        bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadImageActvity = new Intent(UploadOrDownload.this, UploadActivity.class);
                startActivity(uploadImageActvity);
            }
        });
    }
}
