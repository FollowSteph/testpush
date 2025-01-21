package com.example.views.testpush;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Random;
import java.util.stream.IntStream;

@PageTitle("Test Push")
@Route("")
public class TestPushView extends VerticalLayout
{
    private Button startThreadButton;
    private Button resetButton;
    private NativeLabel statusLabel;
    private NativeLabel counterLabel;
    private ProgressBar progressBar;

    private TextField textField;

    private Random RANDOM = new Random();

    public TestPushView()
    {
        statusLabel = new NativeLabel("Status");
        counterLabel = new NativeLabel("0");

        textField = new TextField("Test Value");
        progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        startThreadButton = new Button("Start Thread", click -> startButtonClicked());
        resetButton = new Button("Reset", click -> reset());

        add(startThreadButton, resetButton, textField, statusLabel, counterLabel, progressBar);
    }

    private void startButtonClicked()
    {
        reset();
        startThread();
    }

    private void startThread()
    {
        UI ui = UI.getCurrent();

        statusLabel.setText("Status: Starting");

        progressBar.setVisible(true);

        // ***** IMPORTANT -> None of the components write (push) to any component that is read.
        new Thread(() ->
        {
            System.out.println("Starting thread");

            // Wrapping it all in a try catch in case there is ever an exception so that we can see it.
            try
            {
                // Some computations
                processingPause();

                if(ui == null)
                    return;

                // Loop through some updates to see some movement.
                System.out.println("Push 1 - started - Multiple pushes in rapid succession to also show work done.");
                IntStream.range(0, 20).forEach(x ->
                {
                    // Some computations
                    randomPause();
                    // Push an update
                    System.out.println("UI Status : " + (ui == null ? "null" : ui.toString()));
                    ui.access(() -> {
                        System.out.println("Push 1. " + x + " - counter - " + x);
                        counterLabel.setText(String.valueOf(x));
                    });
                });
                System.out.println("--> Push 1 - completed");

                // Removing the progressbar.
                System.out.println("UI Status : " + (ui == null ? "null" : ui.toString()));
                ui.access(() -> {
                    System.out.println("--> Push 2 - started");
                    statusLabel.setText("Status: Done");
                    progressBar.setVisible(false);
                    System.out.println("--> Push 2 - completed");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("--> Thread completed");
        }).start();
    }

    private void processingPause() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void randomPause() {
        try {
            Thread.sleep(RANDOM.nextInt(10,1000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void reset() {
        progressBar.setVisible(false);
        statusLabel.setText("");
        counterLabel.setText("0");
    }
}
