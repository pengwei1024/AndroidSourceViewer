package com.apkfuns.androidsourceviewer.widget;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.apkfuns.androidsourceviewer.entity.ListDoubleClickEvent;
import com.apkfuns.androidsourceviewer.util.HttpUtil;
import com.apkfuns.androidsourceviewer.util.Log;
import com.apkfuns.androidsourceviewer.util.ThreadPoolManager;
import com.apkfuns.androidsourceviewer.util.Utils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.ScheduledFuture;

public class GlobalSearchDialog extends JDialog implements ListSelectionListener,
        ListDoubleClickEvent.DoubleClickListener<String> {

    private static final String SEARCH_RESULT_EMPTY = "Result Empty!";
    private static final String[] SEARCH_EXT = {".java", ".c", ".cpp", ".cc", "*"};

    private JPanel contentPane;
    private JTextField searchBar;
    private JList<String> listView;
    private ScheduledFuture searchTask;
    private final DefaultListModel<String> dataSet = new DefaultListModel<String>();
    private final SearchFinishResult searchFinishResult;

    public GlobalSearchDialog(SearchFinishResult result) {
        this(null, result);
    }

    /**
     * @param keyword searchBar
     * @param result 搜索结果回调
     */
    public GlobalSearchDialog(String keyword, SearchFinishResult result) {
        setContentPane(contentPane);
        setModal(true);
        setTitle("Android Source Search");
        setSize(800, 500);
        this.setLocationRelativeTo(null);
        listView.setModel(dataSet);
        listView.setFixedCellHeight(25);
        listView.addListSelectionListener(this);
        listView.addMouseListener(new ListDoubleClickEvent<String>(this));
        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                startSearch();
            }
        });
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.searchFinishResult = result;
        if (!Utils.isEmpty(keyword)) {
            searchBar.setText(keyword);
            startSearch();
        }
    }

    /**
     * 开始搜索
     */
    private synchronized void startSearch() {
        final String text = searchBar.getText();
        if (Utils.isEmpty(text)) {
            return;
        }
        if (searchTask != null) {
            searchTask.cancel(true);
        }
        searchTask = ThreadPoolManager.getInstance().addTaskDelay(new Runnable() {
            @Override
            public void run() {
                boolean firstReturn = true;
                Log.debug("startSearch, key=" + text  + ", thread=" + Thread.currentThread().getName());
                try {
                    String result = HttpUtil.syncGet("http://source.apkfuns.com/yahoo.php?s=" + text);
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject.containsKey("status") && jsonObject.getIntValue("status") == 0) {
                        String keyword = jsonObject.getString("keyword");
                        if (text.equals(keyword)) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            dataSet.clear();
                            if (!jsonArray.isEmpty()) {
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    dataSet.addElement(jsonArray.getJSONObject(i).getString("name"));
                                }
                            } else {
                                dataSet.addElement(SEARCH_RESULT_EMPTY);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (dataSet.isEmpty()) {
                        dataSet.addElement(SEARCH_RESULT_EMPTY);
                    }
                }
//                try {
//                    if (text.contains(".")) {
//                        ClassEntity classEntity = new ClassEntity(text, "7.1.2_r36");
//                        List<String> urls = SearchDownload.onlineSearch(classEntity, false);
//                        dataSet.clear();
//                        for (String url : urls) {
//                            if (!Utils.isEmpty(url)) {
//                                dataSet.addElement(url);
//                            }
//                        }
//                    } else {
//                        for (String ext: SEARCH_EXT) {
//                            ClassEntity classEntity = new ClassEntity(text + ext, "7.1.2_r36");
//                            List<String> urls = SearchDownload.onlineSearch(classEntity, false);
//                            if (firstReturn) {
//                                dataSet.clear();
//                                firstReturn = false;
//                            }
//                            for (String url : urls) {
//                                if (!Utils.isEmpty(url)) {
//                                    dataSet.addElement(url);
//                                }
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (dataSet.isEmpty()) {
//                        dataSet.addElement(SEARCH_RESULT_EMPTY);
//                    }
//                }
            }
        }, 100);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!listView.getValueIsAdjusting()){   // 设置只有释放鼠标时才触发
            // item click
        }
    }

    @Override
    public void onDoubleClick(JList<String> jList, int position, String selectedValue) {
        if (Utils.isEmpty(selectedValue)) {
            return;
        }
        if (SEARCH_RESULT_EMPTY.equals(selectedValue)) {
            return;
        }
        dispose();
        if (searchFinishResult != null && !Utils.isEmpty(selectedValue)) {
            selectedValue = "http://androidxref.com" + selectedValue.replace("/xref/", "/raw/");
            searchFinishResult.OnResult(selectedValue);
        }
    }

    public interface SearchFinishResult {
        void OnResult(String result);
    }
}
