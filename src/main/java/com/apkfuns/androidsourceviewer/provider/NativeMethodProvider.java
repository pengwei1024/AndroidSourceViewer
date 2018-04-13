package com.apkfuns.androidsourceviewer.provider;

import com.apkfuns.androidsourceviewer.action.FindNativeMethodAction;
import com.apkfuns.androidsourceviewer.icons.PluginIcons;
import com.apkfuns.androidsourceviewer.util.Utils;
import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;

/**
 * 支持Native方法跳转
 */
public class NativeMethodProvider implements LineMarkerProvider, GutterIconNavigationHandler<PsiElement> {

    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement psiElement) {
        if (!(psiElement instanceof PsiMethod)) {
            return null;
        }
        // 只展示系统 native 方法
        PsiMethod method = (PsiMethod) psiElement;
        for (JvmModifier modifier: method.getModifiers()) {
            if (modifier == JvmModifier.NATIVE && method.getContainingClass() != null) {
                String packageName = method.getContainingClass().getQualifiedName();
                if (packageName != null && Utils.getVersionList(packageName) != null) {
                    return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), PluginIcons.NATIVE,
                            Pass.UPDATE_ALL, null, this,
                            GutterIconRenderer.Alignment.LEFT);
                }
            }
        }
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> list, @NotNull Collection<LineMarkerInfo> collection) {

    }

    @Override
    public void navigate(MouseEvent mouseEvent, final PsiElement psiElement) {
        PsiMethod method = (PsiMethod) psiElement;
        AnActionEvent anActionEvent = AnActionEvent.createFromInputEvent(mouseEvent, "", new Presentation(), new DataContext() {
                    @Nullable
                    @Override
                    public Object getData(String key) {
                        if (CommonDataKeys.PROJECT.getName().equals(key)) {
                            return psiElement.getProject();
                        } else if (CommonDataKeys.PSI_ELEMENT.getName().equals(key)) {
                            return psiElement;
                        }
                        return null;
                    }
                });
        new FindNativeMethodAction(method).actionPerformed(anActionEvent);
    }
}
