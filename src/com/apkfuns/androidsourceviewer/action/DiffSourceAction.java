package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.Constant;
import com.apkfuns.androidsourceviewer.widget.PopListView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by pengwei on 2017/11/6.
 */
public class DiffSourceAction extends AnAction implements PopListView.OnItemClickListener{

    private PopListView popListView;
    private String firstValue;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        firstValue = null;
        popListView = new PopListView(anActionEvent);
        popListView.createList("选择一个版本", Constant.ANDROID_VERSION_LIST, this);
    }

    @Override
    public void OnItemClick(int position, String value) {
        if (firstValue == null) {
            System.out.println("first =>" + value);
            firstValue = value;
            popListView.createList("选择另一个版本", Constant.ANDROID_VERSION_LIST, this);
        } else {
            System.out.println("second =>" + value);
        }
    }
}
