package com.apkfuns.androidsourceviewer.widget;

import com.apkfuns.androidsourceviewer.download.SearchDownload;
import com.apkfuns.androidsourceviewer.entity.ClassEntity;
import com.apkfuns.androidsourceviewer.util.TextUtils;
import com.apkfuns.androidsourceviewer.util.ThreadPoolManager;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;

public class GlobalSearchDialog extends JDialog implements ListSelectionListener {

    private static final String SEARCH_RESULT_EMPTY = "Result Empty!";
    private static final String[] SEARCH_EXT = {".java", ".c", ".cpp", "*"};

    private JPanel contentPane;
    private JTextField searchBar;
    private JList<String> listView;
    private ScheduledFuture searchTask;
    private final DefaultListModel<String> dataSet = new DefaultListModel<String>();
    private boolean hasResult = false;
    private CountDownLatch searchItemCountDown;

    public GlobalSearchDialog() {
        setContentPane(contentPane);
        setModal(true);
        setTitle("Android Source Search");
        setSize(600, 400);
        this.setLocationRelativeTo(null);
        dataSet.addElement("/frameworks/base/services/java/com/android/server/SystemServer.java");
        listView.setModel(dataSet);
        listView.setFixedCellHeight(25);
        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                startSearch();
            }
        });
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * 开始搜索
     */
    private void startSearch() {
        String text = searchBar.getText();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (searchTask != null) {
            searchTask.cancel(true);
        }
        searchTask = ThreadPoolManager.getInstance().addTaskDelay(new Runnable() {
            @Override
            public void run() {
                dataSet.clear();
                try {
                    if (text.contains(".")) {
                        ClassEntity classEntity = new ClassEntity(text, "7.1.2_r36");
                        List<String> urls = SearchDownload.onlineSearch(classEntity, false);
                        for (String url : urls) {
                            if (!TextUtils.isEmpty(url)) {
                                dataSet.addElement(url);
                            }
                        }
                    } else {
                        for (String ext: SEARCH_EXT) {
                            ClassEntity classEntity = new ClassEntity(text + ext, "7.1.2_r36");
                            List<String> urls = SearchDownload.onlineSearch(classEntity, false);
                            for (String url : urls) {
                                if (!TextUtils.isEmpty(url)) {
                                    dataSet.addElement(url);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (dataSet.isEmpty()) {
                    dataSet.addElement(SEARCH_RESULT_EMPTY);
                }
            }
        }, 500);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!listView.getValueIsAdjusting()){   // 设置只有释放鼠标时才触发
            System.out.println(listView.getSelectedValue());
        }
    }
}
