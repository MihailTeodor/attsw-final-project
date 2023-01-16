package com.gurzumihail.library.transaction_manager;

import com.gurzumihail.library.transaction_code.TransactionCode;

public interface TransactionManager {
	
	<T> T doInTransaction(TransactionCode<T> code) throws RuntimeException;
}

