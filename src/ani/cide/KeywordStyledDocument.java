package ani.cide;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;

public class KeywordStyledDocument extends DefaultStyledDocument  {
    private static final long serialVersionUID = 1L;
    private Style _defaultStyle;
    private Style _cwStyle,_cwStyle2,_cwStyle3;

    public KeywordStyledDocument(Style defaultStyle, Style cwStyle,Style cw2,Style cw3) {
        _defaultStyle =  defaultStyle;
        _cwStyle = cwStyle;
        _cwStyle2=cw2;
        _cwStyle3=cw3;
    }

    public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
        super.insertString(offset, str, a);
        refreshDocument();
    }

    public void remove (int offs, int len) throws BadLocationException {
        super.remove(offs, len);
        refreshDocument();
    }

    private synchronized void refreshDocument() throws BadLocationException {
        String text = getText(0, getLength());
        final List<HiliteWord> list = processWords(text);
        final List<HiliteWord> list2 = processHeaders(text);
        final List<HiliteWord> list3 = processString(text);
        setCharacterAttributes(0, text.length(), _defaultStyle, true);
        for(HiliteWord word : list) {
            int p0 = word._position;
            setCharacterAttributes(p0, word._word.length(), _cwStyle, true);
        }
        for(HiliteWord word : list3) {
            int p0 = word._position-2;
            setCharacterAttributes(p0, word._word.length()+2, _cwStyle3, true);
        }
        for(HiliteWord word : list2) {
            int p0 = word._position;
            setCharacterAttributes(p0, word._word.length(), _cwStyle2, true);
        }
    }

    private List<HiliteWord> processString(String content) {
        content= " "+content ;
        boolean on=false;
        List<HiliteWord> hiliteWords = new ArrayList<>();
        int lastIncommaPosition;
        StringBuilder word = new StringBuilder();
        char[] data = content.toCharArray();

        for(int index=0; index < data.length; index++) {
            char ch = data[index];
            if (ch=='\"'&&data[index-1]!='\\') {
                lastIncommaPosition = index;
                if (word.length() > 0) {
                    if (on) {
                        hiliteWords.add(new HiliteWord(word.toString(), (lastIncommaPosition - word.length())));
                    }
                    word = new StringBuilder();
                }
                on=!on;
            } else {
                word.append(ch);
            }
        }
        return hiliteWords;
    }

    private List<HiliteWord> processHeaders(String content) {
        content += "\n";
        List<HiliteWord> hiliteWords = new ArrayList<>();
        int lastHashPosition=-1;
        StringBuilder word = new StringBuilder();
        char[] data = content.toCharArray();

        for(int index=0; index < data.length; index++) {
            char ch = data[index];
            if(ch=='#') {
                lastHashPosition = index;
            }
            else if(ch=='\n'){
                if(word.length() > 0&&lastHashPosition>-1) {
                    word.append(' ');
                    hiliteWords.add(new HiliteWord(word.toString(),lastHashPosition));
                    lastHashPosition=-1;
                    word = new StringBuilder();
                }
            }
            else {
                word.append(ch);
            }
        }
        return hiliteWords;
    }

    private static  List<HiliteWord> processWords(String content) {
        content += " ";
        List<HiliteWord> hiliteWords = new ArrayList<>();
        int lastWhitespacePosition;
        StringBuilder word = new StringBuilder();
        char[] data = content.toCharArray();

        for(int index=0; index < data.length; index++) {
            char ch = data[index];
            if(!(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_')) {
                lastWhitespacePosition = index;
                if(word.length() > 0) {
                    if(isReservedWord(word.toString())) {
                        hiliteWords.add(new HiliteWord(word.toString(),(lastWhitespacePosition - word.length())));
                    }
                    word = new StringBuilder();
                }
            }
            else {
                word.append(ch);
            }
        }
        return hiliteWords;
    }

    private static boolean isReservedWord(String word) {
        return(word.trim().equals("auto")
                ||word.trim().equals("break")
                ||word.trim().equals("case")
                ||word.trim().equals("char")
                ||word.trim().equals("const")
                ||word.trim().equals("continue")
                ||word.trim().equals("default")
                ||word.trim().equals("do")
                ||word.trim().equals("double")
                ||word.trim().equals("else")
                ||word.trim().equals("enum")
                ||word.trim().equals("extern")
                ||word.trim().equals("float")
                ||word.trim().equals("for")
                ||word.trim().equals("goto")
                ||word.trim().equals("if")
                ||word.trim().equals("int")
                ||word.trim().equals("long")
                ||word.trim().equals("register")
                ||word.trim().equals("return")
                ||word.trim().equals("short")
                ||word.trim().equals("signed")
                ||word.trim().equals("sizeof")
                ||word.trim().equals("static")
                ||word.trim().equals("struct")
                ||word.trim().equals("switch")
                ||word.trim().equals("typedef")
                ||word.trim().equals("union")
                ||word.trim().equals("unsigned")
                ||word.trim().equals("void")
                ||word.trim().equals("volatile")
                ||word.trim().equals("while"));
    }
}
