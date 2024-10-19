package org.example.liner;

import lombok.Getter;

import javax.swing.*;

public class CoolTimeReducer extends JLabel {
    @Getter
    boolean on = false;
    ImageIcon baseIcon;
    ImageIcon checkMark;

    public void setOn(boolean on) {
        this.on = on;
        updateIcon();
    }

    public CoolTimeReducer(ImageIcon icon, ImageIcon checkMark, int size, int x, int y) {
        super(icon);
        this.baseIcon = icon;
        this.checkMark = checkMark;
        setLocation(x, y);
        setSize(size, size);
    }

    public void updateIcon() {
        if (on) {
            setIcon(checkMark);  // on == true면 checkMark로 아이콘 교체
        } else {
            setIcon(baseIcon);  // on == false면 기본 아이콘으로 교체
        }
        repaint();  // 변경 사항을 즉시 반영
    }
}
