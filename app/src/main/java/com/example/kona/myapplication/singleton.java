package com.example.kona.myapplication;

/**
 * Created by kona on 1.5.2018.
 */

public class singleton {

        private static singleton instance = null;
        protected singleton() {
            // Exists only to defeat instantiation.
        }
        public static singleton getInstance() {
            if(instance == null) {
                instance = new singleton();
            }
            return instance;
        }
    }

