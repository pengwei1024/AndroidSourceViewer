package com.apkfuns.androidsourceviewer.widget;

import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.util.Timer;
import java.util.TimerTask;

public class GlobalSearchDialog extends JDialog implements InputMethodListener {
    private JPanel contentPane;
    private JTextField searchBar;
    private JList<String> listView;
    private final DefaultListModel<String> dataSet = new DefaultListModel<String>();

    public GlobalSearchDialog() {
        System.out.println("GlobalSearchDialog");
        setContentPane(contentPane);
        setModal(true);
        searchBar.addInputMethodListener(this);
        setTitle("Android Source Search");
        setSize(600, 400);
        this.setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        String[] str = {"aa","bb","cc","dd","ee","ff","gg"};
        final DefaultListModel<String> d = new DefaultListModel<String>();
        d.addElement("ababababababbababababababab");
        d.addElement("bb");
//        searchBar.addInputMethodListener(new InputMethodListener() {
//            @Override
//            public void inputMethodTextChanged(InputMethodEvent event) {
//                System.out.println(searchBar.getText());
//            }
//
//            @Override
//            public void caretPositionChanged(InputMethodEvent event) {
//
//            }
//        });
        listView.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!listView.getValueIsAdjusting()){   //设置只有释放鼠标时才触发
                    System.out.println(listView.getSelectedValue());
                }
            }
        });
        listView.setModel(d);
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                d.addElement("123456");
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        listView.setModel(d);
                    }
                });
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        searchBar.removeInputMethodListener(this);
        System.out.println("dispose");
    }

    private Timer searchTimer;

    @Override
    public void inputMethodTextChanged(InputMethodEvent event) {
        System.out.println("inputMethodTextChanged!!" + searchBar.getText());
        if (searchTimer != null) {
            searchTimer.cancel();
        }
        searchTimer = new Timer();
        searchTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(searchBar.getText());
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dataSet.addElement(searchBar.getText());
                        listView.setModel(dataSet);
                    }
                });
            }
        }, 200);
    }

    @Override
    public void caretPositionChanged(InputMethodEvent event) {
        System.out.println("caretPositionChanged => " + searchBar.getText());
    }
}
