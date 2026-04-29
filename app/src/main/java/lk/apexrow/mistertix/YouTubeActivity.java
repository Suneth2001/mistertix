package lk.apexrow.mistertix;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class YouTubeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_you_tube);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contactUsDesign), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            WebView webView = findViewById(R.id.youtube_webView);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);

            TextView back = findViewById(R.id.back_trailer);
            back.setOnClickListener(v1 -> finish());

            // Prevent YouTube app from opening
            webView.setWebViewClient(new WebViewClient());

            // Get YouTube Link and Load it in WebView
            String youtubeLink = getIntent().getStringExtra("youtubeLink");
            if (youtubeLink != null && !youtubeLink.isEmpty()) {
                String videoId = extractYouTubeVideoId(youtubeLink);
                if (videoId != null) {
                    String embedUrl = "https://www.youtube.com/embed/" + videoId;
                    webView.loadUrl(embedUrl);
                }
            }

            return insets;
        });
    }

    private String extractYouTubeVideoId(String url) {
        String videoId = null;
        if (url.contains("youtube.com/watch?v=")) {
            videoId = url.split("v=")[1];
            int ampersandIndex = videoId.indexOf("&");
            if (ampersandIndex != -1) {
                videoId = videoId.substring(0, ampersandIndex);
            }
        } else if (url.contains("youtu.be/")) {
            videoId = url.split("youtu.be/")[1];
            int questionMarkIndex = videoId.indexOf("?");
            if (questionMarkIndex != -1) {
                videoId = videoId.substring(0, questionMarkIndex);
            }
        }
        return videoId;
    }
}
