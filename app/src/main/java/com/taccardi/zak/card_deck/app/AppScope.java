package com.taccardi.zak.card_deck.app;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Application wide scope.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AppScope {
}
