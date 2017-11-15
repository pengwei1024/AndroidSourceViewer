package com.apkfuns.androidsourceviewer.action;

import com.apkfuns.androidsourceviewer.util.Log;
import com.apkfuns.androidsourceviewer.util.NotificationUtils;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.net.URLEncoder;

/**
 * 打开 Android 官方文档
 * https://developer.android.google.cn/reference/android/view/
 * View.OnClickListener.html#onClick(android.view.View)
 * https://developer.android.google.cn/reference/android/app/
 * Activity.html#onRestoreInstanceState(android.os.Bundle)
 */
public class AndroidDeveloperAction extends BaseSourceAction {
    final String baseUrl = "https://developer.android.google.cn/reference/";

    @Override
    protected void selectActionPerformed(AnActionEvent event, PsiElement element, String packageName) {
        if (!Utils.isAndroidClass(packageName)) {
            NotificationUtils.infoNotification("Must be Android class or Method");
            return;
        }
        String linkUrl = baseUrl;
        if (element instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) element;
            PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
            StringBuilder paramsBuilder = new StringBuilder("#" + psiMethod.getName());
            paramsBuilder.append("(");
            for (int i = 0; i < parameters.length; i++) {
                PsiParameter parameter = parameters[i];
                paramsBuilder.append(parameter.getType().getCanonicalText());
                if (i < parameters.length - 1) {
                    paramsBuilder.append(",%20");
                }
            }
            paramsBuilder.append(")");
            linkUrl += getRealPackage(psiMethod.getContainingClass()) + paramsBuilder.toString();
        } else if (element instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) element;
            linkUrl += getRealPackage(psiClass);
        }
        Log.debug("linkUrl= " + linkUrl);
        Utils.openUrl(linkUrl);
    }

    @Override
    protected boolean shouldSelectVersion() {
        return false;
    }

    /**
     * 替换成 Developer 需要的包名
     * 如 android.view.View.OnClickListener 替换成 android/view/View.OnClickListener.html
     * @param psiClass
     * @return
     */
    private String getRealPackage(PsiClass psiClass) {
        String topPackage = null;
        if (psiClass.getContainingClass() != null) {
            topPackage = psiClass.getContainingClass().getQualifiedName();
        }
        String classPackage = psiClass.getQualifiedName();
        if (Utils.isEmpty(topPackage)) {
            return classPackage.replaceAll("\\.", "/") + ".html";
        }
        classPackage = classPackage.replaceFirst(topPackage, topPackage.replaceAll("\\.", "/"));
        return classPackage + ".html";
    }
}
