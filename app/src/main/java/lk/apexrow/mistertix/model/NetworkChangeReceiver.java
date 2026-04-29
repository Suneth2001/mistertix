package lk.apexrow.mistertix.model;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean isConnected = isConnectedToInternet(context);
            if (isConnected) {
                Toast.makeText(context, "\uD83D\uDFE2 Online", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "\uD83D\uDD34 You're Offline", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}