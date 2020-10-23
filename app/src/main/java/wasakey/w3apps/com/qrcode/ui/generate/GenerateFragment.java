package wasakey.w3apps.com.qrcode.ui.generate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import wasakey.w3apps.com.qrcode.R;
import wasakey.w3apps.com.qrcode.databinding.FragmentGenerateBinding;
import wasakey.w3apps.com.qrcode.helpers.constant.IntentKey;
import wasakey.w3apps.com.qrcode.helpers.model.Code;
import wasakey.w3apps.com.qrcode.ui.generatedcode.GeneratedCodeActivity;

public class GenerateFragment extends androidx.fragment.app.Fragment implements View.OnClickListener {

    private FragmentGenerateBinding mBinding;
    private Context mContext;
    private InterstitialAd mInterstitialAd;

    public GenerateFragment() {

    }

    public static GenerateFragment newInstance() {
        return new GenerateFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_generate, container, false);
        initializeAd();
        setListeners();
        initializeCodeTypesSpinner();


        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void initializeAd() {
        if (mContext == null) {
            return;
        }

        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_test_interstitial_ad_unit_id));
    }

    private void setListeners() {
        mBinding.spinnerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getSelectedView()).setTextColor(ContextCompat.getColor(mContext,
                        position == 0 ? R.color.text_hint : R.color.text_regular));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBinding.textViewGenerate.setOnClickListener(this);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                generateCode();
            }
        });
    }

    private void initializeCodeTypesSpinner() {
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(mContext,
                R.array.code_types, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.item_spinner);
        mBinding.spinnerTypes.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View view) {
        if (mContext == null) {
            return;
        }

        switch (view.getId()) {
            case R.id.text_view_generate:
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    generateCode();
                }
                break;

            default:
                break;
        }
    }

    private void generateCode() {
        Intent intent = new Intent(mContext, GeneratedCodeActivity.class);
        if (mBinding.editTextContent.getText() != null) {
            String content = mBinding.editTextContent.getText().toString().trim();
            int type = mBinding.spinnerTypes.getSelectedItemPosition();

            if (!TextUtils.isEmpty(content) && type != 0) {

                boolean isValid = true;

                switch (type) {
                    case Code.BAR_CODE:
                        if (content.length() > 80) {
                            Toast.makeText(mContext,
                                    getString(R.string.error_barcode_content_limit),
                                    Toast.LENGTH_SHORT).show();
                            isValid = false;
                        }
                        break;

                    case Code.QR_CODE:
                        if (content.length() > 1000) {
                            Toast.makeText(mContext,
                                    getString(R.string.error_qrcode_content_limit),
                                    Toast.LENGTH_SHORT).show();
                            isValid = false;
                        }
                        break;

                    default:
                        isValid = false;
                        break;
                }

                if (isValid) {
                    Code code = new Code(content, type);
                    intent.putExtra(IntentKey.MODEL, code);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(mContext,
                        getString(R.string.error_provide_proper_content_and_type),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
