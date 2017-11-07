package com.apkfuns.androidsourceviewer.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;

/**
 * Created by pengwei on 2017/11/5.
 */
public class SourceViewerAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        PsiElement element = event.getData(LangDataKeys.PSI_ELEMENT);
        if (element == null) {
            return;
        }
        String packageName = null;
        if (element instanceof PsiClass) {
            PsiClass cls = (PsiClass) element;
            System.out.println("class => " + cls.getQualifiedName());
            packageName = cls.getQualifiedName();
        } else if (element instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            System.out.println("method => " + method.getName() + " # "
                    + method.getContainingClass().getQualifiedName());
            packageName = method.getContainingClass().getQualifiedName();
        } else {
            System.out.println("cls = " + element.getClass());
        }
        if (packageName != null) {
            new VersionSelectAction(packageName).actionPerformed(event);
        }
    }
}
