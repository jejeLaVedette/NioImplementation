/* ***** BEGIN LICENSE BLOCK *****
 *    Copyright 2003 Michel Jacobson jacobson@idf.ext.jussieu.fr
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * ***** END LICENSE BLOCK ***** */
package multiCast.client.gui.console;
/*-----------------------------------------------------------------------*/

import java.awt.*;
import java.io.PrintStream;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Contenu de la console de redirection.
 */
public class ConsolePanel extends javax.swing.JPanel implements javax.swing.event.DocumentListener, Console {

    private final JTextPane myTextPane;
    private final ConsoleWriter myWriter;
    //private final Console console;
    private final PrintStream stdOut = System.out;
    private final PrintStream stdErr = System.err;
    private final boolean stderr, stdout;

    /**
     * Construction du composant graphique JPanel contenant la console.
     */
    public ConsolePanel() {
        super();
        //this.console = console;
        myTextPane = new JTextPane();
        Document doc = myTextPane.getDocument();
        myWriter = new ConsoleWriter(doc);
        JScrollPane jsp = new JScrollPane(myTextPane);
        setLayout(new BorderLayout());
        add("Center", jsp);
        stderr = true;
        stdout = true;
        myTextPane.getDocument().addDocumentListener(this);
        this.activate(true);
    }
    
    public ConsoleWriter getWriter() {
        return myWriter;
    }

    public void clear() {
        Document doc = myTextPane.getDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) {
            System.err.println(e.getMessage());
        }
    }

    //pour DocumentListener
    @Override
    public void changedUpdate(DocumentEvent de) {
    }

    @Override
    public void removeUpdate(DocumentEvent de) {
    }

    @Override
    public void insertUpdate(final DocumentEvent de) {
        //this.setVisible();
        final Document doc = de.getDocument();
        final int i1, i2;
        final int l;
        i1 = de.getOffset();
        i2 = de.getLength();
        l = doc.getLength();
        if (l == i1 + i2) {
            Element e1 = doc.getDefaultRootElement();
            Element e2 = e1.getElement(e1.getElementIndex(i1));
            while (!e2.isLeaf()) {
                e2 = e2.getElement(e2.getElementIndex(i1));
            }
            final Element e3 = e2;
            AttributeSet as = e3.getAttributes();
            if (as.isDefined(myWriter)) {
                if (myTextPane.getCaretPosition() != l) {
                    myTextPane.setCaretPosition(l);
                }
            }
        }
    }

    /**
     * Redirection.
     *
     * @param b true: redirection vers le composant graphique, false:
     * restitution de l'ancienne affectation.
     */
    public void activate(boolean b) {
        if (b) {
            try {
                if (stdout) {
                    System.setOut(new PrintStream(this.getWriter().asStream(), true, "utf8"));
                }
                if (stderr) {
                    System.setErr(new PrintStream(this.getWriter().asStream(), true, "utf8"));
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            if (stdout) {
                System.setOut(stdOut);
            }
            if (stderr) {
                System.setErr(stdErr);
            }
        }
    }

    @Override
    public void removeNotify() {
        if (stdout) {
            System.setOut(stdOut);
        }
        if (stderr) {
            System.setErr(stdErr);
        }
        super.removeNotify();
    }

    @Override
    public void setVisible() {
        if (!isShowing()) {
            super.show();
        }
    }
}
