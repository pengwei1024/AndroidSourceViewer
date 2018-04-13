package com.apkfuns.androidsourceviewer.widget;

import com.apkfuns.androidsourceviewer.util.Utils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;

/**
 * Created by pengwei on 2017/11/7.
 */
public class PopListView {

    private AnActionEvent anActionEvent;
    private ListPopup listPopup;
    private JBPopupFactory.ActionSelectionAid aid;

    public PopListView(AnActionEvent anActionEvent) {
        this.anActionEvent = anActionEvent;
        aid = JBPopupFactory.ActionSelectionAid.NUMBERING;
    }

    /**
     * create ListPop
     *
     * @param title
     * @param data
     * @param listener
     */
    public void createList(String title, String[] data, OnItemClickListener listener) {
        DefaultActionGroup group = new DefaultActionGroup();
        if (data != null && data.length > 0) {
            for (int i = 0; i < data.length; i++) {
                if (!Utils.isEmpty(data[i])) {
                    if (data[i].contains("-")) {
                        group.add(new ListItemAction(i, data[i], listener));
                    } else {
                        group.addSeparator(data[i]);
                    }
                }
            }
        }
        listPopup = JBPopupFactory.getInstance().createActionGroupPopup(title, group,
                anActionEvent.getDataContext(), aid, true, null, -1, null, "unknown");
        show();
    }

    public void dispose() {
        if (listPopup != null) {
            listPopup.dispose();
        }
    }

    private void show() {
        if (listPopup == null || anActionEvent == null) {
            return;
        }
        Project project = anActionEvent.getProject();
        if (project != null) {
            listPopup.showCenteredInCurrentWindow(project);
        } else {
            listPopup.showInBestPositionFor(anActionEvent.getDataContext());
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(int position, String value);
    }

    private static class ListItemAction extends AnAction {

        private int index;
        private String value;
        private OnItemClickListener clickListener;

        public ListItemAction(int index, String value, OnItemClickListener clickListener) {
            super(value);
            this.index = index;
            this.value = value;
            this.clickListener = clickListener;
        }

        @Override
        public void actionPerformed(AnActionEvent anActionEvent) {
            if (clickListener != null) {
                clickListener.OnItemClick(this.index, this.value);
            }
        }
    }
}
