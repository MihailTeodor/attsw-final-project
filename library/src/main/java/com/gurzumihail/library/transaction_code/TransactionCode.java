package com.gurzumihail.library.transaction_code;

import java.util.function.BiFunction;

import com.gurzumihail.library.repository.BookRepository;
import com.gurzumihail.library.repository.UserRepository;

@FunctionalInterface
public interface TransactionCode<T> extends BiFunction<UserRepository, BookRepository, T> {

}
