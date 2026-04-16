package com.knightforge.view;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class ChessGameFrameTest {

    @Test
    void simpleTest() throws InterruptedException, InvocationTargetException {

        SwingUtilities.invokeAndWait(() -> {
            UIManager.put("OptionPane.okButtonText", "OK");
            UIManager.put("OptionPane.cancelButtonText", "Cancel");
            ChessGameFrame mainFrame = new ChessGameFrame(1000, 760);
            mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            mainFrame.setVisible(true);
        });

        // Keep the test alive long enough to see the GUI
        Thread.sleep(5000); // 5 seconds
    }
}
