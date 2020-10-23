package wasakey.w3apps.com.qrcode.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import wasakey.w3apps.com.qrcode.R;
import wasakey.w3apps.com.qrcode.databinding.ActivityHomeBinding;
import wasakey.w3apps.com.qrcode.helpers.util.PermissionUtil;
import wasakey.w3apps.com.qrcode.ui.generate.GenerateFragment;
import wasakey.w3apps.com.qrcode.ui.history.HistoryFragment;
import wasakey.w3apps.com.qrcode.ui.scan.ScanFragment;
import wasakey.w3apps.com.qrcode.ui.settings.SettingsActivity;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityHomeBinding mBinding;
    private Menu mToolbarMenu;

    public Menu getToolbarMenu() {
        return mToolbarMenu;
    }

    public void setToolbarMenu(Menu toolbarMenu) {
        mToolbarMenu = toolbarMenu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        getWindow().setBackgroundDrawable(null);

        setListeners();
        initializeToolbar();
        initializeBottomBar();
        checkInternetConnection();
        playAd();
    }

    private void checkInternetConnection() {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(ReactiveNetwork
                .observeNetworkConnectivity(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    if (connectivity.state() == NetworkInfo.State.CONNECTED) {
                        mBinding.adView.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.adView.setVisibility(View.GONE);
                    }

                }, throwable -> {
                    Toast.makeText(this, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }));
    }

    private void playAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.adView.loadAd(adRequest);
        mBinding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mBinding.adView.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
            }
        });
    }

    private void initializeToolbar() {
        setSupportActionBar(mBinding.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);
        setToolbarMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setListeners() {
        mBinding.textViewGenerate.setOnClickListener(this);
        mBinding.textViewScan.setOnClickListener(this);
        mBinding.textViewHistory.setOnClickListener(this);

        mBinding.imageViewGenerate.setOnClickListener(this);
        mBinding.imageViewScan.setOnClickListener(this);
        mBinding.imageViewHistory.setOnClickListener(this);

        mBinding.constraintLayoutGenerateContainer.setOnClickListener(this);
        mBinding.constraintLayoutScanContainer.setOnClickListener(this);
        mBinding.constraintLayoutHistoryContainer.setOnClickListener(this);
    }

    private void initializeBottomBar() {
        clickOnScan();
    }

    private void clickOnGenerate() {
        mBinding.textViewGenerate.setTextColor(
                ContextCompat.getColor(this, R.color.bottom_bar_selected));

        mBinding.textViewScan.setTextColor(
                ContextCompat.getColor(this, R.color.bottom_bar_normal));

        mBinding.textViewHistory.setTextColor(
                ContextCompat.getColor(this, R.color.bottom_bar_normal));

        mBinding.imageViewGenerate.setVisibility(View.INVISIBLE);
        mBinding.imageViewGenerateActive.setVisibility(View.VISIBLE);

        mBinding.imageViewScan.setVisibility(View.VISIBLE);
        mBinding.imageViewScanActive.setVisibility(View.INVISIBLE);

        mBinding.imageViewHistory.setVisibility(View.VISIBLE);
        mBinding.imageViewHistoryActive.setVisibility(View.INVISIBLE);

        setToolbarTitle(getString(R.string.toolbar_title_generate));
        showFragment(GenerateFragment.newInstance());
    }

    private void clickOnScan() {
        if (PermissionUtil.on().requestPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {


            mBinding.textViewGenerate.setTextColor(
                    ContextCompat.getColor(this, R.color.bottom_bar_normal));

            mBinding.textViewScan.setTextColor(
                    ContextCompat.getColor(this, R.color.bottom_bar_selected));

            mBinding.textViewHistory.setTextColor(
                    ContextCompat.getColor(this, R.color.bottom_bar_normal));

            mBinding.imageViewGenerate.setVisibility(View.VISIBLE);
            mBinding.imageViewGenerateActive.setVisibility(View.INVISIBLE);

            mBinding.imageViewScan.setVisibility(View.INVISIBLE);
            mBinding.imageViewScanActive.setVisibility(View.VISIBLE);

            mBinding.imageViewHistory.setVisibility(View.VISIBLE);
            mBinding.imageViewHistoryActive.setVisibility(View.INVISIBLE);

            setToolbarTitle(getString(R.string.toolbar_title_scan));

        /*IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setBeepEnabled(SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.PLAY_SOUND));
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan a barcode");
        integrator.initiateScan();

        *//*
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
         */
            showFragment(ScanFragment.newInstance());
        }
    }

    private void clickOnHistory() {
        mBinding.textViewGenerate.setTextColor(
                ContextCompat.getColor(this, R.color.bottom_bar_normal));

        mBinding.textViewScan.setTextColor(
                ContextCompat.getColor(this, R.color.bottom_bar_normal));

        mBinding.textViewHistory.setTextColor(
                ContextCompat.getColor(this, R.color.bottom_bar_selected));

        mBinding.imageViewGenerate.setVisibility(View.VISIBLE);
        mBinding.imageViewGenerateActive.setVisibility(View.INVISIBLE);

        mBinding.imageViewScan.setVisibility(View.VISIBLE);
        mBinding.imageViewScanActive.setVisibility(View.INVISIBLE);

        mBinding.imageViewHistory.setVisibility(View.INVISIBLE);
        mBinding.imageViewHistoryActive.setVisibility(View.VISIBLE);

        setToolbarTitle(getString(R.string.toolbar_title_history));
        showFragment(HistoryFragment.newInstance());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_generate:
            case R.id.text_view_generate:
            case R.id.constraint_layout_generate_container:
                clickOnGenerate();
                break;

            case R.id.image_view_scan:
            case R.id.text_view_scan:
            case R.id.constraint_layout_scan_container:
                clickOnScan();
                break;

            case R.id.image_view_history:
            case R.id.text_view_history:
            case R.id.constraint_layout_history_container:
                clickOnHistory();
                break;
        }
    }

    private void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.coordinator_layout_fragment_container, fragment,
                fragment.getClass().getSimpleName());
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionUtil.REQUEST_CODE_PERMISSION_DEFAULT) {
            boolean isAllowed = true;

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isAllowed = false;
                }
            }

            if (isAllowed) {
                clickOnScan();
            }
        }
    }

  /*  public void hideAdMob()
    {
        if (mBinding.adView.isShown())
            mBinding.adView.setVisibility(View.GONE);
    }

    public void showAdmob()
    {
        if (!mBinding.adView.isShown())
            mBinding.adView.setVisibility(View.VISIBLE);
    }*/
}
