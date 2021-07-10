import java.io.StringReader;

public class CodeReader  {

    private byte[] strBuffer;
    private int index = 0;
    private byte[] strByte;
    private int len = 0;
    private static final String bracket = "()";
    private static final char rightBracket = ')';
    private static final char leftBracket = '(';
    private static final char stringHead = '"';


    public CodeReader(String str) {
        index=0;
        strByte = str.getBytes();
        len = str.getBytes().length;
        strBuffer = new byte[len];
    }

    private void stripPreffix() {
        if(index==strByte.length){
            return;
        }
        while (Character.isWhitespace(strByte[index])) {
            index++;
            if(index==strByte.length){
                return;
            }
        }
    }

    public boolean hasNext(){
        stripPreffix();
        return index!=strByte.length;
    }

    public String next() {
        int bufferLen = 0;
        boolean isString = false;
        try {
            stripPreffix();
            String ch = new String(new byte[]{strByte[index]},0,1);
            if (bracket.contains(ch)) {
                index++;
                return ch;
            } else {
                if(ch.charAt(0) == stringHead){
                    isString = true;
                }
                if(isString){
                    int tempIndex = index;
                    String str = getString();
                    if(null!=str){
                        return str;
                    }
                    index = tempIndex;
                }
                stripPreffix();
                byte c = strByte[index];
                while (!Character.isWhitespace(c) && rightBracket != c && leftBracket!=c ) {
                    strBuffer[bufferLen] = c;
                    bufferLen++;
                    index++;
                    c = strByte[index];
                }
                return new String(strBuffer, 0, bufferLen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getString(){
        int bufferLen = 0;
        byte c = strByte[index];
        strBuffer[bufferLen] = c;
        bufferLen++;
        index++;
        c = strByte[index];
        while (c != stringHead) {
            strBuffer[bufferLen] = c;
            bufferLen++;
            index++;
            if(index == len){
                return null;
            }
            c = strByte[index];
        }
        strBuffer[bufferLen++] = c;
        index++;
        return new String(strBuffer, 0, bufferLen);
    }

    public static void main(String[] args) {
        System.out.println(Character.isWhitespace('\n'));
    }
}
