package com.mifta.project.id.favoriteapp.activity;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.mifta.project.id.favoriteapp.R;
import com.mifta.project.id.favoriteapp.database.MoviesHelper;
import com.mifta.project.id.favoriteapp.model.MoviesItems;

import static android.provider.BaseColumns._ID;
import static com.mifta.project.id.favoriteapp.database.DatabaseContract.TableColumns.CONTENT_URI_MOVIE;
import static com.mifta.project.id.favoriteapp.database.DatabaseContract.TableColumns.COUNTRY;
import static com.mifta.project.id.favoriteapp.database.DatabaseContract.TableColumns.DATE;
import static com.mifta.project.id.favoriteapp.database.DatabaseContract.TableColumns.OVERVIEW;
import static com.mifta.project.id.favoriteapp.database.DatabaseContract.TableColumns.PHOTO;
import static com.mifta.project.id.favoriteapp.database.DatabaseContract.TableColumns.RATING;
import static com.mifta.project.id.favoriteapp.database.DatabaseContract.TableColumns.TITLE;

public class MoviesDetailActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE = "extra_movie";
    ProgressBar progressBar;
    MoviesItems movie = new MoviesItems();
    Uri uri;
    private MoviesHelper moviesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_detail);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvDate = findViewById(R.id.tv_date);
        TextView tvRating = findViewById(R.id.tv_rating);
        TextView tvCountry = findViewById(R.id.tv_country);
        TextView tvOverview = findViewById(R.id.tv_overview);
        ImageView img = findViewById(R.id.img_detail);
        TextView tvxMovie = findViewById(R.id.tvx_movieinfo);
        TextView tvxRating = findViewById(R.id.tvx_rating);
        TextView tvxCountry = findViewById(R.id.tvx_country);
        TextView tvxOverview = findViewById(R.id.tvx_overview);
        progressBar = findViewById(R.id.progressBar);

        movie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        moviesHelper = MoviesHelper.getInstance(this);

        showLoading(true);
        if (movie != null) {
            tvTitle.setText(movie.getTitle());
            tvDate.setText(movie.getDate());
            tvRating.setText(movie.getRating());
            tvCountry.setText(movie.getCountry());
            tvOverview.setText(movie.getOverview());
            Glide.with(this)
                    .load(movie.getPhoto())
                    .into(img);
            showLoading(false);
        }

        tvxMovie.setText(getResources().getString(R.string.movie_info));
        tvxRating.setText(getResources().getString(R.string.rating));
        tvxCountry.setText(getResources().getString(R.string.country));
        tvxOverview.setText(getResources().getString(R.string.overview));
    }

    private void showLoading(Boolean state) {
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        moviesHelper.open();
        if (moviesHelper.isExist(movie.getId())) {
            menu.findItem(R.id.menu_favorite).setIcon(R.drawable.ic_favorite_on);
        }
        moviesHelper.close();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menu_favorite) {
            moviesHelper.open();
            if (!moviesHelper.isExist(this.movie.getId())) {
                item.setIcon(R.drawable.ic_favorite_on);
                addToFavorite();
            } else {
                item.setIcon(R.drawable.ic_favorite_off);
                removeFromFavorite();
            }
            moviesHelper.close();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToFavorite() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, movie.getId());
        contentValues.put(TITLE, movie.getTitle());
        contentValues.put(OVERVIEW, movie.getOverview());
        contentValues.put(DATE, movie.getDate());
        contentValues.put(PHOTO, movie.getPhoto());
        contentValues.put(RATING, movie.getRating());
        contentValues.put(COUNTRY, movie.getCountry());

        getContentResolver().insert(CONTENT_URI_MOVIE, contentValues);
        Toast.makeText(this, getResources().getString(R.string.add_favorite), Toast.LENGTH_SHORT).show();
    }

    private void removeFromFavorite() {
        uri = Uri.parse(CONTENT_URI_MOVIE + "/" + movie.getId());
        getContentResolver().delete(uri, null, null);
        Toast.makeText(this, getResources().getString(R.string.remove_favorite), Toast.LENGTH_SHORT).show();
    }
}
