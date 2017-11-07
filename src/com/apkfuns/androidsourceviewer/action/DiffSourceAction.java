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

//    @Override
//    public void actionPerformed(AnActionEvent e) {
//        JBPopupFactory.ActionSelectionAid aid = JBPopupFactory.ActionSelectionAid.NUMBERING;
//        DefaultActionGroup group = new DefaultActionGroup();
//        group.add(new TestAction("1"));
//        group.add(new TestAction("2"));
//        group.add(new TestAction("3"));
//        group.add(new TestAction("4"));
//        group.add(new TestAction("5"));
//        group.add(new TestAction("6"));
//        group.add(new TestAction("7"));
//        group.add(new TestAction("8"));
//        group.add(new TestAction("9"));
//        group.add(new TestAction("10"));
//        ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup("title", group,
//                e.getDataContext(), aid, true, null, -1, null, "unknown");
//        if (popup instanceof ListPopupImpl) {
//            System.out.println("popup => ok");
////            ((ListPopupImpl) popup).getList().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//            ((ListPopupImpl) popup).getList().setSelectionModel(new DefaultListSelectionModel() {
//                @Override
//                public void setSelectionInterval(int index0, int index1) {
//                    if(super.isSelectedIndex(index0)) {
//                        super.removeSelectionInterval(index0, index1);
//                    }
//                    else {
//                        super.addSelectionInterval(index0, index1);
//                    }
//                }
//            });
//        }
//        popup.addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                System.out.println(e.getFirstIndex() + "###" + e.getLastIndex());
//            }
//        });
//        System.out.println("popup =>" + popup.getClass());
//        this.showPopup(e, popup);
//        System.out.println("selectMode=" + ((ListPopupImpl) popup).getList().getSelectionMode());
//    }
//
//    protected void showPopup(AnActionEvent e, ListPopup popup) {
//        Project project = e.getProject();
//        if(project != null) {
//            popup.showCenteredInCurrentWindow(project);
//        } else {
//            popup.showInBestPositionFor(e.getDataContext());
//        }
//    }
//
//    public static class TestAction extends AnAction {
//
//        private String text;
//
//        public TestAction(@Nullable String text) {
//            super(text);
//            this.text = text;
//        }
//
//        @Override
//        public void actionPerformed(AnActionEvent anActionEvent) {
//            System.out.println("click!!!" + this.text);
//        }
//    }
}
