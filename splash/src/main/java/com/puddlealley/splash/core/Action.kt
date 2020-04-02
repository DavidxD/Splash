package com.puddlealley.splash.core

/**
 * An action modifies state.
 */
interface Action

/**
 * An action that is dispatched after a user interaction.
 */
interface Event : Action

/**
 * An action that is created as a side effect to an event.
 */
interface Result : Action

/**
 * Sub classes should be a data class that represents state with in an application.
 */
interface State



