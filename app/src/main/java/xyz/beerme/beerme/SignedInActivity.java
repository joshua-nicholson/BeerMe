package xyz.beerme.beerme;

import android.content.Context;
import android.content.Intent;

import com.firebase.ui.auth.IdpResponse;

/**
 * Created by Josh on 2/21/2017.
 */
public class SignedInActivity {

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = IdpResponse.getIntent(idpResponse);
        in.setClass(context, SignedInActivity.class);
        return in;
    }
}
