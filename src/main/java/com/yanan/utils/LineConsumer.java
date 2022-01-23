package com.yanan.utils;

import java.io.IOException;

public interface LineConsumer {

	void readLine(String line);

	default void onException(IOException e) {
		e.printStackTrace();
	};

}
