package com.example.proxilock;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ProximitySensor proximitySensor;
    private boolean isActivated = false;
    private TextView lockCountView;
    private TextView unlockCountView;
    private TextView sensitivityView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        proximitySensor = new ProximitySensor(this);

        lockCountView = findViewById(R.id.tv_lock_count);
        unlockCountView = findViewById(R.id.tv_unlock_count);
        sensitivityView = findViewById(R.id.tv_sensitivity);

        Button toggleButton = findViewById(R.id.btn_toggle);
        toggleButton.setOnClickListener(v -> {
            if (isActivated) {
                proximitySensor.stop();
                toggleButton.setText("Activer ProxiLock");
            } else {
                proximitySensor.start();
                toggleButton.setText("Désactiver ProxiLock");
            }
            isActivated = !isActivated;

            // Mettre à jour les vues
            updateHistory();
        });

        // Lancer la mise à jour en temps réel depuis le capteur
        proximitySensor.setEventListener(this::updateHistory);
    }

    private void updateHistory() {
        lockCountView.setText("Verrouillages : " + proximitySensor.getLockCount());
        unlockCountView.setText("Déverrouillages : " + proximitySensor.getUnlockCount());
        sensitivityView.setText("Sensibilité actuelle : " + ProximitySensor.SENSITIVITY_DELAY_MS + "ms");
    }
}
