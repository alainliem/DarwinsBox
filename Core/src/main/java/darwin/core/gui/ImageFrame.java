/*
 * Copyright (C) 2012 daniel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.core.gui;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 *
 * @author daniel
 */
public class ImageFrame extends JFrame {

    private final Container main;

    public ImageFrame(int width, int height) throws HeadlessException {
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.X_AXIS));
        ScrollPane scroll = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        getContentPane().add(scroll);
        scroll.add(main);

        setPreferredSize(new Dimension(width, height));
        pack();
        setVisible(true);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Container addImage(Image image) {
        Container img = new JLabel(new ImageIcon(image));
        main.add(img);
        pack();
        return img;
    }

    public Container getMain() {
        return main;
    }
}
