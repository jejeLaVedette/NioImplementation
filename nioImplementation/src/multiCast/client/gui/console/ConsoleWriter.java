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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;

public class ConsoleWriter extends java.io.Writer {

    private final Document monDocument;
    private boolean ouvert = true;
    private int lOffsetAtteint;
    private final Vector buffer;

    public ConsoleWriter(Document doc) {
        monDocument = doc;
        lOffsetAtteint = monDocument.getLength();
        buffer = new Vector();
    }

    @Override
    public void write(char cbuf[], int off, int len) throws IOException {
        byte b[];
        b = new byte[cbuf.length];
        for (int i = off; i < off + len; i++) {
            b[i] = (byte) cbuf[i];
        }

        String s = new String(b, off, len, "utf8");
        buffer.addElement(s);
    }

    @Override
    public void flush() throws IOException {
        if (!ouvert) {
            throw new IOException("Ecrivain fermé");
        }
        try {
            Vector v;
            String ss[];
            final StringBuffer sb;

            v = (Vector) buffer.clone();
            buffer.removeAllElements();
            ss = new String[v.size()];
            v.copyInto(ss);
            sb = new StringBuffer();
            for (int i = 0; i < ss.length; i++) {
                sb.append(ss[i]);
            }
            monDocument.insertString(monDocument.getLength(), sb.toString(), null);
            lOffsetAtteint = monDocument.getLength();
        } catch (BadLocationException ble) {
            throw new IOException(ble.toString());
        } catch (Exception e) {
        }
    }

    @Override
    public void close() {
        ouvert = false;
    }

    public int offsetAtteint() {
        return lOffsetAtteint;
    }

    public OutputStream asStream() {
        return new Stream();
    }

    private class Stream extends java.io.OutputStream {

        char buf[] = new char[1];

        @Override
        public void write(int b) throws IOException {
            buf[0] = (char) b;
            ConsoleWriter.this.write(buf, 0, 1);
            flush();
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            char c[];
            c = new char[b.length];
            for (int i = off; i < off + len; i++) {
                c[i] = (char) b[i];
            }
            ConsoleWriter.this.write(c, off, len);
            flush();
        }

        @Override
        public void flush() throws IOException {
            ConsoleWriter.this.flush();
        }
    }
}
