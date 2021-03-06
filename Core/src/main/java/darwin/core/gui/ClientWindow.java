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

import com.jogamp.newt.Window;
import com.jogamp.newt.event.*;

/**
 * Kein Fenster im eigentlichen Sinne, sondern mehr eine Ansammlung der Objekte,
 * aus denen das Fenster aufgebaut ist. Dient zur Initialisierung des Renderers
 * und dem Zugriff auf dessen Komponenten.
 * <p/>
 * @author Daniel Heinrich
 * <p/>
 */
public class ClientWindow implements ShutdownListener
{
    private final Client client;
    private final int width, height;
    private final boolean fullscreen;

    public ClientWindow(int xSize, int ySize, boolean fullscreen, Client c)
    {
        width = xSize;
        height = ySize;
        this.fullscreen = fullscreen;
        client = c;
        client.addShutdownListener(this);
        //TODO logging
//        client.addLogAppender(new AppenderSkeleton()
//        {
//            @Override
//            protected void append(LoggingEvent event)
//            {
//                if (event.getLevel() == Level.FATAL) {
//                    ThrowableInformation ti = event.getThrowableInformation();
//                    if (ti != null) {
//                        ti.getThrowable().printStackTrace();
//                    }
//                    doShutDown();
//                }
//            }
//
//            @Override
//            public void close()
//            {
//            }
//
//            @Override
//            public boolean requiresLayout()
//            {
//                return false;
//            }
//        });
    }

    public Window startUp() throws InstantiationException
    {
        client.iniClient();
        Window win = client.getWindow();
        win.setSize(width, height);
        win.setVisible(true);

        win.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowDestroyed(WindowEvent arg0)
            {
                doShutDown();
            }
        });
        return win;
    }

    @Override
    public void doShutDown()
    {
        /*
         * Zur Sicherheit in eigenem Thread ausführen, da das Swing-System
         * blockieren könnte.
         */
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                client.shutdown();
                System.exit(0);
            }
        }).start();
    }
}
