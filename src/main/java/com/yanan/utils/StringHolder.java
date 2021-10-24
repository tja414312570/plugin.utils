package com.yanan.utils;


import java.util.ArrayList;
import java.util.List;

public class StringHolder {
    public static class Token {
    	public static final int STRING = 0;
    	public static final int EXPRESS = 0;
        private String token;
		private String name;
        private String value;
        private String attr;
        @Override
		public String toString() {
			return "Token [token=" + token + ", name=" + name + ", value=" + value + ", attr=" + attr + ", type=" + type
					+ "]";
		}

		private int type;
        public Token(String token, String name, String value, String attr, int type) {
			super();
			this.token = token;
			this.name = name;
			this.value = value;
			this.attr = attr;
			this.type = type;
		}
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
    public static String decodeString(String express){
        List<Token> tokenList = decodeTokenList(express);
        return tokenList.toString();
    }

    private static List<Token> decodeTokenList(String express) {
    	List<Token> tokenList = new ArrayList<Token>();
    	int index = express.indexOf("${");
    	if(index == -1)
    		tokenList.add(new Token(express,null,null,null,Token.STRING));
    	else {
    		int start = 0;
    		while(index != -1) {
    			tokenList.add(new Token(express.substring(start,index),null,null,null,Token.STRING));
    			int endex = express.indexOf("}",index);
    			if(endex == -1)
    				throw new IllegalArgumentException("express end of '}' ");
				tokenList.add(new Token(express.substring(index+2,endex),null,null,null,Token.EXPRESS));
    			start = endex+1;
    			index = express.indexOf("${",index+2);
    			
    		}
    	}
		return tokenList;
	}

	public static void main(String[] args) {
		System.err.println(decodeString("select ${column} from ${table}"));
    }
}
