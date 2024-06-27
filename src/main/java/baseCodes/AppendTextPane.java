package baseCodes;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class AppendTextPane extends JTextPane {
	JTextPane textpane;
	
//	public AppendTextPane(JTextPane textpane) {
//		this.textpane=textpane;
//	}

	
	public void appendText(String str) {
//		Document doc = getDocument();
		
		StyledDocument doc = getStyledDocument();
        Style style = doc.addStyle("MyStyle", null);
		//for colorful text
		String clrStr = null;
		String actlStr = str;
		int length = str.length();
		StyleConstants.setForeground(style, Color.BLACK);
		if(str.contains("*#")) {
			
		int a = str.indexOf("*#",0);
		System.out.println(a);
		 clrStr= str.substring(0, a);
		System.out.println(clrStr);
		//removing color code
		actlStr= str.substring(a+2, length);
//		System.out.println(actlStr);
		
        if(clrStr.equals("#00FF00")) {
        	StyleConstants.setForeground(style, Color.BLUE);
        }
        if(clrStr.equals("#FF0000")){
        	StyleConstants.setForeground(style, Color.RED);
        }}

        StyleConstants.setFontSize(style, 12);
        StyleConstants.setFontFamily(style, "Arial");
        try {
            doc.insertString(doc.getLength(), "\n"+actlStr, style);
            setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
