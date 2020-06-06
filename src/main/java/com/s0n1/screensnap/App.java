package com.s0n1.screensnap;

import com.google.zxing.Result;
import com.s0n1.screensnap.tools.GlobalHotKey;
import com.s0n1.screensnap.tools.Settings;
import com.s0n1.screensnap.ui.*;
import com.s0n1.screensnap.util.AppUtil;
import com.s0n1.screensnap.util.DeviceUtil;
import com.s0n1.screensnap.util.QrCodeUtil;
import com.s0n1.screensnap.widget.Application;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Enumeration;

import static com.s0n1.screensnap.ui.UiRes.*;
import static com.s0n1.screensnap.util.DeviceUtil.SCREEN_HEIGHT;
import static com.s0n1.screensnap.util.DeviceUtil.SCREEN_WIDTH;

/**
 * Main Entrance
 * Created by Edsuns@qq.com on 2020-05-25
 */
public class App extends Application {
    private HomeJFrame homeFrame;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                App app = new App();
                app.homeFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private App() {
        try {// 让程序只能单次运行
            FileLock lock = new FileOutputStream("./SingleRunLock").getChannel().tryLock();
            if (lock == null) {
                System.out.println("App is already running...");
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("App started.");
        System.out.println("isWindows: " + DeviceUtil.isWindows);
        System.out.println("isOldVersionJava: " + DeviceUtil.isOldVersionJava);
        System.out.println("DPI Scale: " + DPI_SCALE_RATE);

        // 开始检查DPI缩放是否开启
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        boolean hasDPIScale = !DeviceUtil.isOldVersionJava && screenSize.height != DeviceUtil.displayMode.getHeight();
        if (hasDPIScale) {
            // DPI缩放会导致截图模糊，要求关闭DPI缩放，自己适配高DPI
            throw new Error("Need disable DPI Scale by VM option: -Dsun.java2d.uiScale=1");
        }
        init();
    }

    private ShotJFrame shotJFrame;
    private HotkeyDialog hotkeyDialog;
    private CopyColorJFrame colorJFrame;

    private void init() {
        // 设置系统默认样式
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        if (!DeviceUtil.isOldVersionJava) {
            // 缩放字体大小，要在设置样式后
            Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof FontUIResource) {
                    UIManager.put(key, new FontUIResource((
                            (FontUIResource) value).getFamily(),
                            ((FontUIResource) value).getStyle(),
                            (int) (((FontUIResource) value).getSize() * DPI_SCALE_RATE)
                    ));
                }
            }
        }

        // 初始化取色界面
        shotJFrame = new ShotJFrame();
        shotJFrame.setPickColorListener(new ShotJFrame.PickColorListener() {
            @Override
            public void onColorPicked(Color color) {
                colorJFrame.showCopy(color);
            }

            @Override
            public void onRightCapture(BufferedImage image) {
                System.out.println("onRightCapture");
                Result result = QrCodeUtil.parseQrCode(image);
                if (result != null) {
                    String format = result.getBarcodeFormat().toString();
                    AppUtil.copy(result.getText());
                    Toast.getInstance().show(format + " has been copied", Toast.DELAY_DEFAULT);
                }
            }

            @Override
            public void onLeftCapture(BufferedImage image) {
                System.out.println("onLeftCapture");
            }
        });

        // 设置热键的对话框
        hotkeyDialog = new HotkeyDialog(homeFrame);
        hotkeyDialog.setTitle(CHANGE_HOTKEY);
        hotkeyDialog.setIconImage(APP_ICON);

        Settings.load();
        // 设置快捷键回调
        GlobalHotKey.getInstance().setHotKeyListener(this::showShot);

        colorJFrame = new CopyColorJFrame();
        colorJFrame.setIconImage(APP_ICON);
        colorJFrame.setTitle(COLOR_PICKER);
        colorJFrame.setPickAnotherCallback(this::showShot);

        // 初始化主界面
        homeFrame = new HomeJFrame(hotkeyDialog);
        homeFrame.setTitle(APP_NAME);
        homeFrame.setIconImage(APP_ICON);
        homeFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onAppClose(Settings.isRunInBg());
            }
        });
        // 在屏幕中间显示
        homeFrame.setBounds((SCREEN_WIDTH - WINDOW_WIDTH) / 2,
                (SCREEN_HEIGHT - WINDOW_HEIGHT) / 2, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private void showShot() {
        hotkeyDialog.dispose();
        colorJFrame.setVisible(false);
        Toast.getInstance().setVisible(false);
        shotJFrame.startShot();
    }

    @Override
    public void onAppClose(boolean runInBg) {
        if (runInBg) {
            homeFrame.enableRunInBg();
            homeFrame.setVisible(false);
        } else {
            GlobalHotKey.getInstance().stopHotKey();
            homeFrame.disableTrayIcon();
            System.out.println("App closed.");
            System.exit(0);
        }
    }
}
