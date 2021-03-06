package br.com.targettrust.exercicio09;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView imv = (ImageView) findViewById(R.id.imageView);
        ImagesCache cache = ImagesCache.getInstance();//Singleton instance handled in ImagesCache class.
        cache.initializeCache();

        String img = "http://3.bp.blogspot.com/-xiLdnOAPAA4/VigmxC5IyKI/AAAAAAAACP4/g13xdPN5BYM/s300/BABBQ_NewLogo%2B%25281%2529.png";

        Bitmap bm = cache.getImageFromWarehouse(img);

        if(bm != null)
        {
            imv.setImageBitmap(bm);
        }
        else
        {
            imv.setImageBitmap(null);

            DownloadImageTask imgTask = new DownloadImageTask(cache, imv, 300, 300);//Since you are using it from `Activity` call second Constructor.

            imgTask.execute(img);
        }
        /*
        Usando dentro de um Adapter

        ImageView imv = (ImageView) rowView.findViewById(R.id.imageView);
        ImagesCache cache = ImagesCache.getInstance();
        cache.initializeCache();

        String img = "http://3.bp.blogspot.com/-xiLdnOAPAA4/VigmxC5IyKI/AAAAAAAACP4/g13xdPN5BYM/s300/BABBQ_NewLogo%2B%25281%2529.png";

        Bitmap bm = cache.getImageFromWarehouse(img);

        if(bm != null)
        {
        imv.setImageBitmap(bm);
        }
        else
        {
        imv.setImageBitmap(null);

        DownloadImageTask imgTask = new DownloadImageTask(this, 300, 300);//Since you are using it from `Adapter` call first Constructor.

        imgTask.execute(img);
        }
         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
