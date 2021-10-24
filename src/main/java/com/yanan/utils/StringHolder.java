package com.yanan.utils;


import java.util.ArrayList;
import java.util.List;

public class StringHolder {
    public static class Token {
    	public static final int STRING = 0;
    	public static final int EXPRESS = 1;
        private String token;
		private String name;
		private String attr;
		private String value;
		private int type;
		private String args;
        @Override
		public String toString() {
			return "Token [token=" + token + ", name=" + name + ", attr=" + attr + ", value=" + value + ", type=" + type
					+ ", args=" + args + "]";
		}
        public Token(String token, String name, String attr,String args,  int type) {
			super();
			this.token = token;
			this.name = name;
			this.attr = attr;
			this.args = args;
			this.type = type;
		}
		
        public Token(String token, String name, int type) {
			super();
			this.token = token;
			this.name = name;
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
		public String getArgs() {
			return args;
		}
		public void setArgs(String args) {
			this.args = args;
		}
    }
    public static String decodeString(String express){
        List<Token> tokenList = decodeTokenList(express);
        return tokenList.toString().replace("],", "\r\n");
    }

    private static List<Token> decodeTokenList(String express) {
    	List<Token> tokenList = new ArrayList<Token>();
    	int start = 0;
		int index = -1;
		while((index = express.indexOf("{",index+1)) != -1  ) {
			if(express.charAt(index-1) == '\\') 
				continue;
			if(index > start)
				tokenList.add(new Token(express.substring(start,index),null,null,null,Token.STRING));
			int endex = index;
			while((endex = express.indexOf("}",endex))!=-1){
				if(express.charAt(endex-1) == '\\') 
					continue;
				break;
			}
			if(endex == -1)
				throw new IllegalArgumentException("express end of '}' ");
			String token = express.substring(index,endex+1);
			String name = token.substring(1,token.length()-1);
			String attr = null;
			String args = null;
			int foundAt = -1;
			for(int i = 1;i < token.length()-1;i++) {
				if(token.charAt(i) == '\\') {
					i++;
					continue;
				}
				if(token.charAt(i)=='@' && foundAt == -1) {
					attr = token.substring(i+1);
					name = token.substring(0,i);
					foundAt = i;
				}
				if(token.charAt(i)==':' && foundAt != -1) {
					attr = token.substring(foundAt+1,i);
					args = token.substring(i+1);
				}
					
			}
			tokenList.add(new Token(token,name,attr,args,Token.EXPRESS));
			start = endex+1;
    	}
		if(start<express.length())
			tokenList.add(new Token(express.substring(start),null,null,null,Token.STRING));
		return tokenList;
	}

	public static void main(String[] args) {
		System.err.println(decodeString("select {column@Resource\\:args1} from {table} where"));
//		System.err.println(decodeString("select"));
//		System.err.println(decodeString("select {column\\@Resource} from {table} where"));
//		System.err.println(decodeString("select {column\\@Resource} from {table}"));
    }
}
